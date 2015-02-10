package me.legrange.panstamp.gui.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayEvent;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampEvent;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.RegisterEvent;
import me.legrange.panstamp.RegisterListener;

/**
 *
 * @author gideon
 */
public class EndpointCollector implements GatewayListener, PanStampListener, RegisterListener {

    @Override
    public void gatewayUpdated(GatewayEvent ev) {
        switch (ev.getType()) {
            case DEVICE_DETECTED:
                add(ev.getDevice());
                break;
        }
    }

    @Override
    public void deviceUpdated(PanStampEvent ev) {
        switch (ev.getType()) {
            case REGISTER_DETECTED:
                add(ev.getRegister());
                break;
            case PRODUCT_CODE_UPDATE:
                remove(ev.getDevice());
                add(ev.getDevice());
                break;
        }
    }

    @Override
    public void registerUpdated(RegisterEvent ev) {
        switch (ev.getType()) {
            case ENDPOINT_ADDED:
                add(ev.getEndpoint());
                break;
        }
    }

    public EndpointCollector(Gateway gw) throws GatewayException {
        add(gw);
    }

    public EndpointDataSet getDataSet(Endpoint ep) {
        EndpointDataSet ds = sets.get(ep);
        if (ds == null) {
            ds = addDataSet(ep);
        }
        return ds;
    }

    private void remove(PanStamp ps) {
        ps.removeListener(this);
        try {
            for (Register reg : ps.getRegisters()) {
                remove(reg);
            }
        } catch (GatewayException ex) {
            Logger.getLogger(EndpointCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void remove(Register reg) {
        reg.removeListener(this);
        try {
            for (Endpoint ep : reg.getEndpoints()) {
                remove(ep);
            }
        } catch (GatewayException ex) {
            Logger.getLogger(EndpointCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void remove(Endpoint ep) {
        removeDataSet(ep);
    }

    private void add(Gateway gw) {
        gw.addListener(this);
        for (PanStamp ps : gw.getDevices()) {
            add(ps);
        }
    }

    private void add(PanStamp ps) {
        ps.addListener(this);
        try {
            for (Register reg : ps.getRegisters()) {
                add(reg);
            }
        } catch (GatewayException ex) {
            Logger.getLogger(EndpointCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void add(Register reg) {
        reg.addListener(this);
        try {
            for (Endpoint ep : reg.getEndpoints()) {
                add(ep);
            }
        } catch (GatewayException ex) {
            Logger.getLogger(EndpointCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void add(Endpoint ep) {
        addDataSet(ep);
    }

    private EndpointDataSet addDataSet(Endpoint ep) {
        EndpointDataSet ds = new EndpointDataSet(ep);
        sets.put(ep, ds);
        ep.addListener(ds);
        return ds;
    }

    private void removeDataSet(Endpoint ep) {
        sets.remove(ep);
    }

    private final Map<Endpoint, EndpointDataSet> sets = new HashMap<>();

}
