package me.legrange.panstamp.gui.model.tree;

import java.util.Enumeration;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import me.legrange.panstamp.Network;

/**
 *
 * @author gideon
 */
public class NetworkTreeModel extends DefaultTreeModel {

    public void addGateway(Network gw) {
        ((WorldNode) getRoot()).addChild(gw);
    }
    
    public void removeGateway(Network gw) {
        ((WorldNode) getRoot()).removeChild(gw);
    }

    private NetworkTreeModel(WorldNode wn) {
        super(wn);
    }

    synchronized void addToTree(NetworkTreeNode childNode, MutableTreeNode parentNode) {
        int pos = 0;
        Enumeration peers = parentNode.children();
        while (peers.hasMoreElements()) {
            NetworkTreeNode peer = (NetworkTreeNode) peers.nextElement();
            if (childNode.compareTo(peer) > 0) {
              pos ++;
            }
            else {
                break;
            }
        }
        insertNodeInto(childNode, parentNode, pos   );
//        insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        reload(parentNode);
    }

    @Override
    public synchronized void reload(TreeNode node) {
        super.reload(node);
    }

    public static NetworkTreeModel create() {
        WorldNode wn = new WorldNode();
        NetworkTreeModel tm = new NetworkTreeModel(wn);
        wn.setModel(tm);
        return tm;
    }

}
