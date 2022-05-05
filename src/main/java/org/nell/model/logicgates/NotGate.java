package org.nell.model.logicgates;

import org.nell.control.ErrorHandler;
import org.nell.model.LogicGate;
import org.nell.model.Signal;
import org.nell.model.exceptions.InvalidOutputException;

public class NotGate extends LogicGate
{
    public NotGate(Signal[] inputs, Signal[] outputs)
    {
        super(inputs, outputs);

        if (inputs.length != 1 || outputs.length != 1)
        {
            ErrorHandler.error("invalid number of inputs or outputs, or gate expects exactly 1 input and exactly 1 output");
            throw new InvalidOutputException();
        }
    }

    @Override
    public void trigger()
    {
        outputs[0].setState(!inputs[0].getState());
    }
}
