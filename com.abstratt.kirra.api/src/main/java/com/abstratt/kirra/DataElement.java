package com.abstratt.kirra;

public abstract class DataElement extends TypedElement<DataScope> {
    private static final long serialVersionUID = 1L;
    protected boolean derived;
    protected boolean editable;
    protected boolean initializable;
    protected Integer position;
    protected boolean mnemonic;
    protected boolean unique;
    
    /**
     * A unique property does not allow duplicated values.
     */
    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
    
    
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    public Integer getPosition() {
        return position;
    }

    /**
     * A data element that is derived requires some computation before it can be determined.
     * A client that wants to copy data around without generating unnecessary computations
     * can safely skip derived properties.
     * 
     * @return
     */
    public boolean isDerived() {
        return derived;
    }

    /**
     * A property is editable if its value can be updated any time after the
     * instance is created.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * A property is initializable if it can be assigned at instance creation
     * time.
     */
    public boolean isInitializable() {
        return initializable;
    }

    public void setDerived(boolean derived) {
        this.derived = derived;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setInitializable(boolean initializable) {
        this.initializable = initializable;
    }

    @Override
    public String toString() {
        return this.getOwner() + "->" + super.toString();
    }

    /** Is this element a mnemonic, i.e. a piece of data that is used to represent the object is it part of? */
	public boolean isMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(boolean mnemonic) {
		this.mnemonic = mnemonic;
	}
}
