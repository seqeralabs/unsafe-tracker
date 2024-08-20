package io.seqera.debug;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */

import java.lang.instrument.Instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

public class UnsafeTracerAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("=== Starting UnsafeTracerAgent premain ===");
        new AgentBuilder.Default()
                .ignore(ElementMatchers.none())
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .type(ElementMatchers.named("sun.misc.Unsafe"))
                .transform((builder, typeDescription, classLoader, module, domain) -> {
                    System.err.println("Transforming: " + typeDescription.getName() );
                    return builder
                            .visit(Advice.to(MyAdvice.class)
                                    .on(ElementMatchers.any()));
                })
                .installOn(inst);
    }

    static public class MyAdvice {

        @Advice.OnMethodEnter
        public static void onEnter(@Advice.Origin String method, @Advice.AllArguments Object[] args) {
            System.err.println("Entering method: " + method + " with arguments: " + java.util.Arrays.toString(args));
        }

        @Advice.OnMethodExit
        public static void onExit(@Advice.Origin String method, @Advice.Return Object returnValue) {
            System.err.println("Exiting method: " + method + " with return value: " + returnValue);
        }
    }
}
