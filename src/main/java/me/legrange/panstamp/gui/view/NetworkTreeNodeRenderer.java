package me.legrange.panstamp.gui.view;

import me.legrange.panstamp.gui.model.Model;
import me.legrange.panstamp.gui.model.Format;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
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
        this.view =view;
    }

    private Component renderEndpoint(EndpointNode epn) throws NetworkException {
        JLabel label = new JLabel(epn.toString(), getIcon(ICON_ENDPOINT), JLabel.LEADING);
        return label;
    }

    private Component renderRegister(RegisterNode rn) {
        return new JLabel(rn.toString(), getIcon(ICON_REGISTER), JLabel.LEADING);

    }

    private Component renderPanStamp(PanStampNode psn) {
        return new JLabel(psn.toString(), getIcon(ICON_DEVICE), JLabel.LEADING);
    }

    private Component renderNetwork(NetworkNode gn) {
        return new JLabel(gn.toString(), getIcon(ICON_NETWORK), JLabel.LEADING);
    }

    private Component renderWorld(WorldNode wn) {
        return new JLabel(wn.toString(), getIcon(ICON_WORLD), JLabel.LEADING);
    }

    private String formatValue(Endpoint ep) throws NetworkException {
        return Format.formatValue(ep);
    }

    private Icon getIcon(String name) {
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

    private static final String ICON_WORLD = "world16x16.png";
    private static final String ICON_NETWORK = "network16x16.png";
    private static final String ICON_DEVICE = "device16x16.png";
    private static final String ICON_REGISTER = "register16x16.png";
    private static final String ICON_ENDPOINT = "endpoint16x16.png";
    private static final String ICON_STOP_NETWORK = "stop16x16.png";
    private static final String ICON_START_NETWORK = "play16x16.png";
    private final Map<String, ImageIcon> icons = new HashMap<>();
    private final Model model;
    private final View view;
}
