package work.jianhang.stream.processiong.demo;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.*;

public class NaivePool implements EventConsumer, Closeable {

    private final EventConsumer downStream;
    private final ExecutorService executorService;

    public NaivePool(int size, EventConsumer downStream, MetricRegistry metricRegistry) {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        String name = MetricRegistry.name(ProjectionMetrics.class, "queue");
        Gauge<Integer> gauge = queue::size;
        metricRegistry.register(name, gauge);
        this.executorService = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MICROSECONDS, queue);
        this.downStream = downStream;
    }

    @Override
    public void close() throws IOException {
        executorService.shutdown();
    }

    @Override
    public Event consume(Event event) {
        executorService.submit(() -> downStream.consume(event));
        return event;
    }
}
