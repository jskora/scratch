package jfskora.javaeight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class LazyStreams {
    public static void main(String[] args) {
        // outputs "1, 1, 1, 1, 2, 2 ,2 ,2" because of lazy evaluation
        Stream.iterate(0, i -> i + 1)
                .flatMap(i -> Stream.of(i, i, i, i))
                .map(i -> i + 1)
                .peek(i -> System.out.println("Map: " + i))
                .limit(5)
                .forEach(i -> {});

        System.out.println();
        System.out.println();

        // outputs "1, 1, 1, 1, 2"
        Stream.iterate(0, i -> i + 1)
                .flatMap(i -> Stream.of(i, i, i, i))
                .limit(5)
                .map(i -> i + 1)
                .peek(i -> System.out.println("Map: " + i))
                .forEach(i -> {});
    }
}
