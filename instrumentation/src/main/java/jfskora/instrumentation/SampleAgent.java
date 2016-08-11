package jfskora.instrumentation;

import java.lang.instrument.Instrumentation;

/**
 * Sample agent to add the instrumentation.
 */
public class SampleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println(Thread.currentThread().getStackTrace()[1] + ".premain running");
        inst.addTransformer(new SampleTransformer());
    }
}