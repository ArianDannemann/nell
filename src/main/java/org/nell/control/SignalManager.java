package org.nell.control;

import java.util.ArrayList;
import java.util.List;

import org.nell.model.Signal;
import org.nell.model.exceptions.NotSupportedException;

public class SignalManager
{
    private Signal[] signals = new Signal[0];
    private List<Signal> visibleSignals = new ArrayList<>();

    public void addVisibleSignal(String name)
    {
        visibleSignals.add(getSignalByName(name));
    }

    public boolean isVisibleSignal(Signal signal)
    {
        for (Signal visibleSignal : visibleSignals)
        {
            if (visibleSignal.getName().equals(signal.getName()))
            {
                return true;
            }
        }

        return false;
    }

    public List<Signal> getVisibleSignals()
    {
        return this.visibleSignals;
    }

    public Signal addSignal(String name, boolean state)
    {
        // Create a new signal
        Signal signal = new Signal(name, state);
        // Prepare an array to copy the old signals into
        Signal[] newSignals = new Signal[signals.length + 1];
        int i = 0;

        // Check if that signal already exists
        if (doesSignalExist(name))
        {
            return getSignalByName(name);
        }

        // Copy the old signals
        for (i = 0; i < signals.length; i++)
        {
            newSignals[i] = signals[i];
        }

        // Add the newest signal
        newSignals[newSignals.length - 1] = signal;

        // Overwrite the old array with the new one
        this.signals = newSignals;

        return signal;
    }

    public Signal[] addSignalsByNames(String[] names)
    {
        Signal[] result = null;
        int c = 0;

        for (String name : names)
        {
            if (!doesSignalExist(name))
            {
                addSignal(name, false);
                c++;
            }
        }

        result = new Signal[c];
        System.arraycopy(this.signals, this.signals.length - c, result, 0, c);

        return result;
    }

    public void setSignal(String name, boolean state)
    {
        throw new NotSupportedException();
    }

    public void setSignal(int index, boolean state)
    {
        if (index < 0 || index > this.signals.length - 1)
        {
            ErrorHandler.errorInLine("signal index " + index + " out of range");
        }

        signals[index].setState(state);
    }

    public Signal[] getSignals()
    {
        return this.signals;
    }

    public boolean doesSignalExist(String name)
    {
        for (Signal signal : signals)
        {
            if (signal.getName().equals(name))
            {
                return true;
            }
        }

        return false;
    }

    public Signal getSignalByName(String name)
    {
        for (Signal signal : signals)
        {
            if (signal.getName().equals(name))
            {
                return signal;
            }
        }

        ErrorHandler.errorInLine("could not find signal '" + name + "'");
        return null;
    }

    public Signal[] getSignalsByName(String argument)
    {
        String[] signalNames = argument.split(",");
        List<Signal> signals = new ArrayList<>();
        Signal[] result = new Signal[0];

        for (String signalName : signalNames)
        {
            signals.add(getSignalByName(signalName));
        }

        result = new Signal[signals.size()];

        for (int i = 0; i < signals.size(); i++)
        {
            result[i] = signals.get(i);
        }

        return result;
    }
}
