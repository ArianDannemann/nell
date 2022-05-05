package org.nell.model;

import java.util.ArrayList;
import java.util.List;

public class Result
{
    private String inputSettings;
    private List<Signal> outputSignals = new ArrayList<>();

    public Result(String inputSettings)
    {
        this.inputSettings = inputSettings;
    }

    public void addOutputSignal(Signal outputSignal)
    {
        Signal copy = new Signal(outputSignal.getName(), outputSignal.getState());
        this.outputSignals.add(copy);
    }

    public String getInputSettings()
    {
        return this.inputSettings;
    }

    public List<Signal> getOutputSignals()
    {
        return this.outputSignals;
    }
}
