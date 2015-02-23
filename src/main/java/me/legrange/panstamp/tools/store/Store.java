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
import java.util.LinkedList;
import java.util.List;
import me.legrange.panstamp.Factory;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.core.GatewayImpl;
import me.legrange.panstamp.core.PanStampImpl;
import me.legrange.swap.ModemSetup;
import me.legrange.swap.SWAPException;
import me.legrange.swap.SWAPModem;
import me.legrange.swap.serial.SerialModem;
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

    public void storeGateway(Gateway gw) throws DataStoreException {
        try {
            JsonObject gwO = object(
                    field(NETWORK_ID, gw.getNetworkId()),
                    field(DEVICE_ADDRESS, gw.getDeviceAddress()),
                    field(CHANNEL, gw.getChannel()),
                    field(SECURITY_OPTION, gw.getSecurityOption()),
                    field(SWAP_MODEM, storeModem(gw.getSWAPModem())),
                    field(DEVICES, storeDevices(gw.getDevices()))
            );
            put(gw, gwO);
        } catch (GatewayException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

    }

    public void storePanStamp(PanStamp ps) throws DataStoreException {
        try {
            JsonObject gwO = get(ps.getGateway());
            if (gwO == null) {
                storeGateway(ps.getGateway());
                gwO = get(ps.getGateway());
            }
            JsonObject devicesO = gwO.getObject(DEVICES);
            if (devicesO == null) {
                devicesO = new JsonObject();
                gwO.add(field(DEVICES, devicesO));
            }
            devicesO.put("" + ps.getAddress(), storeDevice(ps));
            flush();
        } catch (GatewayException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Load all gateways stored in the data store
     * @return The list of gateways loaded from storage. 
     * @throws me.legrange.panstamp.tools.store.DataStoreException If there is an error loading the definitions.
     */
    public List<Gateway> load() throws DataStoreException {
        List<Gateway> gateways = new LinkedList<>();
        JsonObject networksO = root.getObject(NETWORKS);
        for (String key : networksO.keySet()) {
            JsonObject networkO = networksO.getObject(key);
            gateways.add(loadGateway(networkO));
        }
        return gateways;
    }

    /**
     * Load a gatway from JSON
     */
    private Gateway loadGateway(JsonObject gatewayO) throws DataStoreException {
        try {
            SWAPModem modem = loadModem(gatewayO.getObject(SWAP_MODEM));
            Gateway gw = Factory.createGateway(modem);
            gw.setNetworkId(gatewayO.getInt(NETWORK_ID));
            gw.setChannel(gatewayO.getInt(CHANNEL));
            gw.setDeviceAddress(gatewayO.getInt(DEVICE_ADDRESS));
            gw.setSecurityOption(gatewayO.getInt(SECURITY_OPTION));
            loadDevices(gw, gatewayO.getObject(DEVICES));
            return gw;
        } catch (GatewayException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Load the devices from JSOM
     */
    private void loadDevices(Gateway gw, JsonObject devicesO) throws GatewayException {
        for (String key : devicesO.keySet()) {
            JsonObject deviceO = devicesO.getObject(key);
            PanStampImpl ps = new PanStampImpl((GatewayImpl) gw, deviceO.getInt(DEVICE_ADDRESS));
            ps.setTxInterval(deviceO.getInt(TX_INTERVAL));
            ps.setProductCode(deviceO.getInt(MANUFACTURER_ID), deviceO.getInt(PRODUCT_ID));
            ((GatewayImpl)gw).addDevice(ps);
        }
    }

    /**
     * Load a SWAP modem
     */
    private SWAPModem loadModem(JsonObject modemO) throws DataStoreException {
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
    private JsonElement storeModem(SWAPModem modem) throws DataStoreException {
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
        } catch (SWAPException ex) {
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
    private JsonElement storeDevices(List<PanStamp> devices) throws GatewayException {
        JsonObject devicesO = new JsonObject();
        for (PanStamp dev : devices) {
            devicesO.put("" + dev.getAddress(), storeDevice(dev));
        }
        return devicesO;
    }

    /**
     * convert a panStamp to JSON
     */
    private JsonObject storeDevice(PanStamp ps) throws GatewayException {
        return object(
                field(DEVICE_ADDRESS, ps.getAddress()),
                field(MANUFACTURER_ID, ps.getManufacturerId()),
                field(PRODUCT_ID, ps.getProductId()),
                field(TX_INTERVAL, ps.getTxInterval())
        );
    }

    /**
     * put the given network Json in the structure and flush the file
     */
    private void put(Gateway gw, JsonObject gwO) throws GatewayException {
        String key = makeKey(gw);
        root.getObject(NETWORKS).put(key, gwO);
        flush();
    }

    private JsonObject get(Gateway gw) throws GatewayException {
        String key = makeKey(gw);
        JsonObject networksO = root.getObject(NETWORKS);
        JsonObject networkO;
        if (networksO.containsKey(key)) {
            return networksO.getObject(key);
        }
        return null;
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
    private String makeKey(Gateway gw) throws GatewayException {
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
                        field(NETWORKS, object())
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
    private static final String TX_INTERVAL = "txInterval";
    private static final String MANUFACTURER_ID = "manufacturerId";
    private static final String PRODUCT_ID = "productId";
    private static final String VERSION = "storeVersion";
    private static final String NETWORKS = "networks";

}
