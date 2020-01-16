package work.jianhang.stream.processiong.demo;

import lombok.extern.slf4j.Slf4j;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;

import java.time.Duration;
import java.time.Instant;

/**
 * @author grant
 */
@Slf4j
public class ClientProjection implements EventConsumer {

    private final ProjectionMetrics metrics;

    public ClientProjection(ProjectionMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public Event consume(Event event) {
        //log.info("ClientProjection consume... clientId = {}, uuid = {}", event.getClientId(), event.getUuid());
        metrics.latency(Duration.between(event.getCreated(), Instant.now()));
        Sleeper.randSleep(10, 1);
        return event;
    }

}
