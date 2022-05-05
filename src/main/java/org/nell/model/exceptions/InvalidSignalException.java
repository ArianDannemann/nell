package org.nell.model.exceptions;

import org.nell.resources.Strings;

public class InvalidSignalException extends RuntimeException
{
    public InvalidSignalException()
    {
        super(Strings.INVALID_SIGNAL_EXCEPTION);
    }
}
