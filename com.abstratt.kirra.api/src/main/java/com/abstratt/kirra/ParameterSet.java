package com.abstratt.kirra;

import java.util.List;

public class ParameterSet extends SubElement<ParameterScope> {
    private static final long serialVersionUID = 1L;
    protected List<String> parameters; 
    public List<String> getParameters() {
        return parameters;
    }
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}
