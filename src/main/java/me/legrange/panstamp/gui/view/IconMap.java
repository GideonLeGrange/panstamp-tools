package me.legrange.panstamp.gui.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.Register;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
class IconMap {
    
    static final String STANDARD_REGISTER = "weight.png";
    
    static Icon getWorldIcon() {
        return getIcon(WORLD);
    }
    
    static Icon getNetworkIcon(Network nw) {
        return getIcon(NETWORK);
    }
    
    static Icon getPanStampIcon(PanStamp ps) {
        String name = PANSTAMP_UNKNOWN;
        try {
            name = (ps.getProductId() != 0) ? PANSTAMP_KNOWN : PANSTAMP_UNKNOWN;
        } catch (NetworkException e) {
        }
        return getIcon(name);
    }
    

    static Icon getRegisterIcon(Register register) {
        return getIcon(REGISTER);
    }


    /**
     * Find the most fitting icon for the given endpoint
     */
    static Icon getEndpointIcon(Endpoint ep) {
        String name = ep.getName();
        String val = null;
        if (name != null) {
            val = findForName(name.toLowerCase().trim());
        }
        if (val == null) {
            val = ep.isOutput() ? DEFAULT_OUTPUT : DEFAULT_INPUT;
        }
        return getIcon(val);
    }
    
    private static Icon getIcon(String name) {
        ImageIcon ico = icons.get(name);
        if (ico == null) {
            try {
                ico = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/" + name)));
            } catch (IOException ex) {
                Logger.getLogger(NetworkTreeNodeRenderer.class.getName()).log(Level.SEVERE, null, ex);

            }
            icons.put(name, ico);
        }

        return ico;
    }


    private static String findForName(String name) {
        String val = map.get(name);
        if (val == null) {
            int idx = name.lastIndexOf(' ');
            if (idx > 0) {
                return findForName(name.substring(0, idx).trim());
            }
        }
        return val;
    }

    private static final Map<String, String> map = new HashMap<>();

    static {
        map.put("frequency", "metronome.png");
        map.put("binary", "document-binary.png");
        map.put("counter", "counter.png");
        map.put("voltage", "battery.png");
        map.put("humidity", "water.png");
        map.put("temperature", "thermometer.png");
        map.put("rgblevel", "color.png");
        map.put("red", "flag.png");
        map.put("green", "flag-green.png");
        map.put("blue", "flag-blue.png");
        map.put("moisture level", "water.png");
    }

    private static final Map<String, ImageIcon> icons = new HashMap<>();
    private static final String DEFAULT_OUTPUT = "switch.png";
    private static final String DEFAULT_INPUT = "light-bulb.png";
    private static final String PANSTAMP_KNOWN = "radio.png";
    private static final String PANSTAMP_UNKNOWN = "radio--exclamation.png";
    private static final String REGISTER = "processor.png";
//    private static final String STANDARD_REGISTER = "processor.png";
    private static final String NETWORK = "network-wireless.png";
    private static final String WORLD = "globe.png";
    


}
