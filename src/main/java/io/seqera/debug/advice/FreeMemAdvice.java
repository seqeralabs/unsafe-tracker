package io.seqera.debug.advice;

import io.seqera.debug.LeaksManager;
import net.bytebuddy.asm.Advice;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class FreeMemAdvice {

    @Advice.OnMethodEnter
    public static void before(@Advice.Argument(0) long address) {
        LeaksManager.unregister(address);
    }

}
