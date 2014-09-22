package me.legrange.panstamp.gui.tree;

import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayEvent;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampEvent;

/**
 *
 * @author gideon
 */
public class GatewayNode extends SWAPNode implements GatewayListener {

    public GatewayNode(Gateway gw) {
        super(gw);
    }

    public Gateway getGateway() {
        return (Gateway) getUserObject();
    }

    @Override
    public void gatewayUpdated(GatewayEvent ev) {
        switch (ev.getType()) {
            case DEVICE_DETECTED : {
                PanStamp ps = ev.getDevice();
                PanStampNode psn = new PanStampNode(ps);
                addToTree(psn, this);
                psn.start();
            }
            break;
        }
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
