package jskora.commander;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class ExampleJCommander {

    private static class JCArgs {
        @Parameter
        private List<String> parameters = new ArrayList<>();

        @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
        private Integer verbose = 1;

        @Parameter(names = "-groups", description = "Comma-separated list of group names to be run")
        private String groups;

        @Parameter(names = "-colors", description = "Comma-separated list of colors names to use")
        private List<String> colors;

        @Parameter(names = "-debug", description = "Debug mode")
        private boolean debug = false;
    }

    public static void main(String[] args) {
        process(new JCArgs(), new String[]{ "-log", "2", "-groups", "unit" });
        process(new JCArgs(), new String[]{ "-log", "1", "-groups", "a,b,c" });
        process(new JCArgs(), new String[]{ "-log", "1", "arg1", "-groups", "a,b,c", "arg2" });
        process(new JCArgs(), new String[]{ "-log", "1", "arg1", "arg2" });
        process(new JCArgs(), new String[]{ "-log", "1", "arg1", "-groups", "a,b,c", "-colors", "red", "-colors", "blue" });
    }

    private static void process(JCArgs jcargs, String[] argv) {
        JCommander.newBuilder()
                .addObject(jcargs)
                .build()
                .parse(argv);
        System.out.println("jcargs");
        System.out.println("   argv = " + String.join(" ", argv));
        System.out.println("   parameters = " + String.join(" ", jcargs.parameters));
        System.out.println("   verbose = " + jcargs.verbose);
        System.out.println("   groups = " + jcargs.groups);
        System.out.println("   colors = " + (jcargs.colors != null ? String.join(" ", jcargs.colors) : "null"));
        System.out.println("   debug = " + jcargs.debug);
        System.out.println();
    }
}
