/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.legrange.panstamp.gui.tree;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author gideon
 */
public class SWAPNodeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof SWAPNode) {
            SWAPNode node = (SWAPNode)value;
            switch (node.getType()) {
                case ENDPOINT : 
                    return renderEndpoint((EndpointNode)node);
                case REGISTER : 
                    return renderRegister((RegisterNode)node);
                case PANSTAMP : 
                    return renderPanStamp((PanStampNode)node);
                case GATEWAY : 
                    return renderGateway((GatewayNode)node);
                default : return null;
            }
        }
        else {
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }

    private Component renderEndpoint(EndpointNode endpointNode) {
        JPanel panel = new JPanel();
            panel.add(new JLabel(leafIcon));
        return panel;
    }

    private Component renderRegister(RegisterNode endpointNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Component renderPanStamp(PanStampNode endpointNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Component renderGateway(GatewayNode endpointNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
