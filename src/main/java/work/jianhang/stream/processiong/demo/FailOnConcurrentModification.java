package work.jianhang.stream.processiong.demo;

import lombok.extern.slf4j.Slf4j;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class FailOnConcurrentModification implements EventConsumer {

    private final ConcurrentMap<Integer, Lock> clientLocks = new ConcurrentHashMap<>();

    private final EventConsumer downStream;

    public FailOnConcurrentModification(EventConsumer downStream) {
        this.downStream = downStream;
    }

    @Override
    public Event consume(Event event) {
        Lock lock = null;
        if (lock.tryLock()) {
            try {
                downStream.consume(event);
            } finally {
                lock.unlock();
            }
        } else {
            log.error("Client {} already being modified by another thread", event.getClientId());
        }
        return event;
    }

    private Lock findClientLock(Event event) {
        return clientLocks.computeIfAbsent(event.getClientId(), clientId -> new ReentrantLock());
    }
}
