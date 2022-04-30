package org.nell.model.exceptions;

import org.nell.resources.Strings;

public class InvalidOutputException extends RuntimeException
{
    public InvalidOutputException()
    {
        super(Strings.INVALID_OUTPUT_EXCEPTION);
    }
}
