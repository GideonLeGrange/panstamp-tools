package me.legrange.panstamp.gui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author gideon
 */
abstract class SWAPNode  extends DefaultMutableTreeNode {
    
    enum Type { GATEWAY, PANSTAMP, REGISTER, ENDPOINT };

    protected SWAPNode(Object userObject) {
        super(userObject);
    }
    
    abstract Type getType();
    
    
}
