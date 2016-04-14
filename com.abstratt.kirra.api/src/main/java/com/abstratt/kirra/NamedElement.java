package com.abstratt.kirra;

import java.io.Serializable;

public abstract class NamedElement<O extends NameScope> implements Serializable, Comparable<NamedElement<O>> {

    private static String toTitleCase(String toConvert, String separators, boolean multiple) {
        StringBuffer output = new StringBuffer(toConvert.length());
        toConvert = toConvert.trim();
        output.append(Character.toUpperCase(toConvert.charAt(0)));
        for (int i = 1; i < toConvert.length(); i++) {
            if (Character.isUpperCase(toConvert.charAt(i)) && i > 0 && !Character.isUpperCase(toConvert.charAt(i - 1)))
                output.append(' ');
            else if (separators.indexOf(toConvert.charAt(i)) >= 0) {
                if (!Character.isWhitespace(output.charAt(output.length() - 1)))
                    output.append(' ');
                continue;
            }
            if (Character.isWhitespace(output.charAt(output.length() - 1)))
                output.append(Character.toUpperCase(toConvert.charAt(i)));
            else
                output.append(toConvert.charAt(i));
        }
        return output.toString();
    }

    private static final long serialVersionUID = 1L;
    protected boolean userVisible;
    protected String description;
    protected String label;
    protected String name;
    protected String symbol;
    

	public boolean isUserVisible() {
	    return userVisible;
	}

	public void setUserVisible(boolean userVisible) {
	    this.userVisible = userVisible;
	}

    @Override
    public int compareTo(NamedElement<O> o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NamedElement<?> other = (NamedElement<?>) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        if (label != null)
            return label;
        if (name == null)
            return name;
        // ignore non-alphabetic prefixes
        String base = name;
        while (!Character.isLetter(base.charAt(0)))
            base = base.substring(1);
        return NamedElement.toTitleCase(base, "_-", false);
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (label == null ? 0 : label.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        return result;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.name + " (" + getClass().getSimpleName() + ")";
    }
}
