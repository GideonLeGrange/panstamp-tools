package me.legrange.panstamp.gui.model;

import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayEvent;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.swap.ModemSetup;
import me.legrange.swap.SWAPException;

/**
 *
 * @author gideon
 */
class GatewayNode extends SWAPNode implements GatewayListener {

    public GatewayNode(Gateway gw) {
        super(gw);
    }

    public Gateway getGateway() {
        return (Gateway) getUserObject();
    }

    @Override
    public String toString() {
        String txt;
        try {
            ModemSetup setup = getGateway().getSWAPModem().getSetup();
            txt = String.format("Network %4x", setup.getNetworkID());
        } catch (SWAPException ex) {
            txt = "Network";
        }
        return txt;
    }

    @Override
    public void gatewayUpdated(GatewayEvent ev) {
        switch (ev.getType()) {
            case DEVICE_DETECTED: {
                addPanStamp(ev.getDevice());
            }
            break;
        }
    }

    @Override
    protected void start() {
        getGateway().addListener(this);
        for (PanStamp ps : getGateway().getDevices()) {
            addPanStamp(ps);
        }
    }
    
    @Override
    protected void stop() {
        getGateway().removeListener(this);
        super.stop();
    }

    @Override
    Type getType() {
        return Type.GATEWAY;
    }

    private synchronized void addPanStamp(PanStamp ps) {
        PanStampNode psn = new PanStampNode(ps);
        addToTree(psn, this);
        psn.start();
    }

}
