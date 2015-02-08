package me.legrange.panstamp.gui.model.tree;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.RegisterEvent;
import me.legrange.panstamp.RegisterListener;

/**
 *
 * @author gideon
 */
public class RegisterNode extends NetworkTreeNode implements RegisterListener {

    public RegisterNode(Register reg) {
        super(reg);
    }

    public Register getRegister() {
        return (Register) getUserObject();
    }

    @Override
    public String toString() {
        return String.format("Register %d: %s", getRegister().getId(), getRegister().getName());
    }

    @Override
    protected synchronized void start() {
        try {
            getRegister().addListener(this);
            for (Endpoint ep : getRegister().getEndpoints()) {
                EndpointNode epn = new EndpointNode(ep);
                addToTree(epn, this);
                epn.start();
            }
        } catch (GatewayException ex) {
            Logger.getLogger(RegisterNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void stop() {
        getRegister().removeListener(this);
        super.stop();
    }
    
    

    void update(Register reg) {
        try {
            if (reg.getEndpoints().size() < getRegister().getEndpoints().size()) {
                for (Endpoint ep : getRegister().getEndpoints()) {
                    EndpointNode epn = new EndpointNode(ep);
                    addToTree(epn, this);
                    epn.start();
                }
                reload();
            }
        } catch (GatewayException ex) {
            Logger.getLogger(RegisterNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Type getType() {
        return Type.REGISTER;
    }

    @Override
    public void registerUpdated(RegisterEvent ev) {
        switch (ev.getType()) {
            case ENDPOINT_ADDED:
                try {
                    for (Endpoint ep : getRegister().getEndpoints()) {
                        EndpointNode epn = new EndpointNode(ep);
                        addToTree(epn, this);
                        epn.start();
                    }
                    reload();
                } catch (GatewayException ex) {
                    Logger.getLogger(RegisterNode.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
}
