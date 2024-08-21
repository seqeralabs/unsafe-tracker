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
 * Implement the logic to track and monitor the release of memory allocations
 * 
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class LeaksManager {

    static final private Map<Long, AllocationContext> leaks = new ConcurrentHashMap<>();

    static final private Timer timer = new Timer(true);

    static {
        // dump leaks on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(LeaksManager::shutdown));

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

    static public void register(Long address, AllocationContext context) {
        System.out.printf("^^ tracing: %s; size: %,d; address: %,d; createdAt: %s\n", context.name, context.size, context.address, context.createdAt);
        leaks.put(address, context);
    }

    static public void unregister(Long address) {
        AllocationContext context = leaks.remove(address);
        System.out.printf("^^ releasing: %s; size: %,d; address: %,d; createdAt: %s\n", context.name, context.size, context.address, context.createdAt);
    }

    static void dumpAllLeaks() {
        if (leaks.isEmpty()) {
            System.out.println("^^ no leaks detected");
        }

        StringBuilder result = new StringBuilder();
        int count=0;
        for (AllocationContext it: leaks.values()) {
            result.append("* LEAK " + (++count) + ": " + it.toString() + "\n");
        }
        System.out.println(result);
    }

    static void dumpSuspectLeaks(Duration duration) {
        int count=0;
        StringBuilder result = new StringBuilder();
        for (AllocationContext it: findOlderThan(leaks.values(), Duration.ofMinutes(1))) {
            result.append("* LEAK " + (++count) + ": " + it + "\n");
        }
        System.out.println(result);
    }

    static List<AllocationContext> findOlderThan(Collection<AllocationContext> all, Duration duration) {
        Instant now = Instant.now();
        List<AllocationContext> result = new ArrayList<>(all.size());
        for (AllocationContext it: all) {
            if( Duration.between(it.createdAt, now).compareTo(duration)>=0 ) {
                result.add(it);
            }
        }
        return result;
    }
}