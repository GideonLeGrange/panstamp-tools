package me.legrange.panstamp.tools.store;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayEvent;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampEvent;
import me.legrange.panstamp.PanStampListener;

/**
 * Updates panStamp data to a data store
 *
 * @author gideon
 */
public class DataUpdater {

    public DataUpdater(Store store) {
        this.store = store;
    }

    public void addGateway(Gateway gw) {
        gw.addListener(gatewayListener);
        for (PanStamp ps : gw.getDevices()) {
            ps.addListener(deviceListener);
        }
    }

    public void removeGateway(Gateway gw) {
        for (PanStamp ps : gw.getDevices()) {
            ps.removeListener(deviceListener);
        }
        gw.removeListener(gatewayListener);
    }

    private final Store store;

    private final GatewayListener gatewayListener = new GatewayListener() {
        @Override
        public void gatewayUpdated(GatewayEvent ev) {
            PanStamp ps = null;
            switch (ev.getType()) {
                case DEVICE_DETECTED:
                    ps = ev.getDevice();
                    ps.addListener(deviceListener);
                    break;
                case DEVICE_REMOVED:
                    ps = ev.getDevice();
                    ps.removeListener(deviceListener);
                    break;
            }
            if (ps != null)
                try {
                    store.storeGateway(ps.getGateway());
            } catch (DataStoreException ex) {
                Logger.getLogger(DataUpdater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    private final PanStampListener deviceListener = new PanStampListener() {

        @Override
        public void deviceUpdated(PanStampEvent ev) {
            switch (ev.getType()) {
                case PRODUCT_CODE_UPDATE: {
                    try {
                        store.storePanStamp(ev.getDevice());
                    } catch (DataStoreException ex) {
                        Logger.getLogger(DataUpdater.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }
        }
    };

}
