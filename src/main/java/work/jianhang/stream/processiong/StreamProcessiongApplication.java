package work.jianhang.stream.processiong;

import com.codahale.metrics.MetricRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;
import work.jianhang.stream.processiong.core.EventStream;
import work.jianhang.stream.processiong.demo.*;

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
                for (int i=0;i<50;i++) {
                    Event event = new Event(new Random().nextInt(10), UUID.randomUUID());
                    eventConsumer.consume(event);
                }
            }
        };

        MetricRegistry metricRegistry = new MetricRegistry();
        ProjectionMetrics metrics = new ProjectionMetrics(metricRegistry);



        // 3 Finally ClientProjection is invoked that does the real business logic.
        ClientProjection clientProjection = new ClientProjection(metrics);
        FailOnConcurrentModification failOnConcurrentModification = new FailOnConcurrentModification(clientProjection);
        //NaivePool naivePool = new NaivePool(10, failOnConcurrentModification, metricRegistry);

        // 2 Then we call SmartPool that always pins given clientId to the same thread and executes next stage in that thread
        SmartPool smartPool = new SmartPool(12, failOnConcurrentModification, metricRegistry);

        // 1 First we apply IgnoreDuplicates to reject duplicates
        IgnoreDuplicates withoutDuplicates = new IgnoreDuplicates(smartPool, metricRegistry);
        eventStream.consume(withoutDuplicates);

    }

}
