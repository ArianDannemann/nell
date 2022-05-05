package org.nell.model.logicgates;

import org.nell.model.Signal;
import org.nell.model.exceptions.InvalidOutputException;
import org.nell.control.ErrorHandler;
import org.nell.model.LogicGate;

public class AndGate extends LogicGate
{
    public AndGate(Signal[] inputs, Signal[] outputs)
    {
        super(inputs, outputs);

        if (outputs.length != 1)
        {
            ErrorHandler.error("invalid number of outputs, and gate expects exactly 1 output");
            throw new InvalidOutputException();
        }
    }

    @Override
    public void trigger()
    {
        for (Signal input : inputs)
        {
            if (!input.getState())
            {
                outputs[0].setState(false);
                return;
            }
        }

        outputs[0].setState(true);
    }
}
