package org.nell.control;

import java.util.ArrayList;
import java.util.List;

import org.nell.model.GateType;
import org.nell.model.Signal;
import org.nell.model.LogicGate;
import org.nell.model.Result;
import org.nell.model.logicgates.NotGate;
import org.nell.model.logicgates.SubCircuitGate;
import org.nell.view.UI;

public class Interpreter
{
    private SignalManager signalManager = new SignalManager();
    private GateManager gateManager = new GateManager();
    private int inputCount = 0;

    private List<String> inputStates = new ArrayList<>();
    private List<Result> circuitResults = new ArrayList<>();

    private int currentCommandIndex = 0;
    private int ignorationLevel = 0;
    private List<String> commands = new ArrayList<>();
    private SubCircuitGate currentSubCircuitGate;

    private boolean isNested = false;

    public List<Result> interpret(String[] commands, String inputState)
    {
        signalManager = new SignalManager();
        gateManager = new GateManager();
        ErrorHandler.currentLine = "";
        this.inputCount = 0;
        boolean cantAddInput = false;

        // If an input state was specified, assume we are a nested interpreter
        if (!inputState.equals(""))
        {
            this.inputStates.add(inputState);
            this.isNested = true;
            UI.isNested = true;
        }
        else
        {
            UI.println("interpreting...");
        }

        for (String command : commands)
        {
            command = command.replace("= ", "");
            command = command.replace("=", "");

            String[] arguments = command.split(" ");

            ErrorHandler.currentLine = command;
            this.currentCommandIndex++;

            if (!arguments[0].equals("input") && !cantAddInput)
            {
                // Add not gates for all inputs
                Signal[] inputs = signalManager.getSignals();
                for (Signal input : inputs)
                {
                    gateManager.addLogicGate(new NotGate(new Signal[] {input}, new Signal[] {signalManager.addSignal("!" + input.getName(), false)}));

                    UI.debugPrint("generated signal !" + input.getName());
                }

                cantAddInput = true;
            }

            // Start ignoring commands when encountering an opening bracket
            if (command.contains("{"))
            {
                this.ignorationLevel++;
            }
            else if (command.contains("}"))
            {
                this.ignorationLevel--;

                if (this.ignorationLevel < 0)
                {
                    ErrorHandler.errorInLine("out of place '}'");
                }
                else if (this.ignorationLevel == 0)
                {
                    // Add the commands to the circuit
                    this.currentSubCircuitGate.addCommands(StringHelper.listToArray(this.commands));

                    // Clear the list of commands for the next subcircuit
                    this.commands.clear();
                }
            }

            // Check if we are in a subcircuit
            // We still want to handle the first layer 'define' command
            if (this.ignorationLevel > 0 && !(this.ignorationLevel == 1 && command.contains("define")))
            {
                // Record the command to a subcircuit
                recordToSubCircuit(command);

                continue;
            }

            switch (arguments[0])
            {
                case "input":
                    if (cantAddInput)
                    {
                        ErrorHandler.errorInLine("input should always be at the top of the file");
                    }
                    handleInputArgument(command, arguments);
                    break;

                case "and":
                    handleAndArgument(command, arguments);
                    break;

                case "show":
                    handleShowArgument(command, arguments);
                    break;

                case "or":
                    handleOrArgument(command, arguments);
                    break;

                case "not":
                    handleNotArgument(command, arguments);
                    break;

                case "nand":
                    handleNandArgument(command, arguments);
                    break;

                case "xor":
                    handleXorArgument(command, arguments);
                    break;

                case "signal":
                    handleSignalArgument(command, arguments);
                    break;

                case "define":
                    handleDefineArgument(command, arguments);
                    break;

                case "}":
                    break;

                default:
                    if (this.gateManager.getSubCircuitByName(arguments[0]) != null)
                    {
                        handleSubCircuitGate(command, arguments);
                    }
                    else
                    {
                        ErrorHandler.errorInLine("command '" + arguments[0] + "' not supported");
                    }
            }
        }

        simulate();

        return this.circuitResults;
    }

    public List<Result> simulate()
    {
        if (!this.isNested)
        {
            UI.println("simulating...");
        }

        String[] inputTable = TableGenerator.generateInputTable(this.inputCount);

        // The uses has specified certain inputs, use those instead of simulating the whole truth table
        if (this.inputStates.size() > 0)
        {
            inputTable = new String[this.inputStates.size()];
            int i = 0;

            for (i = 0; i < this.inputStates.size(); i++)
            {
                inputTable[i] = this.inputStates.get(i);
            }
        }

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
                    UI.debugPrint("-> triggered gate: " + gate.getClass().getSimpleName() + " for '" + output.getName() + "' -> " + output.getState());
                    simulateUpdatedSignal(gate, output);
                }
            }

            // Enter the results in our table
            for (Signal visibleSignal : signalManager.getVisibleSignals())
            {
                UI.debugPrint("-> added signal '" + visibleSignal.getName() + "' -> " + visibleSignal.getState() + " to results");
                result.addOutputSignal(visibleSignal);
            }

            this.circuitResults.add(result);
        }

        showResults();

        return this.circuitResults;
    }

    /*public boolean runSubCircuit(String name, String[] arguments)
    {
        boolean recordCommands = false;
        List<String> subCircuitCommandList = new ArrayList<>();
        String[] subCircuitCommands = null;
        int layer = 0;
        String[] inputSignals;
        String inputState = "";

        UI.debugPrint("running subcircuit '" + name + "'");

        // Go through all commands of the current circuit
        for (String otherCommand : this.commands)
        {
            //UI.println("other command: " + otherCommand);

            // Check if the command starts our subcircuit
            if (otherCommand.equals("define " + name + " {"))
            {
                recordCommands = true;
                layer = 1;
                continue;
            }

            //UI.println("layer: " + layer);

            if (otherCommand.contains("{"))
            {
                layer++;
            }
            else if (otherCommand.contains("}"))
            {
                layer--;

                // If we have reached the final closing bracked and we are currently recording commands, stop looping over them
                if (layer == 0 && recordCommands)
                {
                    break;
                }
            }

            // Record the command if it belongs to the subcircuit
            if (recordCommands)
            {
                subCircuitCommandList.add(otherCommand);

                UI.debugPrint("recorded: " + otherCommand);
            }
        }

        // Copy command list into array
        subCircuitCommands = new String[subCircuitCommandList.size()];
        for (int i = 0; i < subCircuitCommandList.size(); i++)
        {
            subCircuitCommands[i] = subCircuitCommandList.get(i);
        }
        UI.debugPrint("sub command length: " + subCircuitCommands.length);

        inputSignals = arguments[1].split(",");
        for (String inputSignal : inputSignals)
        {
            Signal signal = signalManager.getSignalByName(inputSignal);
            inputState += signal.getState() == true ? '1' : '0';
        }

        // Run the subcircuit in a new interpreter
        Interpreter subInterpreter = new Interpreter();
        List<Result> results = subInterpreter.interpret(subCircuitCommands, inputState);

        UI.debugPrint("returning");

        return true;
    }*/

    public void showResults()
    {
        // Show the results
        UI.println("results:");
        UI.print("\n ");

        int c = 0;
        for (Signal input : signalManager.getSignals())
        {
            UI.print(input.getName());

            c++;

            if (c == this.inputCount)
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

        for (Result result : this.circuitResults)
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

    public void simulateUpdatedSignal(LogicGate origin, Signal signal)
    {
        UI.debugPrint("--> propagating effects");

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
                UI.debugPrint("--> triggered gate through propagation: " + gate.getClass().getSimpleName() + " for '" + output.getName() + "' -> " + output.getState());
                simulateUpdatedSignal(gate, output);
            }
        }
    }

    /**
     *
     * FORM: input <NAME> (opt)<STATE, ...>
     *
     * @param command
     * @param arguments
     */
    public void handleInputArgument(String command, String[] arguments)
    {
        if (arguments.length < 2)
        {
            ErrorHandler.errorInLine("you have to define a name for the input variable");
        }
        else
        {
            // check if enough input states were supplied
            if (!this.isNested && inputCount != 0 && arguments.length - 2 != this.inputStates.size())
            {
                ErrorHandler.errorInLine("all inputs need to have the same amount of states defined");
            }

            // check if there are any specific input states
            if (arguments.length > 2)
            {
                if (this.isNested)
                {
                    ErrorHandler.errorInLine("can't define specific input in nested circuit");
                }

                int i = 0;

                for (i = 2; i < arguments.length; i++)
                {
                    if (!arguments[i].equals("0") && !arguments[i].equals("1"))
                    {
                        ErrorHandler.errorInLine("input state has to be either 0 or 1");
                    }

                    // If its the first input line
                    if (inputCount == 0)
                    {
                        // Add an empty state to fill in
                        this.inputStates.add("");
                    }

                    this.inputStates.set(i - 2, this.inputStates.get(i - 2) + arguments[i]);
                }
            }
        }

        signalManager.addSignal(arguments[1], false);
        this.inputCount++;

        if (this.isNested && this.inputCount > this.inputStates.get(0).toCharArray().length)
        {
            ErrorHandler.errorInLine("invalid amount of inputs. there are more inputs defined in the interpreter than were passed when starting the interpretation");
        }

        UI.debugPrint("added input " + arguments[1]);
    }

    /**
     *
     * FORM: and <SIGNAL,...> <SIGNAL>
     *
     * @param command
     * @param arguments
     */
    public void handleAndArgument(String command, String[] arguments)
    {
        if (arguments.length != 3)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = new Signal[] { signalManager.addSignal(arguments[2], false) };
        gateManager.addLogicGate(GateType.AND, inputs, outputs);

        UI.debugPrint("added AND gate with inputs " + arguments[1] + " and outputs " + arguments[2]);
    }

    /**
     *
     * FORM: or <SIGNAL,...> <SIGNAL>
     *
     * @param command
     * @param arguments
     */
    public void handleOrArgument(String command, String[] arguments)
    {
        if (arguments.length != 3)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = new Signal[] { signalManager.addSignal(arguments[2], false) };
        gateManager.addLogicGate(GateType.OR, inputs, outputs);

        UI.debugPrint("added OR gate with inputs " + arguments[1] + " and outputs " + arguments[2]);
    }

    /**
     *
     * FORM: not <SIGNAL> <SIGNAL>
     *
     * @param command
     * @param arguments
     */
    public void handleNotArgument(String command, String[] arguments)
    {
        if (arguments.length != 3)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = new Signal[] { signalManager.addSignal(arguments[2], false) };
        gateManager.addLogicGate(GateType.NOT, inputs, outputs);

        UI.debugPrint("added NOT gate with inputs " + arguments[1] + " and outputs " + arguments[2]);
    }

    /**
     *
     * FORM: show <SIGNAL, ...>
     *
     * @param command
     * @param arguments
     */
    public void handleShowArgument(String command, String[] arguments)
    {
        if (arguments.length != 2)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        String[] signalNames = arguments[1].split(",");

        for (String signalName : signalNames)
        {
            signalManager.addVisibleSignal(signalName);

            UI.debugPrint("added signal " + signalName + " to visible signals");
        }
    }

    /**
     *
     * FORM: nand <SIGNAL,...> <SIGNAL>
     *
     * @param command
     * @param arguments
     */
    public void handleNandArgument(String command, String[] arguments)
    {
        if (arguments.length != 3)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = new Signal[] { signalManager.addSignal(arguments[2], false) };
        gateManager.addLogicGate(GateType.NAND, inputs, outputs);

        UI.debugPrint("added NAND gate with inputs " + arguments[1] + " and outputs " + arguments[2]);
    }

    /**
     *
     * FORM: and <SIGNAL,...> <SIGNAL>
     *
     * @param command
     * @param arguments
     */
    public void handleXorArgument(String command, String[] arguments)
    {
        if (arguments.length != 3)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = new Signal[] { signalManager.addSignal(arguments[2], false) };
        gateManager.addLogicGate(GateType.XOR, inputs, outputs);

        UI.debugPrint("added XOR gate with inputs " + arguments[1] + " and outputs " + arguments[2]);
    }

    /**
     *
     * FORM: signal <NAME>
     *
     * @param command
     * @param arguments
     */
    public void handleSignalArgument(String command, String[] arguments)
    {
        if (arguments.length != 2)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        signalManager.addSignal(arguments[1], false);

        UI.debugPrint("added signal " + arguments[1]);
    }

    public void handleDefineArgument(String command, String[] arguments)
    {
        if (arguments.length != 3)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        // Add an empty logic gate (for now)
        this.currentSubCircuitGate = (SubCircuitGate) gateManager.addLogicGate(GateType.CUSTOM, null, null);
        this.currentSubCircuitGate.setName(arguments[1]);

        UI.debugPrint("defined new subcircuit '" + arguments[1] + "' at command " + this.currentCommandIndex);
    }

    public void handleSubCircuitGate(String command, String[] arguments)
    {
        if (arguments.length != 3)
        {
            ErrorHandler.errorInLine("invalid number of arguments for command");
        }

        SubCircuitGate subCircuitGate = this.gateManager.getSubCircuitByName(arguments[0]);

        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = signalManager.addSignalsByNames(arguments[2].split(","));

        subCircuitGate.setInputs(inputs);
        subCircuitGate.setOutputs(outputs);

        UI.debugPrint("defined new inputs and outputs for subcircuit '" + arguments[1] + "' at command " + this.currentCommandIndex);
    }

    public void recordToSubCircuit(String command)
    {
        this.commands.add(command);

        UI.debugPrint("recorded command: '" + command + "' into '" + this.currentSubCircuitGate.getName() + "'");
    }
}
