package me.legrange.panstamp.gui.model;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import me.legrange.panstamp.Gateway;

/**
 *
 * @author gideon
 */
class NetworkTreeModel extends DefaultTreeModel {

    public void addGateway(Gateway gw) {
        ((WorldNode) getRoot()).addGateway(gw);
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

    static NetworkTreeModel create() {
        WorldNode wn = new WorldNode();
        NetworkTreeModel tm = new NetworkTreeModel(wn);
        wn.setModel(tm);
        return tm;
    }
}
