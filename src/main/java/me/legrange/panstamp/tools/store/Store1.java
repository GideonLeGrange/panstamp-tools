package me.legrange.panstamp.tools.store;

import com.github.jsonj.JsonObject;
import static com.github.jsonj.tools.JsonBuilder.field;
import static com.github.jsonj.tools.JsonBuilder.object;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import me.legrange.swap.ModemSetup;
import me.legrange.swap.SwapException;
import me.legrange.swap.SwapModem;
import me.legrange.swap.SerialModem;
import me.legrange.swap.tcp.TcpModem;

/**
 * Storage for tool configuration data and for network discovery data.
 *
 * Data is saved to a JSON file.
 *
 * @author gideon
 */
public class Store1 {

    public static final String STORE_VERSION = "1.0";

    /**
     * Add a network to the store. The store will attach itself to the network
     * and make sure to keep it's state in sync.
     *
     * @param nw
     */
    public void addNetwork(Network nw) throws DataStoreException {
        synchronized (this) {
            nw.addListener(networkListener);
            try {
                store(nw);
            } catch (NetworkException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Remove a network from the store. It will also be removed from persistent
     * storage.
     *
     * @param nw
     */
    public void removeNetwork(Network nw) throws NetworkException {
        synchronized (this) {
            nw.removeListener(networkListener);
            remove(nw);
        }
    }

    private void store(Network nw) throws NetworkException {
        getOrCreateNetwork(nw);
        for (PanStamp ps : nw.getDevices()) {
            ps.addListener(panStampListener);
            store(ps);
        }
    }

    private synchronized void remove(Network nw) throws NetworkException {
        for (PanStamp ps : nw.getDevices()) {
            ps.removeListener(panStampListener);
        }
        getOrCreateNetworks().remove(makeKey(nw));
    }

    private synchronized void store(PanStamp ps) throws NetworkException {
        getOrCreateDevice(ps);
        for (Register reg : ps.getRegisters()) {
            store(reg);
        }
    }

    private synchronized void remove(PanStamp ps) throws NetworkException {
        for (Register reg : ps.getRegisters()) {
            reg.removeListener(null);
        }
        getOrCreateDevices(ps.getGateway()).remove(makeKey(ps));
    }

    private synchronized void store(Register reg) throws NetworkException {
        if (reg.hasValue()) {
            getOrCreateDevice(reg.getDevice()).put(makeKey(reg), jsonFor(reg));
        }
    }

    private String jsonFor(Register reg) throws NetworkException {
        StringBuilder val = new StringBuilder();
        byte value[] = reg.getValue();
        for (byte b : value) {
            val.append(String.format("%02x", ((int) b) & 0xFF));
        }
        return val.toString();
    }

    private JsonObject jsonFor(SwapModem modem) throws DataStoreException {
        JsonObject modemO;
        switch (modem.getType()) {
            case SERIAL:
                modemO = jsonFor((SerialModem) modem);
                break;
            case TCP_IP:
                modemO = jsonFor((TcpModem) modem);
                break;
            default:
                throw new DataStoreException(String.format("Unknown modem type '%s'. BUG!", modem.getType()));
        }
        try {
            modemO.add(field(MODEM_SETUP, jsonFor(modem.getSetup())));
        } catch (SwapException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        return modemO;
    }

    /**
     * convert ModemSetup to JSON
     */
    private JsonObject jsonFor(ModemSetup setup) {
        return object(
                field(NETWORK_ID, setup.getNetworkID()),
                field(DEVICE_ADDRESS, setup.getDeviceAddress()),
                field(CHANNEL, setup.getChannel())
        );
    }

    /**
     * convert a serial modem to JSON
     */
    private JsonObject jsonFor(SerialModem modem) {
        return object(
                field(TYPE, SERIAL),
                field(SERIAL_PORT, modem.getPort()),
                field(SERIAL_SPEED, modem.getBaud())
        );
    }

    /**
     * convert a TCP/IP modem to JSON
     */
    private JsonObject jsonFor(TcpModem modem) {
        return object(
                field(TYPE, TCP_IP),
                field(TCP_HOST, modem.getHost()),
                field(TCP_PORT, modem.getPort())
        );
    }

    private JsonObject getOrCreateDevice(PanStamp ps) throws NetworkException {
        JsonObject devicesO = getOrCreateDevices(ps.getGateway());
        String key = makeKey(ps);
        if (!devicesO.containsKey(key)) {
            devicesO.put(key, new JsonObject());
        }
        return devicesO.getObject(key);
    }

    private JsonObject getOrCreateDevices(Network nw) throws NetworkException {
        JsonObject networkO = getOrCreateNetwork(nw);
        if (!networkO.containsKey(DEVICES)) {
            networkO.put(DEVICES, new JsonObject());
        }
        return networkO.getObject(DEVICES);
    }

    private JsonObject getOrCreateNetwork(Network nw) throws NetworkException {
        JsonObject networksO = getOrCreateNetworks();
        String key = makeKey(nw);
        if (!networksO.containsKey(key)) {
            JsonObject nwO = object(
                    field(NETWORK_ID, nw.getNetworkId()),
                    field(DEVICE_ADDRESS, nw.getDeviceAddress()),
                    field(CHANNEL, nw.getChannel()),
                    field(SECURITY_OPTION, nw.getSecurityOption()),
                    field(SWAP_MODEM, jsonFor(nw.getSWAPModem())),
                    field(DEVICES, new JsonObject()));
            networksO.put(key, nwO);
        }
        return networksO.getObject(key);
    }

    private JsonObject getOrCreateNetworks() {
        JsonObject rootO = getOrCreateRoot();
        if (!rootO.containsKey(NETWORKS)) {
            rootO.put(NETWORKS, object());
        }
        return rootO.getObject(NETWORKS);
    }

    private JsonObject getOrCreateRoot() {
        if (root == null) {
            root = new JsonObject();
            root.put(VERSION, STORE_VERSION);
        }
        return root;
    }

    /**
     * make a key for the given register
     */
    private String makeKey(Register reg) {
        return String.format("%2d", reg.getId());
    }

    /**
     * make a key for the given panstamp
     */
    private String makeKey(PanStamp ps) {
        return String.format("%d", ps.getAddress());
    }

    /**
     * make a key for the given gateway
     */
    private String makeKey(Network gw) throws NetworkException {
        String path = "";
        switch (gw.getSWAPModem().getType()) {
            case SERIAL:
                path = ((SerialModem) gw.getSWAPModem()).getPort();
                break;
            case TCP_IP:
                path = ((TcpModem) gw.getSWAPModem()).getHost() + ":" + ((TcpModem) gw.getSWAPModem()).getPort();
                break;
        }
        return String.format("%s->%d", path, gw.getNetworkId());
    }

    /**
     * flush the JSON data to disk
     */
    private void flush() throws DataStoreException {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(root.prettyPrint());
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    private String fileName = "/tmp/poes.json";
    private JsonObject root;

    private final NetworkListener networkListener = new AbstractNetworkListener() {

        @Override
        public void deviceRemoved(Network gw, PanStamp ps) {
            try {
                remove(ps);
            } catch (NetworkException ex) {
                Logger.getLogger(Store1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void deviceDetected(Network gw, PanStamp dev) {
            try {
                store(dev);
            } catch (NetworkException ex) {
                Logger.getLogger(Store1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void networkClosed(Network nw) {
            try {
                flush();
            } catch (DataStoreException ex) {
                Logger.getLogger(Store1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    };

    private final PanStampListener panStampListener = new AbstractPanStampListener() {

        @Override
        public void registerDetected(PanStamp dev, Register reg) {
            if (reg.isStandard()) {
                try {
                    store(reg);
                } catch (NetworkException ex) {
                    Logger.getLogger(Store1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    };
    
    private final RegisterListener registerListener = new AbstractRegisterListener() {

        @Override
        public void valueReceived(Register reg, byte[] value) {
            try {
                store(reg);
            } catch (NetworkException ex) {
                Logger.getLogger(Store1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
    };

    private static final String NETWORKS = "networks";
    private static final String NETWORK_ID = "networkId";
    private static final String DEVICE_ADDRESS = "deviceAddress";
    private static final String CHANNEL = "channel";
    private static final String SECURITY_OPTION = "securityOption";
    private static final String DEVICES = "devices";
    private static final String SWAP_MODEM = "swapModem";
    private static final String TYPE = "type";
    private static final String SERIAL = "serial";
    private static final String TCP_IP = "tcpIp";
    private static final String SERIAL_PORT = "serialPort";
    private static final String SERIAL_SPEED = "serialSpeed";
    private static final String TCP_HOST = "tcpHost";
    private static final String TCP_PORT = "tcpPort";
    private static final String MODEM_SETUP = "modemSetup";
    private static final String VERSION = "storeVersion";
}
