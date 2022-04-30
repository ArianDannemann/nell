package org.nell.model.logicgates;

import org.nell.model.LogicGate;
import org.nell.model.Signal;
import org.nell.model.exceptions.InvalidOutputException;
import org.nell.view.UI;

public class NandGate extends LogicGate
{
    public NandGate(Signal[] inputs, Signal[] outputs)
    {
        super(inputs, outputs);

        if (outputs.length != 1)
        {
            UI.printInterpreterError("invalid number of outputs, and gate expects exactly 1 output");
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
                outputs[0].setState(true);
                return;
            }
        }

        outputs[0].setState(false);
    }
}
