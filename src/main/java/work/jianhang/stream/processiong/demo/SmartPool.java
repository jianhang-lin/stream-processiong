package work.jianhang.stream.processiong.demo;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SmartPool implements EventConsumer, Closeable {

    private final List<LinkedBlockingQueue<Runnable>> queues;

    private final List<ExecutorService> threadPools;

    private final EventConsumer downStream;

    public SmartPool(int size, EventConsumer downStream, MetricRegistry metricRegistry) {
        this.downStream = downStream;
        this.queues = IntStream.range(0, size).mapToObj(i -> new LinkedBlockingQueue<Runnable>()).collect(Collectors.toList());
        List<ThreadPoolExecutor> list = queues.stream().map(q -> new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS, q)).collect(Collectors.toList());
        this.threadPools = new CopyOnWriteArrayList<>(list);
        metricRegistry.register(MetricRegistry.name(ProjectionMetrics.class, "queue"), (Gauge<Double>) this::averageQueueLength);
    }

    private double averageQueueLength() {
        double totalLength = queues.stream().mapToDouble(LinkedBlockingQueue::size).sum();
        return totalLength / queues.size();
    }

    @Override
    public void close() throws IOException {
        threadPools.forEach(ExecutorService::shutdown);
    }

    @Override
    public Event consume(Event event) {
        final int threadIndx = event.getClientId() % threadPools.size();
        final ExecutorService executorService = threadPools.get(threadIndx);
        executorService.submit(() -> downStream.consume(event));
        return event;
    }
}
