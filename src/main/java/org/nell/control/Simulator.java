package org.nell.control;

import java.util.ArrayList;
import java.util.List;

import org.nell.model.LogicGate;
import org.nell.model.Result;
import org.nell.model.Signal;
import org.nell.view.UI;

public class Simulator
{
    private boolean isNested = false;
    private int inputCount = 0;

    // These managers hold the setup of the circuit we want to simulate
    private SignalManager signalManager = null;
    private GateManager gateManager = null;

    // User defined input states, if this list is empty it will just try all possible states
    private List<String> inputStates = new ArrayList<>();
    private List<Result> circuitResults = new ArrayList<>();

    public Simulator(int inputCount, SignalManager signalManager, GateManager gateManager, List<String> inputStates, boolean isNested)
    {
        this.inputCount = inputCount;
        this.signalManager = signalManager;
        this.gateManager = gateManager;
        this.inputStates = inputStates;
        this.isNested = isNested;
    }

    public List<Result> simulate()
    {
        if (!this.isNested)
        {
            UI.println("simulating...");
        }

        // Start of by generating all possible input settings
        String[] inputTable = TableGenerator.generateInputTable(this.inputCount);

        // The uses has specified certain inputs, use those instead of simulating the whole truth table
        if (this.inputStates.size() > 0)
        {
            inputTable = new String[this.inputStates.size()];
            int i = 0;

            for (i = 0; i < this.inputStates.size(); i++)
            {
                inputTable[i] = this.inputStates.get(i);
            }
        }

        // Go through all possible input combinations
        for (String inputSetting : inputTable)
        {
            Result result = null;
            char[] inputStates = null;
            int i = 0;

            // Prepare a result for the circuit
            result = new Result(inputSetting);

            UI.debugPrint("simulating: " + inputSetting);

            // Turn the state string into a char array
            inputStates = inputSetting.toCharArray();
            // Set the input bits
            for (i = 0; i < inputStates.length; i++)
            {
                signalManager.setSignal(i, inputStates[i] == '0' ? false : true);
            }

            // Go through all logic gates in the circuit
            for (LogicGate gate : gateManager.getLogicGates())
            {
                // Run the logic of the gate
                gate.trigger();

                // For every signal that our gate puts out...
                for (Signal output : gate.getOutputs())
                {
                    UI.debugPrint("-> triggered gate: " + gate.getClass().getSimpleName() + " for '" + output.getName() + "' -> " + output.getState());

                    // ... simulate the changes it has on the rest of the circuit
                    simulateUpdatedSignal(gate, output);
                }
            }

            // After all gates have been triggered, go through all signals that were marked as output...
            for (Signal visibleSignal : signalManager.getVisibleSignals())
            {
                // ... and add their state to the result
                result.addOutputSignal(visibleSignal);

                UI.debugPrint("-> added signal '" + visibleSignal.getName() + "' -> " + visibleSignal.getState() + " to results");
            }

            // Add the current result into the list of results
            this.circuitResults.add(result);
        }

        // Show the circuit results
        if (!this.isNested)
        {
            UI.showCircuitResults(this.circuitResults);
        }

        return this.circuitResults;
    }

    public void simulateUpdatedSignal(LogicGate origin, Signal signal)
    {
        UI.debugPrint("--> propagating effects");

        // Go through all logic gates in the circuit
        for (LogicGate gate : gateManager.getLogicGates())
        {
            boolean shouldBeTriggered = false;

            // Don't simulate the gate that just triggered a change
            if (gate == origin)
            {
                continue;
            }

            // Check if the gate uses the signal that has just changed as input
            // (otherwise triggering it again would have no effects)
            for (Signal input : gate.getInputs())
            {
                if (input.getName().equals(signal.getName()))
                {
                    shouldBeTriggered = true;
                }
            }

            // If the gate doesn't use the changed signal as input...
            if (!shouldBeTriggered)
            {
                // ... just skip it
                continue;
            }

            // Run the gate logic once again
            gate.trigger();

            // For every output that has now changed, we need to check again if that had any effect on the circuit
            for (Signal output : gate.getOutputs())
            {
                UI.debugPrint("--> triggered gate through propagation: " + gate.getClass().getSimpleName() + " for '" + output.getName() + "' -> " + output.getState());

                // Call the propagation method recursively
                simulateUpdatedSignal(gate, output);
            }
        }
    }
}
