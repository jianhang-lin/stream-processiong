package work.jianhang.stream.processiong.demo;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import lombok.extern.slf4j.Slf4j;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class WaitOnConcurrentModification implements EventConsumer {

    private final ConcurrentMap<Integer, Lock> clientLocks = new ConcurrentHashMap<>();

    private final EventConsumer downStream;

    private final Timer lockWait;

    public WaitOnConcurrentModification(EventConsumer downStream, MetricRegistry metricRegistry) {
        this.downStream = downStream;
        lockWait = metricRegistry.timer(MetricRegistry.name(WaitOnConcurrentModification.class, "lockWait"));
    }

    @Override
    public Event consume(Event event) {
        try {
            final Lock lock = findClientLock(event);
            final Timer.Context time = lockWait.time();
            try {
                final boolean locked = lock.tryLock(1, TimeUnit.SECONDS);
                time.stop();
                if (locked) {
                    downStream.consume(event);
                }
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            log.warn("Interrupted", e);
        }
        return event;
    }

    private Lock findClientLock(Event event) {
        return clientLocks.computeIfAbsent(event.getClientId(), clientId -> new ReentrantLock());
    }
}
