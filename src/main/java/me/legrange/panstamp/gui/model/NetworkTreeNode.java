package me.legrange.panstamp.gui.model;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author gideon
 */
public abstract class NetworkTreeNode extends DefaultMutableTreeNode {

    public enum Type {

        WORLD, GATEWAY, PANSTAMP, REGISTER, ENDPOINT
    };

    @Override
    public abstract String toString();

    protected NetworkTreeNode(Object userObject) {
        super(userObject);
    }

    protected abstract void start();

    protected void stop() {
        int c = getChildCount();
        for (int i = 0; i < c; ++i) {
            NetworkTreeNode sn = (NetworkTreeNode) getChildAt(c);
            sn.stop();
        }
    }

    protected void addToTree(NetworkTreeNode childNode, NetworkTreeNode parentNode) {
        ((NetworkTreeNode) getParent()).addToTree(childNode, parentNode);
    }

    protected void reload(NetworkTreeNode childNode) {
        ((NetworkTreeNode) getParent()).reload(childNode);
    }

    protected final void reload() {
        reload(this);
    }

    public abstract Type getType();

}
