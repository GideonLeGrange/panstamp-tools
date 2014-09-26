/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.legrange.panstamp.gui.tree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.gui.Format;
import me.legrange.panstamp.impl.ModemException;
import me.legrange.swap.ModemSetup;
import me.legrange.swap.SWAPException;

/**
 *
 * @author gideon
 */
public class SWAPNodeRenderer extends DefaultTreeCellRenderer {
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof SWAPNode) {
            try {
                SWAPNode node = (SWAPNode) value;
                switch (node.getType()) {
                    case ENDPOINT:
                        return renderEndpoint((EndpointNode) node);
                    case REGISTER:
                        return renderRegister((RegisterNode) node);
                    case PANSTAMP:
                        return renderPanStamp((PanStampNode) node);
                    case GATEWAY:
                        return renderGateway((GatewayNode) node);
                    case WORLD:
                        return renderWorld((WorldNode) node);
                }
            } catch (GatewayException ex) {
                Logger.getLogger(SWAPNodeRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
    
    private Component renderEndpoint(EndpointNode epn) throws GatewayException {
        JLabel label = new JLabel(String.format("%s = %s", epn.getEndpoint().getName(), formatValue(epn.getEndpoint())), getIcon(ICON_ENDPOINT), JLabel.LEADING);
        return label;
    }
    
    private Component renderRegister(RegisterNode rn) {
        return new JLabel(String.format("Register %d: %s", rn.getRegister().getId(), rn.getRegister().getName()), getIcon(ICON_REGISTER), JLabel.LEADING);
        
    }
    
    private Component renderPanStamp(PanStampNode psn) {
        JLabel label = new JLabel(String.format("Mote %d: %s", psn.getPanStamp().getAddress(), psn.getPanStamp().getName()), getIcon(ICON_DEVICE), JLabel.LEADING);
        JPopupMenu menu = new JPopupMenu();
        final JMenuItem settingsItem = new JMenuItem("Settings...");
        settingsItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
             throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
         });
        menu.add(settingsItem);
        final JMenuItem graphItem = new JMenuItem("RSSI/LQI Graph...");
        graphItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        menu.add(graphItem);
        label.add(menu);
        return label;
    }
    
    private Component renderGateway(GatewayNode gn) {
        String txt;
        try {
            ModemSetup setup = gn.getGateway().getSWAPModem().getSetup();
            txt = String.format("Network %4x", setup.getNetworkID());
        } catch (SWAPException ex) {
            txt = "Network";
        }
        return new JLabel(txt, getIcon(ICON_NETWORK), JLabel.LEADING);
    }
    
    private Component renderWorld(WorldNode wn) {
        return new JLabel("", getIcon(ICON_WORLD), JLabel.LEADING);
    }
    
    private String formatValue(Endpoint ep) throws GatewayException {
        return Format.formatValue(ep);
    }
    
    private Icon getIcon(String name) {
        ImageIcon ico = icons.get(name);
        if (ico == null) {
            try {
                ico = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream("images/" + name)));
                /*                    Image image = ico.getImage(); // transform it 
                 Image newimg = image.getScaledInstance(16, 16,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                 ico  = new ImageIcon(newimg);  // transform it back */
            } catch (IOException ex) {
                Logger.getLogger(SWAPNodeRenderer.class.getName()).log(Level.SEVERE, null, ex);
                
            }
            icons.put(name, ico);
        }
        
        return ico;
    }
    
    private static final String ICON_WORLD = "world16x16.png";
    private static final String ICON_NETWORK = "network16x16.png";
    private static final String ICON_DEVICE = "device16x16.png";
    private static final String ICON_REGISTER = "register16x16.png";
    private static final String ICON_ENDPOINT = "endpoint16x16.png";
    private final Map<String, ImageIcon> icons = new HashMap<>();
    
}
