package me.legrange.panstamp.gui.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import me.legrange.panstamp.Gateway;

/**
 *
 * @author gideon
 */
public class SWAPTreeModel extends DefaultTreeModel {

    public static SWAPTreeModel create() {
        WorldNode wn = new WorldNode();
        SWAPTreeModel tm = new SWAPTreeModel(wn);
        wn.setModel(tm);
        return tm;
    }
    
    public void addGateway(Gateway gw) {
        ((WorldNode)getRoot()).addGateway(gw);
    }
    private SWAPTreeModel(WorldNode wn) {
        super(wn);
    }
 
    synchronized void  addToTree(SWAPNode childNode, MutableTreeNode parentNode) {
        insertNodeInto(childNode, parentNode, parentNode.getChildCount());
        reload(parentNode);
    }

    @Override
    public synchronized void reload(TreeNode node) {
        super.reload(node); 
    }
    
}
