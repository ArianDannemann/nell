package org.nell.view;

import org.nell.control.Interpreter;

public class UI
{
    public static String prefix = "[NELL] ";
    public static boolean showDebugPrints = false;

    public static void print(String message)
    {
        System.out.print(message);
    }

    public static void println(String message)
    {
        UI.print(UI.prefix + message + "\n");
    }

    public static void debugPrint(String message)
    {
        if (!showDebugPrints)
        {
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
