package com.abstratt.kirra;

public class SubElement<O extends NameScope> extends NamedElement<O> {
    private static final long serialVersionUID = 1;
    protected TypeRef owner;
    protected boolean inherited;
    

    public TypeRef getOwner() {
        return owner;
    }

    public void setOwner(O owner) {
        this.owner = owner.getTypeRef();
    }

    public void setOwner(TypeRef owner) {
        this.owner = owner;
    }
    
    public boolean isInherited() {
		return inherited;
	}
    
    public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}
}
