package jfskora.instrumentation;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Sample transformer to rewrite class code before it's loaded.
 */
class SampleTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className, Class klass, ProtectionDomain domain, byte[] buffer) throws IllegalClassFormatException {
        byte[] byteCode = buffer;

        // filtering on classname (optionally)
        System.out.println("Sample=" + Sample.class.getName());
        if (className.equals("jfskora/instrumentation/Sample")) {
            System.out.println("Instrumenting class: " + className);
            try {
                ClassPool classPool = ClassPool.getDefault();
                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(buffer));
                CtMethod[] methods = ctClass.getDeclaredMethods();
                for (CtMethod method : methods) {
                    System.out.println("Instrumenting method: " + method.getLongName());
                    method.addLocalVariable("start", CtClass.longType);
                    method.addLocalVariable("end", CtClass.longType);
                    method.insertBefore("start = System.nanoTime();" +
                            "System.out.println(\"start=\"+start);");
                    method.insertAfter("end = System.nanoTime();" +
                            "System.out.println(\"end=\"+end);" +
                            "System.out.println(\"duration (sec): \"+ ((float)(end - start))/1000000000.0 );");
                }
                byteCode = ctClass.toBytecode();
                ctClass.detach();
                System.out.println("Instrumenting done\n");
            } catch (Throwable th) {
                System.out.println("Error during instrumentation: " + th);
                th.printStackTrace();
            }
        }
        return byteCode;
    }
}
