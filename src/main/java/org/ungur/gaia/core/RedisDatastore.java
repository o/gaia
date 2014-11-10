package org.ungur.gaia.core;

import com.google.common.collect.ImmutableMap;
import org.ungur.gaia.dao.Event;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RedisDatastore implements DatastoreInterface {

    static final Map<String, Long> RESOLUTIONS =
            new ImmutableMap.Builder<String, Long>()
                    .put("min", 60L)
                    .put("5min", 300L)
                    .put("hour", 3600L)
                    .put("day", 86400L)
                    .put("week", 604800L)
                    .put("month", 2592000L)
                    .put("year", 31536000L)
                    .build();

    private JedisPool pool;

    public RedisDatastore(JedisPool pool) {
        this.pool = pool;
    }

    private Long roundTimestampToResolution(Long timeAsMilliSeconds, Long resolution) {
        return (timeAsMilliSeconds - (timeAsMilliSeconds % resolution));
    }

    private Map<String, Long> getTimestampsForPush(Long timestamp) {
        Map<String, Long> timestamps = new HashMap<String, Long>();

        for (Entry<String, Long> entry : RESOLUTIONS.entrySet()) {
            timestamps.put(entry.getKey(), roundTimestampToResolution(timestamp, entry.getValue()));
        }

        return timestamps;
    }

    private List<Long> getTimestampsForQuery(Long start, Long end, Long resolution) {
        List<Long> timestamps = new ArrayList<Long>();

        final Long roundedStartTimestamp = roundTimestampToResolution(start, resolution);
        final Long roundedEndTimestamp = roundTimestampToResolution(end, resolution);

        for (Long timestamp = roundedStartTimestamp; timestamp <= roundedEndTimestamp; timestamp = timestamp + resolution) {
            timestamps.add(timestamp);
        }

        return timestamps;
    }

    private String getEventKey(String event, String resolution) {
        return new StringBuilder("gaia:").append(event).append(":").append(resolution).toString();
    }

    @Override
    public boolean push(String event, Long increment, Long timestamp) {
        try (Jedis jedis = pool.getResource()) {
            Pipeline p = jedis.pipelined();

            for (Entry<String, Long> entry : getTimestampsForPush(timestamp).entrySet()) {
                p.hincrBy(getEventKey(event, entry.getKey()), entry.getValue().toString(), increment);
            }

            p.sync();
            return true;
        }
    }

    @Override
    public List<Event> query(String event, Long start, Long end, String resolution) {
        List<Long> timestamps = getTimestampsForQuery(start, end, RESOLUTIONS.get(resolution));
        List<Event> result = new ArrayList<Event>();
        String eventKey = getEventKey(event, resolution);
        try (Jedis jedis = pool.getResource()) {
            for (Long timestamp : timestamps) {
                if (jedis.hexists(eventKey, timestamp.toString())) {
                    result.add(new Event(timestamp, Long.valueOf(jedis.hget(eventKey, timestamp.toString()))));
                } else {
                    result.add(new Event(timestamp, 0L));
                }
            }

            return result;
        }
    }

}
