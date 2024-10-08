package io.seqera.debug;

/**
 * Implement a Java agent able to track usage of off-heap memory made via
 * {@link sun.misc.Unsafe} and {@linl jdk.internal.misc.Unsafe} classes
 *
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

/**
 * The agent entry point
 */
public class UnsafeTracerAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.printf("=== Starting UnsafeTracerAgent version %s === args: %s\n", BuildInfo.getVersion(), agentArgs);

        AgentBuilder agent = new AgentBuilder.Default()
                .ignore(ElementMatchers.none())
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .type(ElementMatchers.named("jdk.internal.misc.Unsafe"))
                .transform( allocMemXform() )
                .transform( freeMemXform() );

        if( agentArgs!=null && agentArgs.contains("debug")) {
            agent = agent.with(new AgentBuilder.Listener.StreamWriting(System.out));
        }

        // finally install the agent
        agent.installOn(inst);
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
