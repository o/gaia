package org.ungur.gaia;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class JedisPoolFactory {

    @NotEmpty
    private String host;

    @Min(1)
    @Max(65535)
    private int port = 6379;

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    public JedisPool build(Environment environment) {
        final JedisPool pool = new JedisPool(new JedisPoolConfig(), getHost(), getPort());
        environment.lifecycle().manage(new JedisPoolManager(pool));
        return pool;
    }
}
