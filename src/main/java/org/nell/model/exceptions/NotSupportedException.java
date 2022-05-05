package org.nell.model.exceptions;

import org.nell.resources.Strings;

public class NotSupportedException extends RuntimeException
{
    public NotSupportedException()
    {
        super(Strings.NOT_SUPPORTED_EXCEPTION);
    }
}
