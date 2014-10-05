package me.legrange.panstamp.gui.model;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author gideon
 */
abstract class SWAPNode extends DefaultMutableTreeNode {

    enum Type {

        WORLD, GATEWAY, PANSTAMP, REGISTER, ENDPOINT
    };

    @Override
    public abstract String toString();

    protected SWAPNode(Object userObject) {
        super(userObject);
    }

    protected abstract void start();

    protected void stop() {
        int c = getChildCount();
        for (int i = 0; i < c; ++i) {
            SWAPNode sn = (SWAPNode) getChildAt(c);
            sn.stop();
        }
    }

    protected void addToTree(SWAPNode childNode, SWAPNode parentNode) {
        ((SWAPNode) getParent()).addToTree(childNode, parentNode);
    }

    protected void reload(SWAPNode childNode) {
        ((SWAPNode) getParent()).reload(childNode);
    }

    protected final void reload() {
        reload(this);
    }

    abstract Type getType();

}
