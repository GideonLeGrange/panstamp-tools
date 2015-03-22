package me.legrange.panstamp.gui.model;

import java.util.HashMap;
import java.util.Map;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.NetworkListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.RegisterListener;
import me.legrange.panstamp.event.AbstractNetworkListener;
import me.legrange.panstamp.event.AbstractPanStampListener;
import me.legrange.panstamp.event.AbstractRegisterListener;

/**
 *
 * @author gideon
 */
public class EndpointCollector {

    public EndpointCollector(Network gw) throws NetworkException {
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
        for (Endpoint ep : reg.getEndpoints()) {
            remove(ep);
        }
    }

    private void remove(Network gw) {
        gw.removeListener(gatewayL);
    }

    private void remove(Endpoint ep) {
        removeDataSet(ep);
    }

    private void add(Network gw) {
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
        for (Endpoint ep : reg.getEndpoints()) {
            add(ep);
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

    private final Network gw;
    private final Map<Endpoint, EndpointDataSet> sets = new HashMap<>();

    private final NetworkListener gatewayL = new AbstractNetworkListener() {

        @Override
        public void deviceRemoved(Network gw, PanStamp dev) {
            add(dev);
        }

        @Override
        public void deviceDetected(Network gw, PanStamp dev) {
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
