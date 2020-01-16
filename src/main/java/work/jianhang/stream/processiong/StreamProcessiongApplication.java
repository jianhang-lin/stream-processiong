package work.jianhang.stream.processiong;

import com.codahale.metrics.MetricRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;
import work.jianhang.stream.processiong.core.EventStream;
import work.jianhang.stream.processiong.demo.NaivePool;
import work.jianhang.stream.processiong.demo.ClientProjection;
import work.jianhang.stream.processiong.demo.ProjectionMetrics;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@Slf4j
public class StreamProcessiongApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamProcessiongApplication.class, args);

        runEventStream();
    }

    static void runEventStream() {
        EventStream eventStream = new EventStream() {
            @Override
            public void consume(EventConsumer eventConsumer) {
                //log.info("runEventStream...");
                Event event = new Event(new Random().nextInt(), UUID.randomUUID());
                eventConsumer.consume(event);
            }
        };

        MetricRegistry metricRegistry = new MetricRegistry();
        ProjectionMetrics metrics = new ProjectionMetrics(metricRegistry);

        ClientProjection clientProjection = new ClientProjection(metrics);
        NaivePool naivePool = new NaivePool(10, clientProjection, metricRegistry);

        eventStream.consume(naivePool);

    }

}
