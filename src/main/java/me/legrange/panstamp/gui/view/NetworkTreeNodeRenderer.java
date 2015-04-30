package me.legrange.panstamp.gui.view;

import java.awt.Color;
import me.legrange.panstamp.gui.model.Model;
import me.legrange.panstamp.gui.model.Format;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.gui.model.tree.EndpointNode;
import me.legrange.panstamp.gui.model.tree.NetworkNode;
import me.legrange.panstamp.gui.model.tree.NetworkTreeNode;
import me.legrange.panstamp.gui.model.tree.PanStampNode;
import me.legrange.panstamp.gui.model.tree.RegisterNode;
import me.legrange.panstamp.gui.model.tree.WorldNode;

/**
 *
 * @author gideon
 */
public class NetworkTreeNodeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof NetworkTreeNode) {
            try {
                NetworkTreeNode node = (NetworkTreeNode) value;
                Component com;
                switch (node.getType()) {
                    case ENDPOINT:
                        com = renderEndpoint((EndpointNode) node);
                        break;
                    case REGISTER:
                        com = renderRegister((RegisterNode) node);
                        break;
                    case PANSTAMP:
                        com = renderPanStamp((PanStampNode) node);
                        break;
                    case NETWORK:
                        com = renderNetwork((NetworkNode) node);
                        break;
                    case WORLD:
                        com = renderWorld((WorldNode) node);
                        break;
                    default:
                        com = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                        break;
                }
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                panel.add(com);
                panel.setBackground(sel ? backgroundSelectionColor : tree.getBackground());
                com.setForeground(sel ? textSelectionColor : textNonSelectionColor);
                return panel;
            } catch (NetworkException ex) {
                Logger.getLogger(NetworkTreeNodeRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

    public NetworkTreeNodeRenderer(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    private Component renderEndpoint(EndpointNode epn) throws NetworkException {
        return new JLabel(epn.toString(), IconMap.getEndpointIcon(epn.getEndpoint()), JLabel.LEADING);
    }

    private Component renderRegister(RegisterNode rn) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(new JLabel(IconMap.getRegisterIcon(rn.getRegister())));
        panel.add(new JLabel(rn.toString()));
        return panel;
    }

    private Component renderPanStamp(PanStampNode psn) {
        return new JLabel(psn.toString(), IconMap.getPanStampIcon(psn.getPanStamp()), JLabel.LEADING);
    }

    private Component renderNetwork(NetworkNode gn) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel(IconMap.getNetworkIcon(gn.getNetwork()), JLabel.LEADING));
        panel.add(new JLabel(gn.toString()));
        //       panel.add(new JLabel(getIcon(gn.getNetwork().isOpen() ? ICON_OPEN : ICON_CLOSED)));
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.setOpaque(false);
        return panel;
        //       return new JLabel(gn.toString(), getIcon(ICON_NETWORK), JLabel.LEADING);
    }

    private Component renderWorld(WorldNode wn) {
        return new JLabel(wn.toString(), IconMap.getWorldIcon(), JLabel.LEADING);
    }

    private String formatValue(Endpoint ep) throws NetworkException {
        return Format.formatValue(ep);
    }

    private final Model model;
    private final View view;
}
