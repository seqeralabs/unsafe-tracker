package io.seqera.debug;

/**
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */

import java.lang.instrument.Instrumentation;

import io.seqera.debug.advice.AllocateMemAdvice;
import io.seqera.debug.advice.FreeMemAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.matcher.ElementMatchers;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class UnsafeTracerAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.err.println("=== Starting UnsafeTracerAgent premain ===");

        new AgentBuilder.Default()
                .ignore(ElementMatchers.none())
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .type(ElementMatchers.named("sun.misc.Unsafe")
                        .or(ElementMatchers.named("jdk.internal.misc.Unsafe")))
                .transform( allocMemXform() )
                .transform( freeMemXform() )
                .installOn(inst);
    }

    static AgentBuilder.Transformer allocMemXform() {
        AsmVisitorWrapper.ForDeclaredMethods adv = Advice.to(AllocateMemAdvice.class) .on(named("allocateMemory").and(isPublic()));
        return (builder, typeDescription, classLoader, module, domain) -> builder.visit(adv);
    }

    static AgentBuilder.Transformer freeMemXform() {
        AsmVisitorWrapper.ForDeclaredMethods adv = Advice.to(FreeMemAdvice.class) .on(named("freeMemory").and(isPublic()));
        return (builder, typeDescription, classLoader, module, domain) -> builder.visit(adv);
    }

}
