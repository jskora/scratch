package jfskora;

import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class TestDebugLogging
{
    @Test
    public static void TestDebugLogging()
    {
        Logger logger = Logger.getGlobal();
        logger.info("testing 1 2 3");
        assertTrue("fives", 5==5);
    }
}
