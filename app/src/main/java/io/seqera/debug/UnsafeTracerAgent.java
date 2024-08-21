package io.seqera.debug;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Callable;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

public class UnsafeTracerAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("=== Starting UnsafeTracerAgent premain ===");

        new AgentBuilder.Default()
                .ignore(ElementMatchers.none())
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .type(ElementMatchers.named("sun.misc.Unsafe"))
                .transform((builder, typeDescription, classLoader, module, domain) -> builder
                        .method(ElementMatchers.named("allocateMemory")
                                .and(ElementMatchers.isPublic()))
                        .intercept(MethodDelegation.to(AllocMemWrapper.class)))
                .with(new AgentBuilder.Listener.StreamWriting(System.out))
                .installOn(inst);
    }

    static public class AllocMemWrapper {
        @RuntimeType
        public static Long intercept(@SuperCall Callable<Long> zuper, @Argument(0) long value) throws Exception {
            System.out.println("== Before method with argument: " + value);
            //long result = zuper.call();  // Invoke the original method
            System.out.println("== After method with result: " + 100);
            return 100L;
        }
    }

}
