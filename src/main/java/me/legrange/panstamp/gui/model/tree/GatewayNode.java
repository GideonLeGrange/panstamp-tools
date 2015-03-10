package me.legrange.panstamp.gui.model.tree;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.impl.ModemException;
import me.legrange.swap.SWAPModem;

/**
 * A node representing a panStamp gateway to a network. 
 * @author gideon
 */
public class GatewayNode extends NetworkTreeNode<Gateway, PanStamp> implements GatewayListener {

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
            Gateway gw = getGateway();
            SWAPModem sm = gw.getSWAPModem();
            switch (sm.getType()) {
                case SERIAL : txt = String.format("Serial Network - %4x",  gw.getNetworkId());  
                    break;
                case TCP_IP : txt = String.format("TCP/IP Network - %4x", gw.getNetworkId());
                    break;
                default : 
                    txt = String.format("%4x", gw.getNetworkId());
            }
        } catch (ModemException ex) {
            Logger.getLogger(GatewayNode.class.getName()).log(Level.SEVERE, null, ex);
            txt = "Network";
        } catch (GatewayException ex) {
            Logger.getLogger(GatewayNode.class.getName()).log(Level.SEVERE, null, ex);
            txt = "Network";
        }
        return txt;
    }

    @Override
    public void deviceDetected(Gateway gw, PanStamp dev) {
        addPanStamp(dev);
    }

    @Override
    public void deviceRemoved(Gateway gw, PanStamp dev) {
        removePanStamp(dev);
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
    public Type getType() {
        return Type.GATEWAY;
    }

    private synchronized void addPanStamp(PanStamp ps) {
        addChild(ps);
    }
    
    private synchronized void removePanStamp(PanStamp ps) {
        for (int i = 0; i < getChildCount(); ++i) {
            PanStampNode psn = (PanStampNode) getChildAt(i);
            if (psn.getPanStamp() == ps) {
                psn.stop();
                removeFromTree(psn, this);
                return;
            }
        }
    }

    @Override
    void addChild(PanStamp ps) {
        PanStampNode psn = new PanStampNode(ps);
        addToTree(psn, this);
        psn.start();
    }

}
