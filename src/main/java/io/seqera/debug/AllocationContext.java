package io.seqera.debug;

import java.time.Instant;

/**
 * Model a off-heap memory allocation context
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class AllocationContext {

    static final ThreadLocal<AllocationContext> instance = new ThreadLocal<>();

    /**
     * The name of method used to allocate the memory
     */
    String name;

    /**
     * The size of the allocated memoty in bytes
     */
    long size;

    /**
     * The instant when the allocation happened
     */
    Instant createdAt;

    /**
     * The stack trace at the allocation point
     */
    String stackTrace;

    /**
     * The address of the memory allocated
     */
    long address;

    AllocationContext(String name, long size, Instant ts, String stackTrace) {
        this.name = name;
        this.size = size;
        this.createdAt = ts;
        this.stackTrace = stackTrace;
    }

    public AllocationContext withAddress(long address) {
        this.address = address;
        return this;
    }

    @Override
    public String toString() {
        return "AllocationContext{" +
                "name=" + name +
                ", size=" + size +
                ", address=" + address +
                ", createdAt=" + createdAt +
                ", stackTrace='" + stackTrace + '\'' +
                '}';
    }

    public static String dumpStack() {
        StringBuilder result = new StringBuilder();
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i=0; i<stack.length; i++) {
            if( i==0 )
                continue;
            String it = stack[i].toString();
            if( it.startsWith(AllocationContext.class.getName()))
                continue;
            result.append("  " + it).append('\n');
        }
        return result.toString();
    }

    public static void create(String name, long size) {
        AllocationContext context = new AllocationContext(name, size, Instant.now(), dumpStack());
        instance.set(context);
    }

    public static AllocationContext get() {
        AllocationContext result = instance.get();
        instance.remove();
        return result;
    }
}
