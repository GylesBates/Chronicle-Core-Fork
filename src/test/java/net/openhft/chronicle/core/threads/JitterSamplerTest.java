package net.openhft.chronicle.core.threads;

import net.openhft.chronicle.core.Jvm;
import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JitterSamplerTest {

    @Test
    public void takeSnapshot() throws InterruptedException {

        Assume.assumeTrue(!Jvm.isArm());
        Thread t = new Thread(() -> {
            JitterSampler.atStage("started");
            JitterSampler.sleepSilently(60);
            JitterSampler.atStage("finishing");
            JitterSampler.sleepSilently(60);
            JitterSampler.finished();
        });
        t.start();
        Jvm.pause(20);
        for (int i = 0; i < 10; i++) {
            JitterSampler.sleepSilently(10);
            String s = JitterSampler.takeSnapshot(10_000_000);
            //System.out.println(s);
            if ("finishing".equals(JitterSampler.desc)) {
                if (s != null && s.contains("finish"))
                    break;
            } else {
                assertEquals("started", JitterSampler.desc);
            }
        }
        t.join();
        String s = JitterSampler.takeSnapshot();
        assertNull(s);
    }
}