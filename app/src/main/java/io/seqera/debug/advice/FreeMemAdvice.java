package io.seqera.debug.advice;

import io.seqera.debug.Leaks;
import net.bytebuddy.asm.Advice;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class FreeMemAdvice {

    @Advice.OnMethodEnter
    public static void before(@Advice.Argument(0) long address) {
        Leaks.unregister(address);
    }

}
