package com.abstratt.kirra;

import java.util.List;

import com.abstratt.kirra.TypeRef.TypeKind;

/**
 * Represents behavioral elements such as Actions and Queries.
 */
public class Operation extends TypedElement<BehaviorScope> implements ParameterScope {
    /**
     * An operation can of many kinds. See {@link Operation#getKind()}.
     */
    public enum OperationKind {
        /**
         * An operation that performs a change on the target object and or
         * system (for static actions).
         */
        Action,
        /**
         * Events are not operations per se, but share quite a few properties
         * with operations, hence modeled as such.
         */
        Event,
        /**
         * A finder finds instances of entities according to some internal
         * criteria. Finders always return sets of {@link Instance}s.
         */
        Finder,
        /**
         * A retriever produces data. The data may be backed by entity
         * instances, but it can really be any arbitrary tuple-shaped data.
         * Retrievers return sets of {@link Tuple}s.
         */
        Retriever,
        /**
         * An operation that creates an instance of its entity.
         */
        Constructor
    }

    private static final long serialVersionUID = 1L;
    protected boolean enabled = true;
    protected boolean instanceOperation;
    protected OperationKind kind;
    protected List<Parameter> parameters;
    protected List<ParameterSet> parameterSets;

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        Operation other = (Operation) obj;
        if (instanceOperation != other.instanceOperation)
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        } else if (!parameters.equals(other.parameters))
            return false;
        return true;
    }

    public OperationKind getKind() {
        return kind;
    }

    public Parameter getParameter(String parameterName) {
        return NameScope.find(parameters, parameterName);
    }
    
    public ParameterSet getParameterSet(String parameterName) {
        return NameScope.find(parameterSets, parameterName);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
    
    public List<ParameterSet> getParameterSets() {
        return parameterSets;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (instanceOperation ? 1231 : 1237);
        result = prime * result + (parameters == null ? 0 : parameters.hashCode());
        return result;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isInstanceOperation() {
        return instanceOperation;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setInstanceOperation(boolean instanceOperation) {
        this.instanceOperation = instanceOperation;
    }

    public void setKind(OperationKind kind) {
        this.kind = kind;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public TypeKind getTypeKind() {
        return TypeKind.Operation;
    }
    
    public void setParameterSets(List<ParameterSet> parameterSets) {
        this.parameterSets = parameterSets;
    }
}
