package me.legrange.panstamp.gui.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampEvent;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;

/**
 *
 * @author gideon
 */
class PanStampNode extends NetworkTreeNode implements PanStampListener {

    public PanStampNode(PanStamp ps) {
        super(ps);
    }

    public PanStamp getPanStamp() {
        return (PanStamp) getUserObject();
    }

    @Override
    public String toString() {
        return String.format("Mote %d: %s", getPanStamp().getAddress(), getPanStamp().getName());
    }

    @Override
    protected void start() {
        try {
            getPanStamp().addListener(this);
            for (Register reg : getPanStamp().getRegisters()) {
                addRegister(reg);
            }
        } catch (GatewayException ex) {
            Logger.getLogger(PanStampNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void stop() {
        getPanStamp().removeListener(this);
        super.stop(); 
    }

    @Override
    Type getType() {
        return Type.PANSTAMP;
    }

    @Override
    public void deviceUpdated(PanStampEvent ev) {
        switch (ev.getType()) {
            case PRODUCT_CODE_UPDATE: {
                try {
                    for (Register reg : ev.getDevice().getRegisters()) {
                        RegisterNode rn = nodes.get(reg);
                        if (rn == null) {
                            addNode(reg);
                            reload();
                        } else {
                            rn.update(reg);
                            rn.reload();
                        }
                    }
                } catch (GatewayException ex) {
                    Logger.getLogger(PanStampNode.class.getName()).log(Level.SEVERE, null, ex);
                }
                reload();
            }
            break;
            case SYNC_STATE_CHANGE: {

            }
            break;
            case REGISTER_DETECTED:
                Register reg = ev.getRegister();
                if (nodes.get(reg) == null) {
                    addNode(reg);
                }
        }
    }

    private synchronized void addRegister(Register reg) {
        if (nodes.get(reg) == null) {
            addNode(reg);
        }
    }

    private void addNode(Register reg) {
        RegisterNode old = nodes.remove(reg);
        if (old != null) {
            old.stop();
        }
        RegisterNode rn = new RegisterNode(reg);
        addToTree(rn, this);
        rn.start();
        nodes.put(reg, rn);
    }

    private Map<Register, RegisterNode> nodes = new HashMap<>();

}
