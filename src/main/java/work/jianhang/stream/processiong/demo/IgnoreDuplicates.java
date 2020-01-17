package work.jianhang.stream.processiong.demo;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class IgnoreDuplicates implements EventConsumer {

    private final EventConsumer downStream;

    private final Meter duplicates;

    private Cache<UUID, UUID> seenUuids = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    public IgnoreDuplicates(EventConsumer downStream, MetricRegistry metricRegistry) {
        this.downStream = downStream;
        duplicates = metricRegistry.meter(MetricRegistry.name(IgnoreDuplicates.class, "duplicates"));
        metricRegistry.register(MetricRegistry.name(IgnoreDuplicates.class, "cacheSize"), (Gauge<Long>) seenUuids::size);
    }

    @Override
    public Event consume(Event event) {
        final UUID uuid = event.getUuid();
        if (seenUuids.asMap().putIfAbsent(uuid, uuid) == null) {
            return downStream.consume(event);
        } else {
            duplicates.mark();
            return event;
        }
    }
}
