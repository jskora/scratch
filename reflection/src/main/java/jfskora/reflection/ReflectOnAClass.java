package jfskora.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class ReflectOnAClass
{
    public static void main( String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ReflectOnAClass reflector = new ReflectOnAClass();
        System.out.println(System.getProperty("user.dir"));
        reflector.inspectClasses(Arrays.asList("jfskora.reflection.SummerDemo", "jfskora.reflection.Summer"));
    }

    private void inspectClasses(List<String> klassNames) throws ClassNotFoundException {
        for (String klassName : klassNames) {
            Class klass = Class.forName(klassName);
            System.out.println("class " + klass.getSimpleName() + " (" + klass.getCanonicalName() + ")");
            for (Method method : klass.getDeclaredMethods()) {
                System.out.print("  " + method.getReturnType().getName() + " " + method.getName() + "(");
                boolean first = true;
                for (Parameter parameter :method.getParameters()) {
                    if (!first) {
                        System.out.print(", ");
                        first = false;
                    }
                    System.out.print(parameter.getType() + " " + parameter.getName());
                }
                System.out.println(")");
            }
            System.out.println("");
        }
    }
}
