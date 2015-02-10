package me.legrange.panstamp.gui.model;

import me.legrange.panstamp.Gateway;

/**
 *
 * @author gideon
 */
public class WorldNode extends NetworkTreeNode {

    public WorldNode() {
        super("");
    }

    @Override
    public String toString() {
        return "SWAP Networks";
    }

    @Override
    protected void start() {
    }

    @Override
    public Type getType() {
        return Type.WORLD;
    }

    public synchronized void addGateway(Gateway gw) {
        GatewayNode gn = new GatewayNode(gw);
        addToTree(gn, this);
        gn.start();
    }

    @Override
    protected void addToTree(NetworkTreeNode childNode, NetworkTreeNode parentNode) {
        tm.addToTree(childNode, parentNode);
    }

    @Override
    protected void reload(NetworkTreeNode childNode) {
        tm.reload(childNode);
    }

    void setModel(NetworkTreeModel tm) {
        this.tm = tm;
    }

    private NetworkTreeModel tm;

}
