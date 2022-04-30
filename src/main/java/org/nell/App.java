package org.nell;

import org.nell.control.ErrorHandler;
import org.nell.control.FileParser;
import org.nell.control.Interpreter;
import org.nell.view.UI;

public class App
{
    /**
     * Hello, world!
     *
     * args 0: File path
     *
     * @param args
     */
    public static void main(String[] args)
    {
        String filePath = "";

        UI.clearLog();

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
                    System.exit(0);
                    break;

                case "-d":
                case "-debug":
                    UI.showDebugPrints = true;
                    break;

                default:
                    ErrorHandler.error("unknown argument '" + arg + "'. use -help to get a list of all arguments");
                    break;
            }
        }

        UI.println("opening: " + filePath);
        String[] lines = FileParser.getLinesFromFile(filePath);

        UI.println("interpreting...");
        Interpreter.interpret(lines);
    }
}
