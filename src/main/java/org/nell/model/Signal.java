package org.nell.model;

public class Signal
{
    private String name;
    private boolean state;

    public Signal(String name, boolean state)
    {
        this.name = name;
        this.state = state;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean getState()
    {
        return this.state;
    }

    public void setState(boolean state)
    {
        this.state = state;
    }
}
