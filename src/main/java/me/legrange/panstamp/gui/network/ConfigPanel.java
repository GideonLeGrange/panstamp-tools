package me.legrange.panstamp.gui.network;

import javax.swing.JPanel;
import me.legrange.panstamp.gui.config.Config;

/**
 *
 * @author gideon
 */
public class ConfigPanel extends JPanel {

    public ConfigPanel(Config conf) {
        super();
        this.config = conf;
    }
    
    
    protected final Config config;
    
}
