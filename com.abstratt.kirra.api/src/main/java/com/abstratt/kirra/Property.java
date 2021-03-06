package com.abstratt.kirra;

/**
 * Represents a property.
 */
public class Property extends DataElement {

    private static final long serialVersionUID = 1;

    protected Object defaultValue;
    protected boolean autoGenerated;

    public Property() {
    }
    
    /**
     * An auto-generated property has its value automatically assigned (as opposed to provided by user).
     */
    public boolean isAutoGenerated() {
		return autoGenerated;
    }
    
    public void setAutoGenerated(boolean autoGenerated) {
		this.autoGenerated = autoGenerated;
	}

    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(Object value) {
        this.defaultValue = value;
    }
}