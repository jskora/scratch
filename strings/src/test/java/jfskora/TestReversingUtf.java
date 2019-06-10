package jfskora;

import org.junit.Test;

public class TestReversingUtf {

    @Test
    public void reverseUtfTest() {
        String original = "\u0041" + "\uD835\uDD38" + "BC";

        String reverse1 = "";
        for (int i = original.length() - 1;  0 <= i; i--) {
            reverse1 += original.charAt(i);
        }

        String reverse2 = new StringBuilder(original).reverse().toString();

        System.out.println(original);
        System.out.println(reverse1);
        System.out.println(reverse2);
    }
}
