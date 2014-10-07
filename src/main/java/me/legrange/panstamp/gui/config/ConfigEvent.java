package me.legrange.panstamp.gui.config;

/**
 *
 * @author gideon
 */
public final class ConfigEvent {
    
    public enum Type { SERIAL, NETWORK; }
    
    ConfigEvent(Type type) {
        this.type = type;
    }
    
    public Type getType() {
        return type;
    }
    
    private final Type type;
}
