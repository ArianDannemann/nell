package org.nell.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.nell.control.ErrorHandler;
import org.nell.model.Result;
import org.nell.model.Signal;

public class UI
{
    private static String prefix = "[NELL] ";
    private static String logFilePath = "./latest.log";
    public static boolean showDebugPrints = false;
    public static boolean noLog = false;
    public static boolean isNested = false;

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

            bufferedWriter.write((UI.isNested ? "---->" : "") + message);

            bufferedWriter.close();
        }
        catch (IOException e)
        {
            ErrorHandler.error("could not write to log file");
        }
    }

    public static void print(String message)
    {
        if (!UI.isNested)
        {
            System.out.print(message);
            UI.log(message);
        }
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

    public static void showCircuitResults(List<Result> circuitResults)
    {
        Result firstResult = circuitResults.get(0);

        UI.println("got " + circuitResults.size() + " results:\n");

        UI.print("INPUT\t");
        for (Signal output : firstResult.getOutputSignals())
        {
            UI.print("|\t" + output.getName() + "\t");
        }
        UI.print("\n");

        UI.print("--------");
        for (Signal output : firstResult.getOutputSignals())
        {
            if (output.getState() == true || output.getState() == false)
                UI.print("|---------------");
        }
        UI.print("\n");

        for (Result result : circuitResults)
        {
            UI.print(result.getInputSettings() + "\t");

            for (Signal output : result.getOutputSignals())
            {
                UI.print("|\t" + (output.getState() == true ? '1' : '0') + "\t");
            }

            UI.print("\n");
        }

        UI.print("\n");
    }
}
