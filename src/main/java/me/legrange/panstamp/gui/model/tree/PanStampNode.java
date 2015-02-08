package me.legrange.panstamp.gui.model.tree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.TreeNode;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampEvent;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;

/**
 *
 * @author gideon
 */
public class PanStampNode extends NetworkTreeNode implements PanStampListener {
    
    public enum RegisterDisplay { ALL, NONE, INTERESTING; };

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
    public int getChildCount() {
        switch (registerDisplay) {
            case ALL : 
                return super.getChildCount(); 
            case NONE : 
                int count = 0;
                for (RegisterNode rn : nodes.values()) {
                    if (rn.getRegister().getId() > 10) {
                        count ++;
                    }
                }
                return count;
            case INTERESTING : 
                count = 0;
                for (RegisterNode rn : nodes.values()) {
                    if ((rn.getRegister().getId() <= 10)) {
                        if (rn.getRegister().hasValue()) {
                            count ++;
                        }
                    }
                    else {
                        count ++;
                    }
                }
                return count;
        }
        return 0;
    }

    @Override
    public TreeNode getChildAt(int index) {
        switch (registerDisplay) {
            case ALL : 
                return super.getChildAt(index);
            case NONE : 
                int idx = 0;
                for (RegisterNode rn : nodes.values()) {
                    if ((rn.getRegister().getId() > 10)) {
                        if (idx == index) {
                            return rn;
                        }
                        else {
                            idx ++;
                        }
                    }
                }
                return null;
            case INTERESTING :
                idx = 0;
                for (RegisterNode rn : nodes.values()) {
                    if (rn.getRegister().getId() <= 10) {
                        if (rn.getRegister().hasValue()) {
                            if (idx == index) {
                                return rn;
                            }
                            else {
                                idx ++;
                            }
                        }
                    }
                    else {
                            if (idx == index) {
                                return rn;
                            }
                            else {
                                idx ++;
                            }
                    }
                }
        }
        return null;
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
    public Type getType() {
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
    
    void setRegisterDisplay(RegisterDisplay rd) {
        if (rd != registerDisplay) {
            registerDisplay = rd;
            reload();
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

    private final Map<Register, RegisterNode> nodes = new ConcurrentHashMap<>();
    private RegisterDisplay registerDisplay = RegisterDisplay.NONE; 
}
