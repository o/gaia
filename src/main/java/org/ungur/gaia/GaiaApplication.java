package org.ungur.gaia;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.ungur.gaia.core.RedisDatastore;
import org.ungur.gaia.health.JedisPoolHealthCheck;
import org.ungur.gaia.resources.EventResource;
import redis.clients.jedis.JedisPool;

public class GaiaApplication extends Application<GaiaConfiguration> {

    public static void main(String[] args) throws Exception {
        new GaiaApplication().run(args);
    }

    @Override
    public String getName() {
        return "gaia";
    }

    @Override
    public void initialize(Bootstrap<GaiaConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(GaiaConfiguration configuration,
                    Environment environment) {
        JedisPool pool = configuration.getJedisPoolFactory().build(environment);
        environment.healthChecks().register("jedis-pool", new JedisPoolHealthCheck(pool));

        RedisDatastore redisDatastore = new RedisDatastore(pool);
        final EventResource resource = new EventResource(redisDatastore);
        environment.jersey().register(resource);
    }

}
