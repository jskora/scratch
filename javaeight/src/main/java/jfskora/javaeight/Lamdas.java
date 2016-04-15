package jfskora.javaeight;

import org.junit.Test;

import java.util.function.Function;

import static java.lang.Math.*;

public class Lamdas
{
    @Test
    public void simple() {
        Function<Long, Long> square = (Long x) -> x * x;
        System.out.print("square(5)=");
        System.out.println(square.apply(5L));
    }

    @Test
    public void method() {
        Function<Long, Long> square1 = (Long x) -> (long)pow(x + 5, 2);
        Function<Long, Long> square2 = (Long x) -> {
            Long temp = x + 5;
            System.out.println("  interim=" + temp);
            return temp * temp;
        };
        System.out.println("square1(5)=" + square1.apply(5L));
        System.out.println("square2(5)=" + square2.apply(5L));
    }
}
