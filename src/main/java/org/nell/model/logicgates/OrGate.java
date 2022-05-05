package org.nell.model.logicgates;

import org.nell.control.ErrorHandler;
import org.nell.model.LogicGate;
import org.nell.model.Signal;
import org.nell.model.exceptions.InvalidOutputException;

public class OrGate extends LogicGate
{
    public OrGate(Signal[] inputs, Signal[] outputs)
    {
        super(inputs, outputs);

        if (outputs.length != 1)
        {
            ErrorHandler.error("invalid number of outputs, or gate expects exactly 1 output");
            throw new InvalidOutputException();
        }
    }

    @Override
    public void trigger()
    {
        for (Signal input : inputs)
        {
            if (input.getState())
            {
                outputs[0].setState(true);
                return;
            }
        }

        outputs[0].setState(false);
    }
}
