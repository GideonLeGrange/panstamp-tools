package me.legrange.panstamp.gui.model.tree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.tree.TreeNode;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.Gateway;

/**
 * A tree node representing a panStamp device in the network tree.
 *
 * @author gideon
 */
public class PanStampNode extends NetworkTreeNode<PanStamp, Register> implements PanStampListener {

    @Override
    void addChild(Register reg) {
        RegisterNode old = nodes.remove(reg.getId());
        if (old != null) {
            old.stop();
        }
        RegisterNode rn = new RegisterNode(reg);
        addToTree(rn, this);
        rn.start();
        nodes.put(reg.getId(), rn);
    }

    public enum RegisterDisplay {

        ALL, NONE, INTERESTING;
    };

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
            case ALL:
                return super.getChildCount();
            case NONE:
                int count = 0;
                for (RegisterNode rn : nodes.values()) {
                    if (rn.getRegister().getId() > 10) {
                        count++;
                    }
                }
                return count;
            case INTERESTING:
                count = 0;
                for (RegisterNode rn : nodes.values()) {
                    if ((rn.getRegister().getId() <= 10)) {
                        if (rn.getRegister().hasValue()) {
                            count++;
                        }
                    } else {
                        count++;
                    }
                }
                return count;
        }
        return 0;
    }

    @Override
    public TreeNode getChildAt(int index) {
        switch (registerDisplay) {
            case ALL:
                return super.getChildAt(index);
            case NONE:
                int idx = 0;
                for (RegisterNode rn : nodes.values()) {
                    if ((rn.getRegister().getId() > 10)) {
                        if (idx == index) {
                            return rn;
                        } else {
                            idx++;
                        }
                    }
                }
                return null;
            case INTERESTING:
                idx = 0;
                for (RegisterNode rn : nodes.values()) {
                    if (rn.getRegister().getId() <= 10) {
                        if (rn.getRegister().hasValue()) {
                            if (idx == index) {
                                return rn;
                            } else {
                                idx++;
                            }
                        }
                    } else {
                        if (idx == index) {
                            return rn;
                        } else {
                            idx++;
                        }
                    }
                }
        }
        return null;
    }

    @Override
    protected void start() {
        getPanStamp().addListener(this);
        for (Register reg : getPanStamp().getRegisters()) {
            addRegister(reg);
        }
    }

    @Override
    protected void stop() {
        ((Gateway) getPanStamp().getGateway()).removeDevice(getPanStamp().getAddress());
        super.stop();
    }

    @Override
    public Type getType() {
        return Type.PANSTAMP;
    }


    @Override
    public void productCodeChange(PanStamp dev, int manufacturerId, int productId) {
        for (Register reg : dev.getRegisters()) {
            RegisterNode rn = nodes.get(reg.getId());
            if (rn == null) {
                addChild(reg);
                reload();
            } else {
                rn.update(reg);
                rn.reload();
            }
        }
    }

    @Override
    public void syncStateChange(PanStamp dev, int syncState) {
    }

    @Override
    public void registerDetected(PanStamp dev, Register reg) {
        if (nodes.get(reg.getId()) == null) {
            addChild(reg);
        }
    }

    @Override
    public void syncRequired(PanStamp dev) {
    }

    public RegisterDisplay getRegisterDisplay() {
        return registerDisplay;
    }

    public void setRegisterDisplay(RegisterDisplay rd) {
        if (rd != registerDisplay) {
            registerDisplay = rd;
            reload();
        }
    }

    private synchronized void addRegister(Register reg) {
        if (nodes.get(reg.getId()) == null) {
            addChild(reg);
        }
    }

    private final Map<Integer, RegisterNode> nodes = new ConcurrentHashMap<>();
    private RegisterDisplay registerDisplay = RegisterDisplay.NONE;
}
