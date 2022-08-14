package tools.jackson.datatype.hppc.ser;

import java.io.IOException;

/**
 * Helper class used for temporarily holding on to
 */
public class ExceptionHolder
{
    protected IOException _exception;
    
    public ExceptionHolder() { }

    public void assignException(IOException e) { _exception = e; }
    
    public void throwHeld() throws IOException {
        if (_exception != null) {
            throw _exception;
        }
    }
}
