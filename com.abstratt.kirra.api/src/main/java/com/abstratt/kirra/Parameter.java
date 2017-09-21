package com.abstratt.kirra;

import java.util.List;

public class Parameter extends TypedElement<ParameterScope> {
    private static final long serialVersionUID = 1L;
    
    protected Effect effect = Effect.None;
    
    protected Direction direction = Direction.In;
    
    protected List<String> parameterSets;
    
    protected boolean inAllSets;
    
    public List<String> getParameterSets() {
        return parameterSets;
    }
    
    public void setParameterSets(List<String> parameterSets) {
        this.parameterSets = parameterSets;
    }
    
    public void setInAllSets(boolean inAllSets) {
        this.inAllSets = inAllSets;
    }
    
    public boolean getInAllSets() {
        return inAllSets;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public enum Direction {
        In,
        Out,
        InOut
    }
    
    public Effect getEffect() {
        return effect;
    }
    
    public void setEffect(Effect effect) {
        this.effect = effect;
    }
    
    public enum Effect {
        /** 
         * No special behavior for this parameter.
         */
        None,
        /**
         * This parameter is going to be used as a basis to create a new instance.
         */
        Creation
    }
}
