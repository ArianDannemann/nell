package org.nell.control;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileParser
{
    public static String[] getLinesFromFile(String path)
    {
        InputStream inputStream = null;
        List<String> lines = new ArrayList<String>();
        String[] result;

        try
        {
            inputStream = new FileInputStream(path);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()))
        {
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                boolean hasCharacters = false;

                if (line.startsWith("#"))
                {
                    continue;
                }

                for (char ch : line.toCharArray())
                {
                    int code = ch;

                    if (code > 96 && code < 123)
                    {
                        hasCharacters = true;
                    }
                }

                if (hasCharacters)
                {
                    lines.add(line);
                }
            }
        }

        result = new String[lines.size()];

        for (int i = 0; i < lines.size(); i++)
        {
            result[i] = lines.get(i);
        }

        return result;
    }
}
