package org.nell.control;

import org.nell.view.UI;

public class ErrorHandler
{
    public static String currentLine = "";

    public static void error(String message)
    {
        UI.println(" >>> ERROR: " + message);
        System.exit(1);
    }

    public static void errorInLine(String message)
    {
        ErrorHandler.errorInLine(message, ErrorHandler.currentLine);
    }

    public static void errorInLine(String message, String line)
    {
        UI.println(" >>> ERROR: " + message);
        UI.println(" >>> ERROR: " + "line: '" + line + "'");
        System.exit(1);
    }

    public static void assertArgumentCount(int expectedCount, int actualCount, String commandName)
    {
        if (expectedCount != actualCount)
        {
            ErrorHandler.errorInLine("invalid number of arguments, command '" + commandName + "' expects " + expectedCount + " arguments but got " + actualCount);
        }
    }
}
