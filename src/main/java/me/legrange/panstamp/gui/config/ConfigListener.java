package me.legrange.panstamp.gui.config;

/**
 *
 * @author gideon
 */
public interface ConfigListener {
    
    /**
     * The application configuration was updated.
     * @param ev
     */
    public void configUpdated(ConfigEvent ev);
    
}
