package org.nell.model;

public abstract class LogicGate
{
    protected Signal[] inputs;
    protected Signal[] outputs;
    public abstract void trigger();

    public LogicGate(Signal[] inputs, Signal[] outputs)
    {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public Signal[] getInputs()
    {
        return this.inputs;
    }

    public Signal[] getOutputs()
    {
        return this.outputs;
    }
}
