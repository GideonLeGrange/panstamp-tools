package me.legrange.panstamp.gui.model.tree;

import java.util.Enumeration;
import java.util.Iterator;
import me.legrange.panstamp.Gateway;

/**
 * The top level node in the  networks tree. 
 * @author gideon
 */
public class WorldNode extends NetworkTreeNode<String, Gateway> {

    public WorldNode() {
        super("Networks");
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

    @Override
    protected void addToTree(NetworkTreeNode childNode, NetworkTreeNode parentNode) {
        tm.addToTree(childNode, parentNode);
    }

    @Override
    void addChild(Gateway gw) {
        GatewayNode gn = new GatewayNode(gw);
        addToTree(gn, this);
        gn.start();
    }
 
    void removeChild(Gateway gw) {
        Enumeration<GatewayNode> it = children();
        while (it.hasMoreElements()) {
            GatewayNode gn = it.nextElement();
            if (gn.getGateway() == gw) {
                remove(gn);
            }
        }
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
