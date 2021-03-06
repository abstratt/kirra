package com.abstratt.kirra;

/**
 * A relationship connects two entities. There are multiple {@link Style}s of relationships.
 * 
 * A relationship represents one side of an association. Often, the same association will be represented as 
 * two, complementary relationships.
 */
public class Relationship extends DataElement {
    /**
     * The style of a relationship.
     */
    public enum Style {
        /** This relationship refers to one or more child objects. */
        CHILD,
        /**
         * This relationship refers to one or more related objects which are not
         * children or parents of the instance on this side.
         */
        LINK,
        /**
         * This relationship refers to the parent object (typically one non-null
         * at most).
         */
        PARENT;
    }

    private static final long serialVersionUID = 1L;
    protected String associationName;
    protected String associationNamespace;
    protected boolean navigable;
    protected String opposite;
    protected boolean primary;
    protected Style style;
    protected boolean oppositeRequired;
    protected boolean oppositeReadOnly;    

    /**
     * The name of the association that connects the two relationship ends.
     */
    public String getAssociationName() {
        return associationName;
    }
    
    public String getAssociationNamespace() {
		return associationNamespace;
	}

    /**
     * Returns the name of the opposite relationship, if available.
     * 
     * @return the name of the opposite relationship, or <code>null</code>
     */
    public String getOpposite() {
        return opposite;
    }

    /**
     * What is the style of the relationship?
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Is this end of the association navigable?
     */
    public boolean isNavigable() {
        return navigable;
    }

    /**
     * Is this the primary end of the association?
     */
    public boolean isPrimary() {
        return primary;
    }

    public void setAssociationName(String associationName) {
        this.associationName = associationName;
    }
    
    public void setAssociationNamespace(String associationNamespace) {
		this.associationNamespace = associationNamespace;
	}

    public void setNavigable(boolean navigable) {
        this.navigable = navigable;
    }

    public void setOpposite(String opposite) {
        this.opposite = opposite;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public void setOppositeRequired(boolean oppositeRequired) {
        this.oppositeRequired = oppositeRequired;
    }
    
    public boolean isOppositeRequired() {
        return oppositeRequired;
    }
    
    public void setOppositeReadOnly(boolean oppositeReadOnly) {
		this.oppositeReadOnly = oppositeReadOnly;
	}
    
    public boolean isOppositeReadOnly() {
		return oppositeReadOnly;
	}
}
