package dynamic;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IMUL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

/*
https://dzone.com/articles/fully-dynamic-classes-with-asm
 */

public class DynamicClassTest {

    static int[] array = new int[15];

    public static void loop1() {
        int length = array.length;
        for (int i = 0; i < length; i++)
            array[i]--;
    }

    public static class LocalDynamicClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_8,                              // Java 1.8
                ACC_PUBLIC,                         // public class
                "dynamic/DynamicCalculatorImpl",    // package and name
                null,                               // signature (null means not generic)
                "java/lang/Object",                 // superclass
                new String[]{"dynamic/Calculator"}); // interfaces

        /* Build constructor */
        MethodVisitor con = cw.visitMethod(
                ACC_PUBLIC,                         // public method
                "<init>",                           // method name
                "()V",                              // descriptor
                null,                               // signature (null means not generic)
                null);                              // exceptions (array of strings)
        con.visitCode();                            // Start the code for this method
        con.visitVarInsn(ALOAD, 0);                 // Load "this" onto the stack
        con.visitMethodInsn(INVOKESPECIAL,          // Invoke an instance method (non-virtual)
                "java/lang/Object",                 // Class on which the method is defined
                "<init>",                           // Name of the method
                "()V",                              // descriptor
                false);                             // Is this class an interface?
        con.visitInsn(RETURN);                      // End the constructor method
        con.visitMaxs(1, 1);

        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,                         // public method
                "add",                              // name
                "(II)I",                            // descriptor
                null,                               // signature (null means not generic)
                null);                              // exceptions (array of strings)
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 1);                  // Load int value onto stack
        mv.visitVarInsn(ILOAD, 2);                  // Load int value onto stack
        mv.visitInsn(IADD);                         // Integer add from stack and push to stack
        mv.visitInsn(IRETURN);                      // Return integer from top of stack
        mv.visitMaxs(2, 3);                         // Specify max stack and local vars

        mv = cw.visitMethod(
                ACC_PUBLIC,                         // public method
                "multiply",                              // name
                "(II)I",                            // descriptor
                null,                               // signature (null means not generic)
                null);                              // exceptions (array of strings)
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 1);                  // Load int value onto stack
        mv.visitVarInsn(ILOAD, 2);                  // Load int value onto stack
        mv.visitInsn(IMUL);                         // Integer add from stack and push to stack
        mv.visitInsn(IRETURN);                      // Return integer from top of stack
        mv.visitMaxs(2, 3);                         // Specify max stack and local vars

        cw.visitEnd();

        LocalDynamicClassLoader loader = new LocalDynamicClassLoader();
        Class<?> clazz = loader.defineClass("dynamic.DynamicCalculatorImpl", cw.toByteArray());
        System.out.println(clazz.getName());
        Calculator calc = (Calculator) clazz.newInstance();
        System.out.println("2 + 2 = " + calc.add(2, 2));
        System.out.println("2 + 3 = " + calc.add(2, 3));
        System.out.println("3 + 3 = " + calc.add(3, 3));
        System.out.println("2 * 2 = " + calc.multiply(2, 2));
        System.out.println("2 * 3 = " + calc.multiply(2, 3));
        System.out.println("3 * 3 = " + calc.multiply(3, 3));
    }
}
