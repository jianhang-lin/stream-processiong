package work.jianhang.stream.processiong.core;

@FunctionalInterface
public interface EventConsumer {

    Event consume(Event event);

}
