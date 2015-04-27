package me.legrange.panstamp.gui.model.tree;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.ModemException;
import me.legrange.swap.SwapModem;

/**
 * A node representing a panStamp gateway to a network. 
 * @author gideon
 */
public class NetworkNode extends NetworkTreeNode<Network, PanStamp> implements NetworkListener {

    public NetworkNode(Network gw) {
        super(gw);
    }

    public Network getGateway() {
        return (Network) getUserObject();
    }

    @Override
    public String toString() {
        String txt;
        try {
            Network gw = getGateway();
            SwapModem sm = gw.getSWAPModem();
            switch (sm.getType()) {
                case SERIAL : txt = String.format("Serial Network - %4x",  gw.getNetworkId());  
                    break;
                case TCP_IP : txt = String.format("TCP/IP Network - %4x", gw.getNetworkId());
                    break;
                default : 
                    txt = String.format("%4x", gw.getNetworkId());
            }
        } catch (ModemException ex) {
            Logger.getLogger(NetworkNode.class.getName()).log(Level.SEVERE, null, ex);
            txt = "Network";
        } 
        return txt;
    }

    @Override
    public void deviceDetected(Network gw, PanStamp dev) {
        addPanStamp(dev);
    }

    @Override
    public void deviceRemoved(Network gw, PanStamp dev) {
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
        return Type.NETWORK;
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
