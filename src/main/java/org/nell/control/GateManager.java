package org.nell.control;

import org.nell.model.GateType;
import org.nell.model.LogicGate;
import org.nell.model.Signal;
import org.nell.model.logicgates.AndGate;
import org.nell.model.logicgates.NandGate;
import org.nell.model.logicgates.NotGate;
import org.nell.model.logicgates.OrGate;
import org.nell.model.logicgates.XorGate;

public class GateManager
{
    private LogicGate[] gates = new LogicGate[0];

    public void addLogicGate(LogicGate gate)
    {
        LogicGate[] newGates = new LogicGate[gates.length + 1];
        int i = 0;

        for (i = 0; i < gates.length; i++)
        {
            newGates[i] = gates[i];
        }

        newGates[newGates.length - 1] = gate;

        gates = newGates;
    }

    public void addLogicGate(GateType type, Signal[] inputs, Signal[] outputs)
    {
        switch (type)
        {
            case AND:
                addLogicGate(new AndGate(inputs, outputs));
                break;

            case OR:
                addLogicGate(new OrGate(inputs, outputs));
                break;

            case NOT:
                addLogicGate(new NotGate(inputs, outputs));
                break;

            case NAND:
                addLogicGate(new NandGate(inputs, outputs));
                break;

            case XOR:
                addLogicGate(new XorGate(inputs, outputs));
                break;

            default:
                break;
        }
    }

    public LogicGate[] getLogicGates()
    {
        return this.gates;
    }
}
