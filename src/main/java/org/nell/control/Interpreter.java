package org.nell.control;

import java.util.ArrayList;
import java.util.List;

import org.nell.model.GateType;
import org.nell.model.Signal;
import org.nell.model.LogicGate;
import org.nell.model.Result;
import org.nell.model.exceptions.NotSupportedException;
import org.nell.view.UI;

public class Interpreter
{
    private static SignalManager signalManager = new SignalManager();
    private static GateManager gateManager = new GateManager();
    private static int inputCount = 0;

    private static List<Result> circuitResults = new ArrayList<>();

    public static String currentLine = "";

    public static void interpret(String[] commands)
    {
        signalManager = new SignalManager();
        gateManager = new GateManager();
        Interpreter.currentLine = "";
        Interpreter.inputCount = 0;

        for (String command : commands)
        {
            String[] arguments = command.split(" ");

            Interpreter.currentLine = command;

            switch (arguments[0])
            {
                case "input":
                    handleInputArgument(command, arguments);
                    break;

                case "and":
                    handleAndArgument(command, arguments);
                    break;

                case "show":
                    handleShowArgument(command, arguments);
                    break;

                default:
                    UI.printInterpreterError("command '" + arguments[0] + "' not supported");
                    throw new NotSupportedException();
            }
        }

        UI.println("simulating...");
        simulate();
    }

    public static void simulate()
    {
        String[] inputTable = TableGenerator.generateInputTable(Interpreter.inputCount);

        // Go through all possible input combinations
        for (String inputSetting : inputTable)
        {
            Result result = new Result(inputSetting);

            UI.debugPrint("simulating: " + inputSetting);

            char[] inputStates = inputSetting.toCharArray();
            int i = 0;

            // Set the input bits
            for (i = 0; i < inputStates.length; i++)
            {
                signalManager.setSignal(i, inputStates[i] == '0' ? false : true);
            }

            // Simulate the circuit
            for (LogicGate gate : gateManager.getLogicGates())
            {
                gate.trigger();

                for (Signal output : gate.getOutputs())
                {
                    UI.debugPrint("simulating output: '" + output.getName() + "' -> " + output.getState());
                    simulateUpdatedSignal(gate, output);
                }
            }

            // Enter the results in our table
            for (Signal visibleSignal : signalManager.getVisibleSignals())
            {
                UI.debugPrint("added signal '" + visibleSignal.getName() + "' -> " + visibleSignal.getState() + " to results");
                result.addOutputSignal(visibleSignal);
            }

            Interpreter.circuitResults.add(result);
        }

        showResults();
    }

    public static void showResults()
    {
        // Show the results
        UI.println("results:");
        UI.print("\n ");

        int c = 0;
        for (Signal input : signalManager.getSignals())
        {
            UI.print(input.getName());

            c++;

            if (c == Interpreter.inputCount)
            {
                break;
            }
        }

        UI.print(" | \t");

        for (Signal visibleSignal : signalManager.getVisibleSignals())
        {
            UI.print(visibleSignal.getName() + "\t");
        }

        UI.print("\n\n");

        for (Result result : Interpreter.circuitResults)
        {
            UI.print(" " + result.getInputSettings() + " | \t");

            for (Signal signal : result.getOutputSignals())
            {
                UI.print("" + (signal.getState() ? '1' : '0') + "\t");
            }

            UI.print("\n");
        }

        UI.print("\n");
    }

    public static void simulateUpdatedSignal(LogicGate origin, Signal signal)
    {
        for (LogicGate gate : gateManager.getLogicGates())
        {
            boolean shouldBeTriggered = false;

            // Don't simulate the gate that just triggered a change
            if (gate == origin)
            {
                continue;
            }

            for (Signal input : gate.getInputs())
            {
                if (input.getName().equals(signal.getName()))
                {
                    shouldBeTriggered = true;
                }
            }

            if (!shouldBeTriggered)
            {
                continue;
            }

            gate.trigger();

            for (Signal output : gate.getOutputs())
            {
                UI.debugPrint("simulating output: '" + output.getName() + "' -> " + output.getState());
                simulateUpdatedSignal(gate, output);
            }
        }
    }

    /**
     *
     * FORM: input <NAME>"input signal into the curcuit"
     *
     * @param command
     * @param arguments
     */
    public static void handleInputArgument(String command, String[] arguments)
    {
        signalManager.addSignal(arguments[1], false);
        Interpreter.inputCount++;

        UI.debugPrint("added input " + arguments[1]);
    }

    /**
     *
     * FORM: and <SIGNAL,...>"input" <SIGNAL, ...>"output"
     *
     * @param command
     * @param arguments
     */
    public static void handleAndArgument(String command, String[] arguments)
    {
        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = new Signal[] { signalManager.addSignal(arguments[2], false) };
        gateManager.addLogicGate(GateType.AND, inputs, outputs);

        UI.debugPrint("added AND gate with inputs " + arguments[1] + " and outputs " + arguments[2]);
    }

    /**
     *
     * FORM: show <SIGNAL, ...>"signals to show"
     *
     * @param command
     * @param arguments
     */
    public static void handleShowArgument(String command, String[] arguments)
    {
        String[] signalNames = arguments[1].split(",");

        for (String signalName : signalNames)
        {
            signalManager.addVisibleSignal(signalName);

            UI.debugPrint("added signal " + signalName + " to visible signals");
        }
    }
}
