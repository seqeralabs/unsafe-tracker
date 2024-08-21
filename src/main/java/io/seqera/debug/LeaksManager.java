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

    static final long _10_MB = 10 * 1024 * 1024;

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
                dumpSuspectLeaks(Duration.ofMinutes(5), 1024 * 1024);
            }
        }, 1_000, 10_000);  // Initial delay 0 ms, repeat every 10,000 ms (10 seconds)

    }

    static void shutdown() {
        timer.cancel();
        dumpAllLeaks();
    }

    static public void register(Long address, AllocationContext context) {
        if( context.size>_10_MB )
            System.out.printf("^^ tracing: %s; size: %,d; address: %,d; createdAt: %s\n", context.name, context.size, context.address, context.createdAt);
        leaks.put(address, context);
    }

    static public void unregister(Long address) {
        AllocationContext context = leaks.remove(address);
        if( context != null ) {
            if( context.size>_10_MB )
                System.out.printf("^^ releasing: %s; size: %,d; address: %,d; createdAt: %s\n", context.name, context.size, context.address, context.createdAt);
        }
        else {
            System.out.printf("^^ releasing untracked address: %,d; stack: %s\n", address, AllocationContext.dumpStack());
        }
    }

    static void dumpAllLeaks() {
        if (leaks.isEmpty()) {
            System.out.println("^^ no leaks detected");
            return;
        }

        StringBuilder result = new StringBuilder();
        int count=0;
        List<AllocationContext> list = new ArrayList<>(leaks.values());
        for (AllocationContext it: list) {
            result.append("* LEAK " + (++count) + ": " + it.toString() + "\n");
        }

        System.out.println(summary(list, 0));
        System.out.println(result);
    }

    static void dumpSuspectLeaks(Duration duration, int size) {
        int count=0;
        StringBuilder result = new StringBuilder();
        final List<AllocationContext> copy = new ArrayList<>(leaks.values());
        for (AllocationContext it: findOlderThan(copy, duration)) {
            if( size==0 || it.size>size ) {
                result.append("* LEAK " + (++count) + ": " + it + "\n");
            }
        }

        int omitted = copy.size()-count;
        System.out.println(summary(copy, omitted));
        System.out.println(result);
    }

    static String summary(List<AllocationContext> list, int omitted) {
        long total=0;
        for (AllocationContext it : list) {
            total += it.size;
        }

        return String.format("* LEAKS SUMMARY: %,d suspect leaks; %,d omitted; %,d retained memory", list.size(), omitted, total);
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
