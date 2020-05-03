package com.abstratt.kirra;

import java.util.List;

/**
 * The protocol for things that can hold parameters (such {@link Operation}s).
 */
public interface ParameterScope extends NameScope {
    public List<Parameter> getParameters();
    public Parameter getParameter(String parameterName);
}
