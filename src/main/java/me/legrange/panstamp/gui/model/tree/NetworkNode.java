package me.legrange.panstamp.gui.model.tree;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.ModemException;
import me.legrange.panstamp.event.AbstractNetworkListener;
import me.legrange.swap.SwapModem;

/**
 * A node representing a panStamp gateway to a network.
 *
 * @author gideon
 */
public class NetworkNode extends NetworkTreeNode<Network, PanStamp> {

    public NetworkNode(Network gw) {
        super(gw);
    }

    public Network getNetwork() {
        return (Network) getUserObject();
    }

    @Override
    public String toString() {
        String txt;
        try {
            Network gw = getNetwork();
            SwapModem sm = gw.getSwapModem();
            switch (sm.getType()) {
                case SERIAL:
                    txt = String.format("Serial Network - %4x", gw.getNetworkId());
                    break;
                case TCP_IP:
                    txt = String.format("TCP/IP Network - %4x", gw.getNetworkId());
                    break;
                default:
                    txt = String.format("%4x", gw.getNetworkId());
            }
        } catch (ModemException ex) {
            Logger.getLogger(NetworkNode.class.getName()).log(Level.SEVERE, null, ex);
            txt = "Network";
        }
        return txt;
    }


    @Override
    protected void start() {
        getNetwork().addListener(listener);
        for (PanStamp ps : getNetwork().getDevices()) {
            addPanStamp(ps);
        }
    }

    @Override
    protected void stop() {
        getNetwork().removeListener(listener);
        super.stop();
    }

    @Override
    public Type getType() {
        return Type.NETWORK;
    }

    @Override
    public int compareTo(NetworkTreeNode<Network, PanStamp> o) {
        return 0;
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

    private final NetworkListener listener = new AbstractNetworkListener() {
            
        @Override
        public void deviceDetected(Network gw, PanStamp dev) {
            addPanStamp(dev);
        }

        @Override
        public void deviceRemoved(Network gw, PanStamp dev) {
            removePanStamp(dev);
        }

        @Override
        public void networkClosed(Network nw) {
            reload();
        }

        @Override
        public void networkOpened(Network nw) {
            reload();
        }
        
        

    };
}
