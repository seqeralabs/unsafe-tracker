package io.seqera.debug.advice;

import io.seqera.debug.AllocationContext;
import io.seqera.debug.LeaksManager;
import net.bytebuddy.asm.Advice;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class AllocateMemAdvice {

    @Advice.OnMethodEnter
    public static long before(@Advice.Argument(0) long size) {
        // Return the size so it can be captured by @Enter in @OnMethodExit
        return size;
    }

    @Advice.OnMethodExit
    public static void after(@Advice.Return long address, @Advice.Enter long size) {
        AllocationContext c = AllocationContext.create("Unsafe.allocateMemory", size, address);
        LeaksManager.register(address, c);
    }

}
