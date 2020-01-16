package work.jianhang.stream.processiong.demo;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author grant
 */
@Slf4j
public class Sleeper {

    private static final Random RANDOM = new Random();

    static void randSleep(double mean, double stdDev) {
        //log.info("Sleeper randSleep...");
        final double micros = 1_000 * (mean + RANDOM.nextGaussian() * stdDev);
        try {
            TimeUnit.MICROSECONDS.sleep((long) micros);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
