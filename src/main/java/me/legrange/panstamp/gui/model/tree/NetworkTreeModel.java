package me.legrange.panstamp.gui.model.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import me.legrange.panstamp.Gateway;

/**
 *
 * @author gideon
 */
public class NetworkTreeModel extends DefaultTreeModel {

    public void addGateway(Gateway gw) {
        ((WorldNode) getRoot()).addChild(gw);
    }
    
    public void removeGateway(Gateway gw) {
        ((WorldNode) getRoot()).removeChild(gw);
    }

    private NetworkTreeModel(WorldNode wn) {
        super(wn);
    }

    synchronized void addToTree(NetworkTreeNode childNode, MutableTreeNode parentNode) {
        insertNodeInto(childNode, parentNode, parentNode.getChildCount());
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
