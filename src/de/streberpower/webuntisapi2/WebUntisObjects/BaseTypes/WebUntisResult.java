package de.streberpower.webuntisapi2.WebUntisObjects.BaseTypes;

/**
 * Created by Streberpower on 10.10.2015.
 */
public class WebUntisResult<T> {
    public String error;
    public String jsonrpc;
    public String id;
    public T result;
}
