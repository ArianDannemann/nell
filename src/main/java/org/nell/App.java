package org.nell;

import java.util.List;

import org.nell.control.ErrorHandler;
import org.nell.control.FileParser;
import org.nell.control.Interpreter;
import org.nell.model.Result;
import org.nell.view.UI;

public class App
{
    private static String filePath = "";

    public static void main(String[] args)
    {
        Interpreter interpreter = new Interpreter();

        //args = new String[] { ".\\examples\\NOR.nell" };

        UI.clearLog();
        handleArguments(args);

        UI.println("opening: " + filePath);

        String[] lines = FileParser.getLinesFromFile(filePath);
        List<Result> results = interpreter.interpret(lines, "");

        UI.println("got " + results.size() + " results");
    }

    private static void handleArguments(String[] args)
    {
        if (args.length < 1)
        {
            ErrorHandler.error("no arguments given. use -help to get a list of all arguments");
        }

        for (String arg : args)
        {
            if (!arg.startsWith("-"))
            {
                filePath = arg;
                continue;
            }

            switch (arg)
            {
                case "-h":
                case "-help":
                    UI.println("<path to .nell> \t-\t a path to the file that should be opened");
                    UI.println("-h, -help \t-\t shows this message");
                    UI.println("-d, -debug \t-\t shows debug output during interpretation and simulation");
                    UI.println("-n, -nolog \t-\t disables writing output to logfile");
                    System.exit(0);
                    break;

                case "-d":
                case "-debug":
                    UI.showDebugPrints = true;
                    break;

                case "-n":
                case "-nolog":
                    UI.noLog = true;
                    break;

                default:
                    ErrorHandler.error("unknown argument '" + arg + "'. use -help to get a list of all arguments");
                    break;
            }
        }
    }
}
