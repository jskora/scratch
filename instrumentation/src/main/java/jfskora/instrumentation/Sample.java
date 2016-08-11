package jfskora.instrumentation;

/**
 * Sample class for instrumentation.
 */
class Sample {
    void execute() throws InterruptedException {
        System.out.println("Sample going to sleep for 2000 ms");
        Thread.sleep(2000L);
    }

}