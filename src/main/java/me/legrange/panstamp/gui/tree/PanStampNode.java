package me.legrange.panstamp.gui.tree;

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
public class PanStampNode extends SWAPNode implements PanStampListener {

    public PanStampNode(PanStamp ps) {
        super(ps);
    }

    public PanStamp getPanStamp() {
        return (PanStamp) getUserObject();
    }

    @Override
    protected void start() {
        try {
            getPanStamp().addListener(this);
            List<Register> regs = getPanStamp().getRegisters();
            for (Register reg : regs) {
                addNode(reg);

            }
        } catch (GatewayException ex) {
            Logger.getLogger(PanStampNode.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            }
            break;
            case SYNC_STATE_CHANGE : {
                System.out.printf("%s\n", ev.getDevice().getAddress());
            }
        }
    }

    private void addNode(Register reg) {
        RegisterNode rn = new RegisterNode(reg);
        addToTree(rn, this);
        rn.start();
        nodes.put(reg, rn);
    }

    private Map<Register, RegisterNode> nodes = new HashMap<>();

}
