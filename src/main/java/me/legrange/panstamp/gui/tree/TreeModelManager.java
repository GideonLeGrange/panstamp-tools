package me.legrange.panstamp.gui.tree;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.EndpointListener;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.Register;

/**
 *
 * @author gideon
 */
public class TreeModelManager {

    public TreeModelManager() {
        root = new DefaultMutableTreeNode("Root");
        model = new DefaultTreeModel(root);
    }

    public DefaultTreeModel getModel() {
        return model;
    }

    public void addGateway(Gateway gw) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Network");
        NetworkListener l = new NetworkListener(gw, node);
        insert(node, root); //model.insertNodeInto(node, root, root.getChildCount());
        gw.addListener(l);

    }

    private final DefaultMutableTreeNode root;
    private final DefaultTreeModel model;
    
    private class ValueListener implements EndpointListener {

        @Override
        public void valueReceived(Endpoint ep) {
            try {
                List<String> units = ep.getUnits();
                String txt;
                if (!units.isEmpty()) {
                    String unit = units.get(0);
                    txt = String.format("%s %s", ep.getValue(unit), unit);
                }
                else {
                    txt = ep.getValue().toString();
                }
                JLabel content = new JLabel(String.format("%s = %s", ep.getName(), txt));
                node.setUserObject(content);
                model.reload(node);
                System.out.printf("Updated %s to %s\n", ep.getName(), ep.getValue());
            } catch (GatewayException ex) {
                Logger.getLogger(TreeModelManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private ValueListener(Endpoint ep, DefaultMutableTreeNode node) {
            this.node = node;
            this.ep = ep;
        }
        
        private final Endpoint ep;
        private final DefaultMutableTreeNode node;
    }
    
    private class NetworkListener implements GatewayListener {

        @Override
        public void deviceDetected(PanStamp ps) {
            try {
                String desc = String.format("Mote %d", ps.getAddress());
                DefaultMutableTreeNode psNode = new DefaultMutableTreeNode(desc);
                insert(psNode, node); //model.insertNodeInto(psNode, node, node.getChildCount());
                for (Register reg : ps.getRegisters()) {
                    DefaultMutableTreeNode regNode = new DefaultMutableTreeNode("Register " + reg.getId());
                    insert(regNode, psNode);
                    for (Endpoint ep : reg.getEndpoints()) {
                        DefaultMutableTreeNode epNode = new DefaultMutableTreeNode(ep.getName() + " = ");
                        insert(epNode, regNode);
                        ep.addListener(new ValueListener(ep, epNode ));
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(TreeModelManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private NetworkListener(Gateway gw, DefaultMutableTreeNode node) {
            this.node = node;
            this.gw = gw;
        }

        private final Gateway gw;
        private final DefaultMutableTreeNode node;

    }

    private void insert(final DefaultMutableTreeNode child, final DefaultMutableTreeNode parent) {
        model.insertNodeInto(child, parent,  parent.getChildCount()); //, root, index);
        model.reload();
    }

}
