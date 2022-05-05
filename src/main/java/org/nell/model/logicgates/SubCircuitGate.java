package org.nell.model.logicgates;

import java.util.ArrayList;
import java.util.List;

import org.nell.control.ErrorHandler;
import org.nell.control.Interpreter;
import org.nell.model.LogicGate;
import org.nell.model.Result;
import org.nell.model.Signal;

public class SubCircuitGate extends LogicGate
{
    private String name;
    private String[] commands;

    public SubCircuitGate(Signal[] inputs, Signal[] outputs)
    {
        super(inputs, outputs);
    }

    public void addCommands(String[] commands)
    {
        this.commands = commands;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setInputs(Signal[] inputs)
    {
        this.inputs = inputs;
    }

    public void setOutputs(Signal[] outputs)
    {
        this.outputs = outputs;
    }

    @Override
    public void trigger()
    {
        String inputState = "";
        List<Result> results = new ArrayList<>();
        int i = 0;

        // Generate an input string from our gate inputs
        for (Signal input : inputs)
        {
            inputState += input.getState() == true ? '1' : '0';
        }

        // Run the commands passed to the gate in a new interpreter
        Interpreter interpreter = new Interpreter();
        results = interpreter.interpret(commands, inputState);

        if (results.get(0).getOutputSignals().size() != this.outputs.length)
        {
            ErrorHandler.errorInLine("results from circuit and expected results do not match");
        }

        // Write the states from the results of the subinterpreter into our output array
        for (i = 0; i < results.get(0).getOutputSignals().size(); i++)
        {
            outputs[i].setState(results.get(0).getOutputSignals().get(i).getState());
        }
    }
}
