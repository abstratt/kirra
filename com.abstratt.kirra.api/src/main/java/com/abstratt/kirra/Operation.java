package com.abstratt.kirra;

import java.util.List;

public class Operation extends TypedElement<BehaviorScope> {
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
         * An operation that creates an instance of the parent entity.
         */
        Construtor
    }

    private static final long serialVersionUID = 1L;
    protected boolean enabled = true;
    protected boolean instanceOperation;
    protected OperationKind kind;
    protected List<Parameter> parameters;

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
        for (Parameter current : parameters)
            if (parameterName.equals(current.getName()))
                return current;
        return null;
    }

    public List<Parameter> getParameters() {
        return parameters;
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
}
