package org.nell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nell.control.Interpreter;
import org.nell.model.Result;

public class CircuitTest
{
    Interpreter interpreter = new Interpreter();

    @Test
    public void OneBitFullAdder()
    {
        List<Result> results = new ArrayList<>();
        String[] testSkript = new String[] {
            "input a",
            "input b",
            "input c",
            "signal sum",
            "signal carry",
            "or a,b = 01",
            "and a,b = 02",
            "or a,b,c = 03",
            "and a,b,c = 04",
            "signal 21",
            "and 01,c = 11",
            "and 21,03 = 12",
            "or 11,02 = 21p",
            "not 21p = 21",
            "or 12,04 = sum",
            "not 21 = carry",
            "show carry,sum"
        };

        results = this.interpreter.interpret(testSkript, "000");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());
        assertFalse(results.get(0).getOutputSignals().get(1).getState());

        results = this.interpreter.interpret(testSkript, "001");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());
        assertTrue(results.get(0).getOutputSignals().get(1).getState());

        results = this.interpreter.interpret(testSkript, "010");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());
        assertTrue(results.get(0).getOutputSignals().get(1).getState());

        results = this.interpreter.interpret(testSkript, "011");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());
        assertFalse(results.get(0).getOutputSignals().get(1).getState());

        results = this.interpreter.interpret(testSkript, "100");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());
        assertTrue(results.get(0).getOutputSignals().get(1).getState());

        results = this.interpreter.interpret(testSkript, "101");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());
        assertFalse(results.get(0).getOutputSignals().get(1).getState());

        results = this.interpreter.interpret(testSkript, "110");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());
        assertFalse(results.get(0).getOutputSignals().get(1).getState());

        results = this.interpreter.interpret(testSkript, "111");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());
        assertTrue(results.get(0).getOutputSignals().get(1).getState());

    }
}
