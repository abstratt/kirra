package com.abstratt.kirra;

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
    protected boolean navigable;
    protected String opposite;
    protected boolean primary;
    protected Style style;
    protected boolean visible;
    protected boolean oppositeRequired;

    /**
     * The name of the association that connects the two relationship ends.
     */
    public String getAssociationName() {
        return associationName;
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

    public boolean isVisible() {
        return visible;
    }

    public void setAssociationName(String associationName) {
        this.associationName = associationName;
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

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void setOppositeRequired(boolean oppositeRequired) {
        this.oppositeRequired = oppositeRequired;
    }
    
    public boolean isOppositeRequired() {
        return oppositeRequired;
    }
}
