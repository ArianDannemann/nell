package org.nell.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.nell.control.ErrorHandler;
import org.nell.control.Interpreter;

public class UI
{
    private static String prefix = "[NELL] ";
    private static String logFilePath = "./lates.log";
    public static boolean showDebugPrints = false;
    public static boolean noLog = false;

    public static void clearLog()
    {
        File logFile = new File(logFilePath);

        try
        {
            logFile.createNewFile();
        }
        catch (IOException e)
        {
            ErrorHandler.error("could not create log file");
        }

        log("# nell log\n\n", true);
    }

    public static void log(String message)
    {
        log(message, false);
    }

    public static void log(String message, boolean overwrite)
    {
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;

        if (noLog)
        {
            return;
        }

        try
        {
            fileWriter = new FileWriter(logFilePath, !overwrite);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(message);

            bufferedWriter.close();
        }
        catch (IOException e)
        {
            ErrorHandler.error("could not write to log file");
        }
    }

    public static void print(String message)
    {
        System.out.print(message);
        UI.log(message);
    }

    public static void println(String message)
    {
        UI.print(UI.prefix + message + "\n");
    }

    public static void debugPrint(String message)
    {
        if (!showDebugPrints)
        {
            // Still log the message
            UI.log(message + "\n");
            return;
        }

        UI.println(message);
    }

    public static void printInterpreterError(String message)
    {
        UI.println(">>> ERROR: " + message);
        UI.println(">>> ERROR: in line: '" + Interpreter.currentLine + "'");
    }
}
