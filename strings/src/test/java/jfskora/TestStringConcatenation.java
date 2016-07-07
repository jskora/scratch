package jfskora;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestStringConcatenation {

    private static final long MAX_CONCATS = 50000;

    private Map<String, Long> times = new HashMap<>();

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    @Rule
    public TestName name = new TestName();

    @Before
    public void before() {
        System.out.println(sdf.format(new Date(System.currentTimeMillis())));
        times.put("before", System.nanoTime());
        System.out.printf("%s\n   before=%d\n", name.getMethodName(), times.get("before"));
    }

    @After
    public void after() {
        times.put("after", System.nanoTime());
        System.out.printf("   after=%d\n   duration=%d nanos\n",
                times.get("after"), times.get("after") - times.get("before"));
        System.out.println(sdf.format(new Date(System.currentTimeMillis())));
        System.out.println("");
    }

    @Test
    public void nullConcatEmpty() {
        String temp1 = "string1";
        String temp2 = "";
        String temp3 = "";
        for (long n = MAX_CONCATS; n > 0; n--) {
            temp3 = temp1 + temp2;
        }
        assertTrue(true);
    }

    @Test
    public void nullCheckNullIsNull() {
        String temp1 = "string1";
        String temp2 = null;
        String temp3;
        for (long n = MAX_CONCATS; n > 0; n--) {
            if (temp2 == null) {
                temp3 = temp1;
            } else {
                temp3 = temp1 + temp2;
            }
        }
        assertTrue(true);
    }

    @Test
    public void nullCheckNullIsNotNull() {
        String temp1 = "string1";
        String temp2 = "bob";
        String temp3;
        for (long n = MAX_CONCATS; n > 0; n--) {
            if (temp2 == null) {
                temp3 = temp1;
            } else {
                temp3 = temp1 + temp2;
            }
        }
        assertTrue(true);
    }
}
