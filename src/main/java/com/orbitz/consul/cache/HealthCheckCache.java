package com.orbitz.consul.cache;

import com.google.common.primitives.Ints;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.option.QueryOptions;

import java.util.function.Function;

public class HealthCheckCache extends ConsulCache<String, HealthCheck> {

    private HealthCheckCache(Function<HealthCheck, String> keyConversion, CallbackConsumer<HealthCheck> callbackConsumer) {
        super(keyConversion, callbackConsumer);
    }

    /**
     * Factory method to construct a string/{@link HealthCheck} map for a particular {@link com.orbitz.consul.model.State}.
     * <p/>
     * Keys will be the {@link HealthCheck#getCheckId()}.
     *
     * @param healthClient the {@link HealthClient}
     * @param state        the state fo filter checks
     * @return a cache object
     */
    public static HealthCheckCache newCache(
            final HealthClient healthClient,
            final com.orbitz.consul.model.State state,
            final int watchSeconds,
            final QueryOptions queryOptions,
            Function<HealthCheck, String> keyExtractor) {

        return new HealthCheckCache(keyExtractor, (index, callback) -> {
            QueryOptions params = watchParams(index, watchSeconds, queryOptions);
            healthClient.getChecksByState(state, params, callback);
        });
    }

    public static HealthCheckCache newCache(
            final HealthClient healthClient,
            final com.orbitz.consul.model.State state,
            final int watchSeconds,
            final QueryOptions queryOptions) {

        return newCache(healthClient, state, watchSeconds, queryOptions, HealthCheck::getCheckId);
    }

    public static HealthCheckCache newCache(
            final HealthClient healthClient,
            final com.orbitz.consul.model.State state,
            final int watchSeconds) {
        return newCache(healthClient, state, watchSeconds, QueryOptions.BLANK);
    }

    public static HealthCheckCache newCache(final HealthClient healthClient, final com.orbitz.consul.model.State state) {
        return newCache(healthClient, state, Ints.checkedCast(CacheConfig.get().getWatchDuration().getSeconds()));
    }

}
