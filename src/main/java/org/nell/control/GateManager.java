package org.nell.control;

import org.nell.model.GateType;
import org.nell.model.LogicGate;
import org.nell.model.Signal;
import org.nell.model.logicgates.AndGate;
import org.nell.model.logicgates.NandGate;
import org.nell.model.logicgates.NotGate;
import org.nell.model.logicgates.OrGate;
import org.nell.model.logicgates.SubCircuitGate;
import org.nell.model.logicgates.XorGate;

public class GateManager
{
    private LogicGate[] gates = new LogicGate[0];

    public LogicGate addLogicGate(LogicGate gate)
    {
        LogicGate[] newGates = new LogicGate[gates.length + 1];
        int i = 0;

        for (i = 0; i < gates.length; i++)
        {
            newGates[i] = gates[i];
        }

        newGates[newGates.length - 1] = gate;

        gates = newGates;

        return gate;
    }

    public LogicGate addLogicGate(GateType type, Signal[] inputs, Signal[] outputs)
    {
        switch (type)
        {
            case AND:
                return addLogicGate(new AndGate(inputs, outputs));

            case OR:
                return addLogicGate(new OrGate(inputs, outputs));

            case NOT:
                return addLogicGate(new NotGate(inputs, outputs));

            case NAND:
                return addLogicGate(new NandGate(inputs, outputs));

            case XOR:
                return addLogicGate(new XorGate(inputs, outputs));

            case CUSTOM:
                return addLogicGate(new SubCircuitGate(inputs, outputs));

            default:
                return null;
        }
    }

    public LogicGate[] getLogicGates()
    {
        return this.gates;
    }

    public SubCircuitGate getSubCircuitByName(String name)
    {
        for (LogicGate logicGate : this.gates)
        {
            if (logicGate instanceof SubCircuitGate)
            {
                SubCircuitGate subCircuitGate = (SubCircuitGate) logicGate;

                if (subCircuitGate.getName().equals(name))
                {
                    return subCircuitGate;
                }
            }
        }

        return null;
    }
}
