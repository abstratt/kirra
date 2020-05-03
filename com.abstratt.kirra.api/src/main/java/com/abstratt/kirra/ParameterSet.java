package com.abstratt.kirra;

import java.util.List;

/**
 * A parameter set ties multiple parameters together. 
 * 
 * An operation may have different subsets of its parameters grouped under different parameter sets,
 * representing different ways an operation could be invoked.
 */
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
