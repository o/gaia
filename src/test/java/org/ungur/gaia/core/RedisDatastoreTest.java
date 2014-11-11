package org.ungur.gaia.core;

import org.junit.Test;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Some of the test data providers borrowed from https://github.com/o/simmetrica/blob/master/tests/test_simmetrica.py
 */
public class RedisDatastoreTest {

    private RedisDatastore ds = new RedisDatastore(new JedisPool(new JedisPoolConfig(), "localhost"));

    @Test
    public void testRoundTimestampToResolution() {
        org.junit.Assert.assertEquals(new Long(1363597200), ds.roundTimestampToResolution(1363599249L, 3600L));

        org.junit.Assert.assertEquals(new Long(1415697240), ds.roundTimestampToResolution(1415697244L, 60L));
    }


    @Test
    public void testGetResolution() {
        org.junit.Assert.assertEquals(new Long(3600), ds.getResolution("hour"));

        org.junit.Assert.assertEquals(new Long(86400), ds.getResolution("day"));
    }

    @Test
    public void testGetTimestampsForPush() {
        Map<String, Long> actualTimestamps = ds.getTimestampsForPush(1363707716L);

        org.junit.Assert.assertEquals(new Long(1363707660), actualTimestamps.get("min"));
        org.junit.Assert.assertEquals(new Long(1363707600), actualTimestamps.get("5min"));
        org.junit.Assert.assertEquals(new Long(1363707000), actualTimestamps.get("15min"));
        org.junit.Assert.assertEquals(new Long(1363705200), actualTimestamps.get("hour"));
        org.junit.Assert.assertEquals(new Long(1363651200), actualTimestamps.get("day"));
        org.junit.Assert.assertEquals(new Long(1363219200), actualTimestamps.get("week"));
        org.junit.Assert.assertEquals(new Long(1363392000), actualTimestamps.get("month"));
        org.junit.Assert.assertEquals(new Long(1356048000), actualTimestamps.get("year"));
    }

    @Test
    public void testGetTimestampsForQuery() {
        List<Long> expectedTimestamps = new ArrayList<Long>();

        expectedTimestamps.add(1363707480L);
        expectedTimestamps.add(1363707540L);
        expectedTimestamps.add(1363707600L);
        expectedTimestamps.add(1363707660L);
        expectedTimestamps.add(1363707720L);
        // Simmetrica uses different solution for the fetching last data point https://github.com/o/simmetrica/blob/master/bin/simmetrica-app.py#L109
        expectedTimestamps.add(1363707780L);

        List<Long> actualTimestamps = ds.getTimestampsForQuery(1363707480L, 1363707780L, 60L);

        org.junit.Assert.assertEquals(expectedTimestamps, actualTimestamps);
    }

    @Test
    public void testGetEventKey() {
        org.junit.Assert.assertEquals("gaia:foo:day", ds.getEventKey("foo", "day"));

        org.junit.Assert.assertEquals("gaia:rush:hour", ds.getEventKey("rush", "hour"));
    }


}
