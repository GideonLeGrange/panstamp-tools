package me.legrange.panstamp.gui.tree;

import me.legrange.panstamp.Gateway;

/**
 *
 * @author gideon
 */
public class GatewayNode extends SWAPNode {

    public GatewayNode(Gateway gw) {
        super(gw);
    }
    
    @Override
    Type getType() {
        return Type.GATEWAY;
    }
    
}
