package com.abstratt.kirra;

public class Relationship extends DataElement {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The style of a relationship.
	 */
	public enum Style {
		/** This relationship refers to the parent object (typically one non-null at most).*/
		PARENT, 
		/** This relationship refers to one or more child objects.*/
		CHILD,
		/** This relationship refers to one or more related objects which are not children or parents of the instance on this side.*/
		LINK;
	}
	protected Style style;
	protected boolean visible;
	protected boolean primary;
	protected String opposite;
	protected boolean navigable;
	protected String associationName;
	
	/**
	 * Is this the primary end of the association?
	 */
	public boolean isPrimary() {
		return primary;
	}
	
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
	public void setAssociationName(String associationName) {
		this.associationName = associationName;
	}
	/**
	 * The name of the association that connects the two relationship ends.
	 */
	public String getAssociationName() {
		return associationName;
	}
	
	/**
	 * Is this end of the association navigable?
	 */
	public boolean isNavigable() {
		return navigable;
	}

	public void setNavigable(boolean navigable) {
		this.navigable = navigable;
	}

	/**
	 * What is the style of the relationship?
	 */
	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setOpposite(String opposite) {
		this.opposite = opposite;
	}
	
	/**
	 * Returns the name of the opposite relationship, if available.
	 * 
	 * @return the name of the opposite relationship, or <code>null</code>
	 */
	public String getOpposite() {
		return opposite;
	}
}
