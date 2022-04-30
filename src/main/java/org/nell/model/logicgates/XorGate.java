package org.nell.model.logicgates;

import org.nell.model.LogicGate;
import org.nell.model.Signal;
import org.nell.model.exceptions.InvalidOutputException;
import org.nell.view.UI;

public class XorGate extends LogicGate
{
    public XorGate(Signal[] inputs, Signal[] outputs)
    {
        super(inputs, outputs);

        if (outputs.length != 1)
        {
            UI.printInterpreterError("invalid number of outputs, or gate expects exactly 1 output");
            throw new InvalidOutputException();
        }
    }

    @Override
    public void trigger()
    {
        int trueInputCount = 0;

        for (Signal input : inputs)
        {
            if (input.getState())
            {
                trueInputCount++;
            }
        }

        outputs[0].setState(trueInputCount == 1 ? true : false);
    }
}
