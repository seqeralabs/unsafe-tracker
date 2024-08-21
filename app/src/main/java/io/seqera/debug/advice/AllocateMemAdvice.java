package io.seqera.debug.advice;

import io.seqera.debug.Context;
import io.seqera.debug.Leaks;
import net.bytebuddy.asm.Advice;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class AllocateMemAdvice {

    @Advice.OnMethodEnter
    public static void before(@Advice.Argument(0) long size) {
        Context.create("Unsafe.allocateMemory", size);
    }

    @Advice.OnMethodExit
    public static void after(@Advice.Return long address) {
        Leaks.register(address, Context.get().withAddress(address));
    }

}
