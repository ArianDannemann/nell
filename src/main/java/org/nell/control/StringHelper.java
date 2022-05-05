package org.nell.control;

import java.util.List;

public class StringHelper
{
    public static String[] listToArray(List<String> input)
    {
        String[] result = new String[input.size()];
        int i = 0;

        for (i = 0; i < input.size(); i++)
        {
            result[i] = input.get(i);
        }

        return result;
    }
}
