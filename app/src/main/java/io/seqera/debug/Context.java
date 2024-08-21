package io.seqera.debug;

import java.time.Instant;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class Context {

    static final ThreadLocal<Context> instance = new ThreadLocal<>();

    String name;
    long size;
    Instant createdAt;
    String stackTrace;
    long address;

    Context(String name, long size, Instant ts, String stackTrace) {
        this.name = name;
        this.size = size;
        this.createdAt = ts;
        this.stackTrace = stackTrace;
    }

    public Context withAddress(long address) {
        this.address = address;
        return this;
    }

    @Override
    public String toString() {
        return "Context{" +
                "name=" + name +
                ", size=" + size +
                ", address=" + address +
                ", createdAt=" + createdAt +
                ", stackTrace='" + stackTrace + '\'' +
                '}';
    }

    static String dumpStack() {
        StringBuilder result = new StringBuilder();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement s : stack) {
            result.append("  " + s).append('\n');
        }
        return result.toString();
    }

    public static void create(String name, long size) {
        Context context = new Context(name, size, Instant.now(), dumpStack());
        instance.set(context);
    }

    public static Context get() {
        Context result = instance.get();
        instance.remove();
        return result;
    }
}
