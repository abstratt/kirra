package com.abstratt.kirra;

public class SubElement<O extends NameScope> extends NamedElement<O> {
    private static final long serialVersionUID = 1;
    protected TypeRef owner;
    protected TypeRef definer;
    protected boolean inherited;
    
    public TypeRef getDefiner() {
		return definer;
	}
    
    public void setDefiner(TypeRef definer) {
		this.definer = definer;
	}

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
