package work.jianhang.stream.processiong.demo;

import lombok.extern.slf4j.Slf4j;
import work.jianhang.stream.processiong.core.Event;
import work.jianhang.stream.processiong.core.EventConsumer;

/**
 * @author grant
 */
@Slf4j
public class ClientProjection implements EventConsumer {

    @Override
    public Event consume(Event event) {
        log.info("ClientProjection consume... clientId = {}, uuid = {}", event.getClientId(), event.getUuid());
        Sleeper.randSleep(10, 1);
        return event;
    }

}
