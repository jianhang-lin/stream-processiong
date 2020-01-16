package work.jianhang.stream.processiong;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;
import work.jianhang.stream.processiong.core.EventStream;
import work.jianhang.stream.processiong.demo.ClientProjection;

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
                log.info("runEventStream...");
                Event event = new Event(1, UUID.randomUUID());
                eventConsumer.consume(event);
            }
        };

        eventStream.consume(new ClientProjection());
    }

}
