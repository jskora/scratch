package jfskora;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSlf4jLogging {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Before
    public void setup() {
        // nothing
    }

    @Test
    public void testError()
    {
        Exception e = new RuntimeException("second");
        log.info("bump");
        log.error("{} <= first and second => {}", this, e);
    }
}
