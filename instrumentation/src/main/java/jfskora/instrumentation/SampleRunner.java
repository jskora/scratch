package jfskora.instrumentation;

/**
 * Sample executable class to run the instrumented Sample class.
 */
public class SampleRunner {
    public static void main(String args[]) throws InterruptedException {
        Sample l = new Sample();
        l.execute();
    }
}