package jskora.bytecode;

public class BracesOnConditional {

    private transient int i = 0;
    private transient int b = 5;

    private void method1NoBraces() {
        if (i == 0)
            b = b - 1;  // b = -1
    }

    private void method1WithBraces() {
        if (i == 0) {
            b = b - 1;  // b = -1
        }
    }

    private void method2NoBraces() {
        if (i == 0)
            b = b - 1;  // b = -1
        b = b * 2;      // b = -2
    }

    private void method2WithBraces() {
        if (i == 0) {
            b = b - 1;  // b = -1
            b = b * 2;  // b = -2
        }
    }
}
