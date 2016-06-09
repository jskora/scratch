package jfskora;

import org.junit.Test;

public class Continuations {

    interface Cont {
        int k(int v);
    }

    @Test
    public void continuationFib() {
        String res = "";
        int arr[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 10};
        for (int elem : arr) {
            res += (fib(elem,
                    new Cont() {
                        public int k(int v) {
                            return v;
                        }
                    }
            )) + ", ";
        }
        System.out.println("First 10 fibonacci numbers: " + res);
    }

    private static int fib(final int n, final Cont cont) {
        if (n <= 1) {
            return cont.k(1);
        } else {
            return fib(n - 1,
                    new Cont() {
                        public int k(final int ret) {
                            return fib(n - 2, new Cont() {
                                public int k(int ret2) {
                                    return cont.k(ret + ret2);
                                }
                            });
                        }
                    }
            );
        }
    }
}
