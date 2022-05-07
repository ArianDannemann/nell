package org.nell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nell.control.Interpreter;
import org.nell.model.Result;

public class BaseGateTest
{
    Interpreter interpreter = new Interpreter();

    @Test
    public void AND()
    {
        List<Result> results = new ArrayList<>();
        String[] testSkript = new String[] {
            "input a",
            "input b",
            "and a,b = AND",
            "show AND"
        };

        results = this.interpreter.interpret(testSkript, "00");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "01");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "10");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "11");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());
    }

    @Test
    public void NAND()
    {
        List<Result> results = new ArrayList<>();
        String[] testSkript = new String[] {
            "input a",
            "input b",
            "nand a,b = NAND",
            "show NAND"
        };

        results = this.interpreter.interpret(testSkript, "00");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "01");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "10");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "11");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());
    }

    @Test
    public void OR()
    {
        List<Result> results = new ArrayList<>();
        String[] testSkript = new String[] {
            "input a",
            "input b",
            "or a,b = OR",
            "show OR"
        };

        results = this.interpreter.interpret(testSkript, "00");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "01");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "10");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "11");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());
    }

    @Test
    public void XOR()
    {
        List<Result> results = new ArrayList<>();
        String[] testSkript = new String[] {
            "input a",
            "input b",
            "xor a,b = XOR",
            "show XOR"
        };

        results = this.interpreter.interpret(testSkript, "00");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "01");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "10");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "11");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());
    }

    @Test
    public void NOR()
    {
        List<Result> results = new ArrayList<>();
        String[] testSkript = new String[] {
            "input a",
            "input b",
            "nor a,b = NOR",
            "show NOR"
        };

        results = this.interpreter.interpret(testSkript, "00");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "01");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "10");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "11");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());
    }

    @Test
    public void NOT()
    {
        List<Result> results = new ArrayList<>();
        String[] testSkript = new String[] {
            "input a",
            "not a = NOT",
            "show NOT"
        };

        results = this.interpreter.interpret(testSkript, "0");
        assertTrue(results.get(0).getOutputSignals().get(0).getState());

        results = this.interpreter.interpret(testSkript, "1");
        assertFalse(results.get(0).getOutputSignals().get(0).getState());
    }
}
