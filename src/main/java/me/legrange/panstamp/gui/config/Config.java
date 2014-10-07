package me.legrange.panstamp.gui.config;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * A simple container for the configuration data
 *
 * @author gideon
 */
public class Config {

    public Config() {
        conf = Preferences.userRoot().node(Config.class.getPackage().getName());
        load();
    }

    public void addListener(ConfigListener l) {
        listeners.add(l);
    }

    public boolean hasChanged() {
        for (String key : set.keySet()) {
            if (!set.get(key).equals(loaded.get(key))) {
                return false;
            }
        }
        return true;
    }

    /**
     * return the list of serial ports
     */
    public String[] getPorts() {
        List<String> serials = new LinkedList<>();
        Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier cpi = ports.nextElement();
            if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                serials.add(cpi.getName());
            }
        }
        return serials.toArray(new String[]{});
    }

    public boolean hasValidPort() {
        if (!getPortName().equals("")) {
            try {
                CommPortIdentifier.getPortIdentifier(getPortName());
                return true;
            } catch (NoSuchPortException ex) {
            }
        }
        return false;
    }

    public Integer[] getSpeeds() {
        return new Integer[]{38400, 19200, 9600};
    }

    public Integer getPortSpeed() {
        return getInt(SERIAL_SPEED);
    }

    public void setPortSpeed(int portSpeed) {
        setInt(SERIAL_SPEED, portSpeed);
    }

    public String getPortName() {
        return getString(SERIAL_PORT);
    }

    public void setPortName(String portName) {
        setString(SERIAL_PORT, portName);
    }

    public int getChannel() {
        return getInt(CHANNEL);
    }

    public void setChannel(int channel) {
        setInt(CHANNEL, channel);
    }

    public int getNetworkID() {
        return getInt(NETWORK_ID);
    }

    public void setNetworkID(int networkID) {
        setInt(NETWORK_ID, networkID);
    }

    public int getDeviceAddress() {
        return getInt(DEVICE_ADDRESS);
    }

    public void setDeviceAddress(int deviceAddress) {
        setInt(DEVICE_ADDRESS, deviceAddress);
    }

    public int getSecurityOption() {
        return getInt(SECURITY);
    }

    public void setSecurityOption(int securityOption) {
        setInt(SECURITY, securityOption);
    }

    public final void load() {
        setPortName(conf.get(SERIAL_PORT, ""));
        setPortSpeed(conf.getInt(SERIAL_SPEED, 38400));
        setChannel(conf.getInt(CHANNEL, 0));
        setNetworkID(conf.getInt(NETWORK_ID, 0xB547));
        setSecurityOption(conf.getInt(SECURITY, 0));
        setDeviceAddress(conf.getInt(DEVICE_ADDRESS, 1));
        mapCopy(set, loaded);
    }

    public final void save() throws BackingStoreException {
        for (String key : set.keySet()) {
            Object val = set.get(key);
            if (val instanceof String) {
                conf.put(key, (String) val);
            } else if (val instanceof Integer) {
                conf.putInt(key, (Integer) val);
            }
        }
        conf.sync();
        if (!set.equals(loaded)) {

            boolean netU = false;
            boolean serU = false;
            for (String key : set.keySet()) {
                if (!set.get(key).equals(loaded.get(key))) {
                    switch (key) {
                        case CHANNEL:
                        case DEVICE_ADDRESS:
                        case NETWORK_ID:
                            netU = true;
                            break;
                        case SERIAL_PORT:
                        case SERIAL_SPEED:
                            serU = true;
                            break;
                    }
                }
            }
            for (ConfigListener l : listeners) {
                if (serU) {
                    l.configUpdated(new ConfigEvent(ConfigEvent.Type.SERIAL));
                }
                if (netU) {
                    l.configUpdated(new ConfigEvent(ConfigEvent.Type.NETWORK));
                }
            }
        }
        mapCopy(set, loaded);
    }

    public final void revert() {
        mapCopy(loaded, set);
    }

    private Integer getInt(String name) {
        return (Integer) set.get(name);
    }

    private void setInt(String name, Integer val) {
        set.put(name, val);
    }

    private String getString(String name) {
        return (String) set.get(name);
    }

    private void setString(String name, String val) {
        set.put(name, val);
    }

    private void mapCopy(Map<String, Object> src, Map<String, Object> dst) {
        dst.clear();
        for (String key : src.keySet()) {
            dst.put(key, src.get(key));
        }
    }
    /**
     * config variable names
     */
    private static final String SERIAL_PORT = "serial.port";
    private static final String SERIAL_SPEED = "serial.speed";
    private static final String NETWORK_ID = "network.id";
    private static final String CHANNEL = "network.channel";
    private static final String SECURITY = "network.security";
    private static final String DEVICE_ADDRESS = "network.address";

    private final Preferences conf;
    private final Map<String, Object> loaded = new HashMap<>();
    private final Map<String, Object> set = new HashMap<>();
    private final List<ConfigListener> listeners = new CopyOnWriteArrayList<>();
}
