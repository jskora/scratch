package jfskora;

import java.util.Arrays;

public class PrintProps {
    public static void main(String[] args) {
        final Object[] keys = System.getProperties().keySet().toArray();
        Arrays.sort(keys);
        for (Object key : keys) {
            System.out.println(key + "=" + System.getProperties().get(key));
        }
    }
}