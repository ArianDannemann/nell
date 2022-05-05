package org.nell.control;

public class TableGenerator
{
    public static String[] generateInputTable(int variableCount)
    {
        String[] result = new String[(int) Math.pow(2, variableCount)];
        String state = getStartingInputStates(variableCount);
        result[0] = state;

        for (int i = 1; i < result.length; i++)
        {
            state = getNextInputState(state);
            result[i] = state;
        }

        return result;
    }

    private static String getStartingInputStates(int variableCount)
    {
        String result = "";

        for (int i = 0; i < variableCount; i++)
        {
            result += '0';
        }

        return result;
    }

    private static String getNextInputState(String currentState)
    {
        char[] stateChars = currentState.toCharArray();
        int i = stateChars.length - 1;
        String result = "";

        while (true)
        {
            if (i < 0)
            {
                return result;
            }

            if (stateChars[i] == '0')
            {
                stateChars[i] = '1';
                break;
            }
            else
            {
                stateChars[i] = '0';
                i--;
            }
        }

        for (int j = 0; j < stateChars.length; j++)
        {
            result += stateChars[j];
        }

        return result;
    }
}
