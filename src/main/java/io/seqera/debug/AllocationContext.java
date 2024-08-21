package io.seqera.debug;

import java.time.Instant;

/**
 * Model a off-heap memory allocation context
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class AllocationContext {

    /**
     * The name of method used to allocate the memory
     */
    final String name;

    /**
     * The size of the allocated memoty in bytes
     */
    final long size;

    /**
     * The instant when the allocation happened
     */
    final Instant createdAt;

    /**
     * The stack trace at the allocation point
     */
    final String stackTrace;

    /**
     * The address of the memory allocated
     */
    final long address;

    AllocationContext(String name, long size, long address, Instant ts, String stackTrace) {
        this.name = name;
        this.size = size;
        this.address = address;
        this.createdAt = ts;
        this.stackTrace = stackTrace;
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

    public static AllocationContext create(String name, long size, long address) {
        return new AllocationContext(
                name,
                size,
                address,
                Instant.now(),
                dumpStack());
    }

}
