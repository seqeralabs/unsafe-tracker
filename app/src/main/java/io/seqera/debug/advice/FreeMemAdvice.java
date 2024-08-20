package io.seqera.debug.advice;

import java.lang.reflect.Method;

import net.bytebuddy.asm.Advice;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
public class FreeMemAdvice {

    @Advice.OnMethodEnter
    public static void before(@Advice.This Object thisObj, @Advice.Origin Method method, @Advice.AllArguments Object[] args) {
        System.err.println("Entering method: " + method + " with arguments: " + java.util.Arrays.toString(args));
    }

    @Advice.OnMethodExit
    public static void after(@Advice.This Object thisObj, @Advice.Origin Method method) {
        System.err.println("Exiting method: " + method + " with void return" );
    }

}
