package com.abstratt.kirra;

import java.util.List;

public interface ParameterScope extends NameScope {
    public List<Parameter> getParameters();
    public Parameter getParameter(String parameterName);
}
