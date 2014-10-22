package me.legrange.panstamp.gui.model;

import java.util.HashMap;
import java.util.Map;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayEvent;
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
public class EndpointCollector {

    EndpointCollector(Gateway gw) {
        gw.addListener(new GatewayListener() {

            @Override
            public void gatewayUpdated(GatewayEvent ev) {
                switch (ev.getType()) {
                    case DEVICE_DETECTED: {
                        PanStamp ps = ev.getDevice();
                        ps.addListener(new PanStampListener() {

                            @Override
                            public void deviceUpdated(PanStampEvent ev) {
                                switch (ev.getType()) {
                                    case REGISTER_DETECTED: {
                                        Register reg = ev.getRegister();
                                        reg.addListener(new RegisterListener() {

                                            @Override
                                            public void registerUpdated(RegisterEvent ev) {
                                                switch (ev.getType()) {
                                                    case ENDPOINT_ADDED : {
                                                        Endpoint ep = ev.getEndpoint();
                                                        addDataSet(ep);
                                                    }
                                                    break;
                                                }
                                            }
                                        });
                                    }
                                    break;
                                }
                            }
                        });
                    }
                    break;
                }
            }
        });
    }

    public EndpointDataSet getDataSet(Endpoint ep) {
        EndpointDataSet ds = sets.get(ep);
        if (ds == null) {
            ds = addDataSet(ep);
        }
        return ds;
    }
    
    private EndpointDataSet addDataSet(Endpoint ep) {
            EndpointDataSet ds = new EndpointDataSet(ep);
            sets.put(ep, ds);
            ep.addListener(ds);
            return ds;
    }

    private final Map<Endpoint, EndpointDataSet> sets = new HashMap<>();
}
