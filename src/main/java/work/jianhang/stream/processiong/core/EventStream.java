package work.jianhang.stream.processiong.core;

public interface EventStream {

    void consume(EventConsumer eventConsumer);
}
