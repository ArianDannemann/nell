package org.nell.control;

import java.util.ArrayList;
import java.util.List;

import org.nell.model.GateType;
import org.nell.model.Signal;
import org.nell.model.Result;
import org.nell.model.logicgates.NotGate;
import org.nell.model.logicgates.SubCircuitGate;
import org.nell.view.UI;

public class Interpreter
{
    // Used to model the current circuit, will be filled with content by interpreter
    private SignalManager signalManager = new SignalManager();
    private GateManager gateManager = new GateManager();
    // The actual circuit will be passed on to the simulator
    private Simulator simulator = null;

    // List of defined input states, if this list is empty, all possible input combinations will be tried
    private List<String> inputStates = new ArrayList<>();
    // List of commands to be interpreted
    private List<String> commands = new ArrayList<>();

    // Number of input signals
    private int inputCount = 0;
    // How many layers we are inside of subcircuits, 0 == no subcircuit
    private int layer = 0;
    // The current subcircuit we are creating
    private SubCircuitGate currentSubCircuitGate;
    // Are we ourselfes a subcircuit?
    private boolean isNested = false;

    public List<Result> interpret(String[] commands, String inputState)
    {
        signalManager = new SignalManager();
        gateManager = new GateManager();
        ErrorHandler.currentLine = "";
        this.inputCount = 0;
        this.inputStates.clear();
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
                this.layer++;
            }
            else if (command.contains("}"))
            {
                this.layer--;

                if (this.layer < 0)
                {
                    ErrorHandler.errorInLine("out of place '}'");
                }
                else if (this.layer == 0)
                {
                    // Add the commands to the circuit
                    this.currentSubCircuitGate.addCommands(StringHelper.listToArray(this.commands));

                    // Clear the list of commands for the next subcircuit
                    this.commands.clear();
                }
            }

            // Check if we are in a subcircuit
            // We still want to handle the first layer 'define' command
            if (this.layer > 0 && !(this.layer == 1 && command.contains("define")))
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

                case "nor":
                    handleNorArgument(command, arguments);
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

        // Setup the simulator with all the information we interpreted from the file
        simulator = new Simulator(this.inputCount, this.signalManager, this.gateManager, this.inputStates, this.isNested);

        return simulator.simulate();
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
        ErrorHandler.assertArgumentCount(3, arguments.length, arguments[0]);

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
        ErrorHandler.assertArgumentCount(3, arguments.length, arguments[0]);

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
        ErrorHandler.assertArgumentCount(3, arguments.length, arguments[0]);

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
        ErrorHandler.assertArgumentCount(2, arguments.length, arguments[0]);

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
        ErrorHandler.assertArgumentCount(3, arguments.length, arguments[0]);

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
        ErrorHandler.assertArgumentCount(3, arguments.length, arguments[0]);

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
        ErrorHandler.assertArgumentCount(2, arguments.length, arguments[0]);

        signalManager.addSignal(arguments[1], false);

        UI.debugPrint("added signal " + arguments[1]);
    }

    public void handleDefineArgument(String command, String[] arguments)
    {
        ErrorHandler.assertArgumentCount(3, arguments.length, arguments[0]);

        // Add an empty logic gate (for now)
        this.currentSubCircuitGate = (SubCircuitGate) gateManager.addLogicGate(GateType.CUSTOM, null, null);
        this.currentSubCircuitGate.setName(arguments[1]);

        UI.debugPrint("defined new subcircuit '" + arguments[1] + "'");
    }

    public void handleSubCircuitGate(String command, String[] arguments)
    {
        ErrorHandler.assertArgumentCount(3, arguments.length, arguments[0]);

        SubCircuitGate subCircuitGate = this.gateManager.getSubCircuitByName(arguments[0]);

        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = signalManager.addSignalsByNames(arguments[2].split(","));

        subCircuitGate.setInputs(inputs);
        subCircuitGate.setOutputs(outputs);

        UI.debugPrint("defined new inputs and outputs for subcircuit '" + arguments[1] + "'");
    }

    /**
     *
     * FORM: and <SIGNAL,...> <SIGNAL>
     *
     * @param command
     * @param arguments
     */
    public void handleNorArgument(String command, String[] arguments)
    {
        ErrorHandler.assertArgumentCount(3, arguments.length, arguments[0]);

        Signal[] inputs = signalManager.getSignalsByName(arguments[1]);
        Signal[] outputs = new Signal[] { signalManager.addSignal(arguments[2], false) };
        gateManager.addLogicGate(GateType.NOR, inputs, outputs);

        UI.debugPrint("added NOR gate with inputs " + arguments[1] + " and outputs " + arguments[2]);
    }

    public void recordToSubCircuit(String command)
    {
        this.commands.add(command);

        UI.debugPrint("recorded command: '" + command + "' into '" + this.currentSubCircuitGate.getName() + "'");
    }
}
