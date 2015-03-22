package me.legrange.panstamp.tools.store;

import com.github.jsonj.JsonElement;
import com.github.jsonj.JsonObject;
import static com.github.jsonj.tools.JsonBuilder.field;
import static com.github.jsonj.tools.JsonBuilder.object;
import com.github.jsonj.tools.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.DeviceStateStore;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.Register;
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
public class Store {

    public static final String STORE_VERSION = "1.0";

    public static Store openFile(String fileName) throws DataStoreException {
        return new Store(fileName);
    }

    public void addGateway(Network gw) {
        JsonObject gwO = getGateway(gw);
        gw.setDeviceStore(new JsonStateStore(gw));
    }

    public void removeGateway(Network gw) throws NetworkException {
        root.getObject(GATEWAYS).remove(makeKey(gw));
    }

    /**
     * Load all gateways stored in the data store
     *
     * @return The list of gateways loaded from storage.
     * @throws me.legrange.panstamp.tools.store.DataStoreException If there is
     * an error loading the definitions.
     */
    public List<Network> load() throws DataStoreException {
        List<Network> gateways = new LinkedList<>();
        JsonObject networksO = root.getObject(GATEWAYS);
        for (String key : networksO.keySet()) {
            JsonObject networkO = networksO.getObject(key);
            gateways.add(loadGateway(networkO));
        }
        return gateways;
    }

    /**
     * Load a gatway from JSON
     */
    private Network loadGateway(JsonObject gatewayO) throws DataStoreException {
        try {
            SwapModem modem = loadModem(gatewayO.getObject(SWAP_MODEM));
            Network gw = Network.create(modem);
            gw.setNetworkId(gatewayO.getInt(NETWORK_ID));
            gw.setChannel(gatewayO.getInt(CHANNEL));
            gw.setDeviceAddress(gatewayO.getInt(DEVICE_ADDRESS));
            gw.setSecurityOption(gatewayO.getInt(SECURITY_OPTION));
            loadDevices(gw, gatewayO.getObject(DEVICES));
            return gw;
        } catch (NetworkException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Load the devices from JSOM
     */
    private void loadDevices(Network gw, JsonObject devicesO) throws NetworkException {
        for (String key : devicesO.keySet()) {
            JsonObject deviceO = devicesO.getObject(key);
            PanStamp ps = new PanStamp((Network) gw, Integer.parseInt(key));
            for (String regKey : deviceO.keySet()) {
                int id = Integer.parseInt(regKey);
                Register reg = ps.getRegister(id);
                reg.setValue(new BigInteger(deviceO.getString(regKey), 16).toByteArray());
            }
            ((Network) gw).addDevice(ps);
        }
    }

    /**
     * Load a SWAP modem
     */
    private SwapModem loadModem(JsonObject modemO) throws DataStoreException {
        String type = modemO.getString(TYPE);
        switch (type) {
            case SERIAL:
                return loadSerialModem(modemO);
            case TCP_IP:
                return loadTcpModem(modemO);
            default:
                throw new DataStoreException(String.format("Unknown modem type '%s'. BUG!", type));
        }
    }

    /**
     * Load a serial modem
     */
    private SerialModem loadSerialModem(JsonObject modemO) {
        return new SerialModem(modemO.getString(SERIAL_PORT), modemO.getInt(SERIAL_SPEED));
    }

    /**
     * Load a TCP/IP modem
     */
    private TcpModem loadTcpModem(JsonObject modemO) {
        return new TcpModem(modemO.getString(TCP_HOST), modemO.getInt(TCP_PORT));
    }

    /**
     * store a SWAP modem
     */
    private JsonElement storeModem(SwapModem modem) throws DataStoreException {
        JsonObject modemO;
        switch (modem.getType()) {
            case SERIAL:
                modemO = storeSerialModem((SerialModem) modem);
                break;
            case TCP_IP:
                modemO = storeTcpModem((TcpModem) modem);
                break;
            default:
                throw new DataStoreException(String.format("Unknown modem type '%s'. BUG!", modem.getType()));
        }
        try {
            modemO.add(field(MODEM_SETUP, storeSetup(modem.getSetup())));
        } catch (SwapException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        return modemO;
    }

    /**
     * convert a serial modem to JSON
     */
    private JsonObject storeSerialModem(SerialModem modem) {
        return object(
                field(TYPE, SERIAL),
                field(SERIAL_PORT, modem.getPort()),
                field(SERIAL_SPEED, modem.getBaud())
        );
    }

    /**
     * convert a TCP/IP modem to JSON
     */
    private JsonObject storeTcpModem(TcpModem modem) {
        return object(
                field(TYPE, TCP_IP),
                field(TCP_HOST, modem.getHost()),
                field(TCP_PORT, modem.getPort())
        );
    }

    /**
     * convert ModemSetup to JSON
     */
    private JsonObject storeSetup(ModemSetup setup) {
        return object(
                field(NETWORK_ID, setup.getNetworkID()),
                field(DEVICE_ADDRESS, setup.getDeviceAddress()),
                field(CHANNEL, setup.getChannel())
        );
    }

    /**
     * convert list of panStamp nodes to JSON
     */
    private JsonElement storeDevices(List<PanStamp> devices) throws NetworkException {
        JsonObject devicesO = new JsonObject();
        for (PanStamp dev : devices) {
            devicesO.put("" + dev.getAddress(), storeDevice(dev));
        }
        return devicesO;
    }

    /**
     * convert a panStamp to JSON
     */
    private JsonObject storeDevice(PanStamp ps) throws NetworkException {
        JsonObject stateO = new JsonObject();
        for (Register reg : ps.getRegisters()) {
            if (reg.isStandard()) {
                if (reg.hasValue()) {
                    for (Endpoint ep : reg.getEndpoints()) {
                        stateO.put(ep.getName(), ep.getValue());
                    }
                }
            }
        }
        return stateO;
    }

    private JsonObject getGateway(Network gw) {
        try {
            String key = makeKey(gw);
            JsonObject networksO = root.getObject(GATEWAYS);
            if (!networksO.containsKey(key)) {
                JsonObject gwO = object(
                        field(NETWORK_ID, gw.getNetworkId()),
                        field(DEVICE_ADDRESS, gw.getDeviceAddress()),
                        field(CHANNEL, gw.getChannel()),
                        field(SECURITY_OPTION, gw.getSecurityOption()),
                        field(SWAP_MODEM, storeModem(gw.getSWAPModem())),
                        field(DEVICES, new JsonObject()));
                networksO.put(key, gwO);
            }
            return networksO.getObject(key);
        } catch (NetworkException ex) {
            return new JsonObject();
        }
    }

    private JsonObject getDevices(JsonObject gwO) {
        JsonObject devicesO = gwO.getObject(DEVICES);
        if (devicesO == null) {
            devicesO = new JsonObject();
            gwO.put(DEVICES, devicesO);
        }
        return devicesO;
    }

    private JsonObject getDevice(JsonObject gwO, int address) {
        JsonObject devicesO = getDevices(gwO);
        JsonObject deviceO = devicesO.getObject("" + address);
        if (deviceO == null) {
            deviceO = new JsonObject();
            devicesO.put("" + address, deviceO);
        }
        return deviceO;
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
     * Create new instance with a backing file
     */
    private Store(String fileName) throws DataStoreException {
        try {
            this.fileName = fileName;
            File file = new File(fileName);
            if (file.exists()) {
                root = new JsonParser().parseObject(new FileReader(file));
                if (root.containsKey(VERSION)) {
                    String ver = root.getString(VERSION);
                    if (!STORE_VERSION.equals(ver)) {
                        throw new DataStoreException(String.format("Incompatible store version: Expected '%s' but found '%s'", STORE_VERSION, ver));
                    }
                }
            } else {
                root = object(
                        field(VERSION, STORE_VERSION),
                        field(GATEWAYS, object())
                );
            }
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    private final String fileName;
    private JsonObject root;

    private static final String SWAP_MODEM = "swapModem";
    private static final String TYPE = "type";
    private static final String SERIAL = "serial";
    private static final String TCP_IP = "tcpIp";
    private static final String SERIAL_PORT = "serialPort";
    private static final String SERIAL_SPEED = "serialSpeed";
    private static final String TCP_HOST = "tcpHost";
    private static final String TCP_PORT = "tcpPort";
    private static final String MODEM_SETUP = "modemSetup";
    private static final String NETWORK_ID = "networkId";
    private static final String DEVICE_ADDRESS = "deviceAddress";
    private static final String CHANNEL = "channel";
    private static final String SECURITY_OPTION = "securityOption";
    private static final String DEVICES = "devices";
    private static final String VERSION = "storeVersion";
    private static final String GATEWAYS = "gateways";


    private class JsonStateStore implements DeviceStateStore {

        public JsonStateStore(Network gw) {
            this.gw = gw;
        }

        @Override
        public boolean hasRegisterValue(Register reg) {
            JsonObject devO = getDevice(getGateway(gw), reg.getDevice().getAddress());
            return devO.containsKey("" + reg.getId());        }

        @Override
        public byte[] getRegisterValue(Register reg) {
            JsonObject devO = getDevice(getGateway(gw), reg.getDevice().getAddress());
             return new BigInteger(devO.getString("" + reg.getId()),16).toByteArray();
        }

        @Override
        public void setRegisterValue(Register reg, byte[] value) {
            JsonObject devO = getDevice(getGateway(gw), reg.getDevice().getAddress());
            StringBuilder val = new StringBuilder();
            for (byte b : value) {
                val.append(String.format("%02x", ((int)b)&0xFF));
            }
            devO.put("" + reg.getId(), val.toString());
            try {
                flush();
            } catch (DataStoreException ex) {
                Logger.getLogger(Store.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        private final Network gw;

    }

}
