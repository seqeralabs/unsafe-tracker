package io.seqera.debug;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class Leaks {

    static final private Map<Long, Context> leaks = new ConcurrentHashMap<>();

    static final private Timer timer = new Timer(true);

    static {
        // dump leaks on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(Leaks::shutdown));

        // register a timer to check periodically
        // Schedule the task to run every 10 seconds
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                dumpSuspectLeaks(Duration.ofMinutes(1));
            }
        }, 1_000, 10_000);  // Initial delay 0 ms, repeat every 10,000 ms (10 seconds)

    }

    static void shutdown() {
        timer.cancel();
        dumpAllLeaks();
    }

    static public void register(Long address, Context context) {
        System.out.printf("^^ tracing: %s; size: %,d; address: %,d; createdAt: %s\n", context.name, context.size, context.address, context.createdAt);
        leaks.put(address, context);
    }

    static public void unregister(Long address) {
        Context context = leaks.remove(address);
        System.out.printf("^^ releasing: %s; size: %,d; address: %,d; createdAt: %s\n", context.name, context.size, context.address, context.createdAt);
    }

    static void dumpAllLeaks() {
        if (leaks.isEmpty()) {
            System.out.println("^^ no leaks detected");
        }

        StringBuilder result = new StringBuilder();
        int count=0;
        for (Context it: leaks.values()) {
            result.append("* LEAK " + (++count) + ": " + it.toString() + "\n");
        }
        System.out.println(result);
    }

    static void dumpSuspectLeaks(Duration duration) {
        int count=0;
        StringBuilder result = new StringBuilder();
        for (Context it: findOlderThan(leaks.values(), Duration.ofMinutes(1))) {
            result.append("* LEAK " + (++count) + ": " + it + "\n");
        }
        System.out.println(result);
    }

    static List<Context> findOlderThan(Collection<Context> all, Duration duration) {
        Instant now = Instant.now();
        List<Context> result = new ArrayList<>(all.size());
        for (Context it: all) {
            if( Duration.between(it.createdAt, now).compareTo(duration)>=0 ) {
                result.add(it);
            }
        }
        return result;
    }
}
