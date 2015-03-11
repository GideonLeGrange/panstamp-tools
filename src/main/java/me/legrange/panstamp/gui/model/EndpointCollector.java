package me.legrange.panstamp.gui.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.RegisterListener;
import me.legrange.panstamp.event.AbstractGatewayListener;
import me.legrange.panstamp.event.AbstractPanStampListener;
import me.legrange.panstamp.event.AbstractRegisterListener;

/**
 *
 * @author gideon
 */
public class EndpointCollector {

    public EndpointCollector(Gateway gw) throws GatewayException {
        this.gw = gw;
        add(gw);
    }

    public EndpointDataSet getDataSet(Endpoint ep) {
        EndpointDataSet ds = sets.get(ep);
        if (ds == null) {
            ds = addDataSet(ep);
        }
        return ds;
    }

    public void stop() {
        remove(gw);
    }

    private void remove(PanStamp ps) {
        ps.removeListener(panStampL);
        for (Register reg : ps.getRegisters()) {
            remove(reg);
        }
    }

    private void remove(Register reg) {
        reg.removeListener(registerL);
        try {
            for (Endpoint ep : reg.getEndpoints()) {
                remove(ep);
            }
        } catch (GatewayException ex) {
            Logger.getLogger(EndpointCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void remove(Gateway gw) {
        gw.removeListener(gatewayL);
    }

    private void remove(Endpoint ep) {
        removeDataSet(ep);
    }

    private void add(Gateway gw) {
        gw.addListener(gatewayL);
        for (PanStamp ps : gw.getDevices()) {
            add(ps);
        }
    }

    private void add(PanStamp ps) {
        ps.addListener(panStampL);
        for (Register reg : ps.getRegisters()) {
            add(reg);
        }
    }

    private void add(Register reg) {
        reg.addListener(registerL);
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

    private final Gateway gw;
    private final Map<Endpoint, EndpointDataSet> sets = new HashMap<>();

    private final GatewayListener gatewayL = new AbstractGatewayListener() {

        @Override
        public void deviceRemoved(Gateway gw, PanStamp dev) {
            add(dev);
        }

        @Override
        public void deviceDetected(Gateway gw, PanStamp dev) {
            remove(dev);
        }
    };

    private final PanStampListener panStampL = new AbstractPanStampListener() {

        @Override
        public void registerDetected(PanStamp dev, Register reg) {
            add(reg);
        }

        @Override
        public void productCodeChange(PanStamp dev, int manufacturerId, int productId) {
            remove(dev);
            add(dev);
        }

    };
    private final RegisterListener registerL = new AbstractRegisterListener() {

        @Override
        public void endpointAdded(Register reg, Endpoint ep) {
            add(ep);
        }

    };

}
