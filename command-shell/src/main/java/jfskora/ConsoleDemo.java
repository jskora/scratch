package jfskora;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class ConsoleDemo {

    private Options argOptions = new Options();
    private Map<String, Options> commandMap = new HashMap<>();

    void initOptions() {
        argOptions.addOption("u", "url", true, "NiFi server API URL (http://localhost:8080/nifi-api)");

        Options tmpOptions = new Options();
        tmpOptions.addOption("a", "all",false, "");
        commandMap.put("env", )
    }

    public static void main(String[] args) {

        CommandLineParser argParser = new DefaultParser();
        CommandLine argCommand = null;
        try {
            argCommand = argParser.parse(argOptions, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        CommandLineParser cmdParser = new DefaultParser();
        CommandLine cmdCommand = null;
        if (argCommand != null) {
            System.out.println(String.format("API URL is %s", argCommand.getOptionValue("url")));
            try {
                Scanner console = new Scanner(System.in);

                while (console.hasNextLine()) {
                    final String currentLine = console.nextLine();
                    try {
                        cmdCommand = cmdParser.parse(cmdOptions, currentLine.split(" "));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println(String.format("%s %s", cmdCommand.getOptions(), cmdCommand.getArgs()));
                }
            } catch (Exception ex) {

                // if any error occurs
                ex.printStackTrace();
            }
        }
    }
}