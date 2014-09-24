package me.legrange.panstamp.gui;

/**
 *
 * @author gideon
 */
final class ConfigEvent {
    
    enum Type { SERIAL, NETWORK; }
    
    ConfigEvent(Type type) {
        this.type = type;
    }
    
    Type getType() {
        return type;
    }
    
    private final Type type;
}
