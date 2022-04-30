package org.nell;

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
        String[] lines = FileParser.getLinesFromFile("C:\\Users\\arian\\OneDrive\\Dokumente\\Code\\Java\\nell\\examples\\AND.nell");
        UI.println("interpreting...");
        Interpreter.interpret(lines);
    }
}
