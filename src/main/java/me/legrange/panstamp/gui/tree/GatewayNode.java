package me.legrange.panstamp.gui.tree;

import javax.swing.tree.DefaultTreeModel;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;

/**
 *
 * @author gideon
 */
public class GatewayNode extends SWAPNode implements GatewayListener {

    public GatewayNode(Gateway gw) {
        super(gw);
    }

    public Gateway getGateway() { 
        return (Gateway)getUserObject();
    }

    @Override
    public void deviceDetected(PanStamp ps) {
        PanStampNode psn = new PanStampNode(ps);
        addToTree(psn, this);
        psn.start();
    }

    @Override
    protected void start() {
        getGateway().addListener(this);
    }

    
    @Override
    Type getType() {
        return Type.GATEWAY;
    }
    
    
}
