package com.abstratt.kirra;

public abstract class DataElement extends TypedElement<DataScope> {
	private static final long serialVersionUID = 1L;
	protected boolean derived;
	protected boolean initializable;
	protected boolean editable;
	public boolean isDerived() {
		return derived;
	}
	public void setDerived(boolean derived) {
		this.derived = derived;
	}
	
	@Override
	public String toString() {
		return this.getOwner() + "->" + super.toString();
	}

	/**
	 * A property is initializable if it can be assigned at instance creation time.
	 */
	public boolean isInitializable() {
		return initializable;
	}
	public void setInitializable(boolean initializable) {
		this.initializable = initializable;
	}
	/**
	 * A property is editable if its value can be updated any time after the instance is created.
	 */
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
