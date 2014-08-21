package me.legrange.panstamp.gui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author gideon
 */
abstract class SWAPNode  extends DefaultMutableTreeNode {
    
    enum Type { WORLD, GATEWAY, PANSTAMP, REGISTER, ENDPOINT };

    protected SWAPNode(Object userObject) {
        super(userObject);
    }
    
    protected abstract void start();
    
    protected void addToTree(SWAPNode childNode, SWAPNode parentNode) {
        ((SWAPNode)getParent()).addToTree(childNode, parentNode);
    }
    
    protected void reload(SWAPNode childNode) {
        ((SWAPNode)getParent()).reload(childNode);
    }
    
    protected final void reload() {
        reload(this);
    }
    
    abstract Type getType();
    
    
}
