package work.jianhang.stream.processiong.demo;

import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NaivePool implements EventConsumer, Closeable {

    private final EventConsumer downStream;
    private final ExecutorService executorService;

    public NaivePool(int size, EventConsumer downStream) {
        this.executorService = Executors.newFixedThreadPool(size);
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
