package org.ungur.gaia;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class GaiaConfiguration extends Configuration {

    @Valid
    @NotNull
    private JedisPoolFactory jedisPoolFactory = new JedisPoolFactory();

    @JsonProperty("redis")
    public JedisPoolFactory getJedisPoolFactory() {
        return jedisPoolFactory;
    }

    @JsonProperty("redis")
    public void setJedisPoolFactory(JedisPoolFactory jedisPoolFactory) {
        this.jedisPoolFactory = jedisPoolFactory;
    }
}
