package org.nell.control;

import org.nell.model.GateType;
import org.nell.model.Signal;
import org.nell.model.LogicGate;
import org.nell.model.logicgates.AndGate;
import org.nell.model.logicgates.NandGate;
import org.nell.model.logicgates.NotGate;
import org.nell.model.logicgates.OrGate;

public class GateManager
{
    private LogicGate[] gates = new LogicGate[0];

    public void addLogicGate(GateType type, Signal[] inputs, Signal[] outputs)
    {
        LogicGate gate = null;
        LogicGate[] newGates = new LogicGate[gates.length + 1];
        int i = 0;

        for (i = 0; i < gates.length; i++)
        {
            newGates[i] = gates[i];
        }

        switch (type)
        {
            case AND:
                gate = new AndGate(inputs, outputs);
                break;

            case OR:
                gate = new OrGate(inputs, outputs);
                break;

            case NOT:
                gate = new NotGate(inputs, outputs);
                break;

            case NAND:
                gate = new NandGate(inputs, outputs);
                break;

            default:
                break;
        }

        newGates[newGates.length - 1] = gate;

        gates = newGates;
    }

    public LogicGate[] getLogicGates()
    {
        return this.gates;
    }
}
