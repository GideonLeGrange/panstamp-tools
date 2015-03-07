package me.legrange.panstamp.gui.model.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author gideon
 */
public abstract class NetworkTreeNode<S,C> extends DefaultMutableTreeNode {

    public enum Type {

        WORLD, GATEWAY, PANSTAMP, REGISTER, ENDPOINT
    };
    
    @Override
    public void remove(MutableTreeNode child) {
        ((NetworkTreeNode) child).stop();
        super.remove(child);
        reload(this);
    }

    @Override
    public abstract String toString();

    protected NetworkTreeNode(S userObject) {
        super(userObject);
    }

    abstract void addChild(C child);
    
    protected abstract void start();

    protected void stop() {
        int c = getChildCount();
        for (int i = 0; i < c; ++i) {
            NetworkTreeNode sn = (NetworkTreeNode) getChildAt(i);
            if (sn != null  ) {
                sn.stop();
            }
        }
    }

    protected void addToTree(NetworkTreeNode childNode, NetworkTreeNode parentNode) {
        ((NetworkTreeNode) getParent()).addToTree(childNode, parentNode);
    }
    
    protected void removeFromTree(NetworkTreeNode childNode,NetworkTreeNode parentNode) {
        parentNode.remove(childNode);
    }

    protected void reload(NetworkTreeNode childNode) {
        ((NetworkTreeNode) getParent()).reload(childNode);
    }

    protected final void reload() {
        reload(this);
    }

    public abstract Type getType();

}
