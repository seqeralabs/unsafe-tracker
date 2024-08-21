package io.seqera.debug.advice;

import io.seqera.debug.AllocationContext;
import io.seqera.debug.LeaksManager;
import net.bytebuddy.asm.Advice;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class AllocateMemAdvice {

    @Advice.OnMethodEnter
    public static void before(@Advice.Argument(0) long size) {
        AllocationContext.create("Unsafe.allocateMemory", size);
    }

    @Advice.OnMethodExit
    public static void after(@Advice.Return long address) {
        LeaksManager.register(address, AllocationContext.get().withAddress(address));
    }

}
