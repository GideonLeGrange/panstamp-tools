package me.legrange.panstamp.gui;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * A simple container for the configuration data
 *
 * @author gideon
 */
class Config {

    Config() {
        conf = Preferences.userRoot().node(Config.class.getPackage().getName());
        load();
    }
    
    boolean hasChanged() {
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
    String[] getPorts() {
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

    boolean hasValidPort() {
        if (!getPortName().equals("")) {
            try {
                CommPortIdentifier.getPortIdentifier(getPortName());
                return true;
            } catch (NoSuchPortException ex) {
            }
        }
        return false;
    }

    Integer[] getSpeeds() {
        return new Integer[]{38400, 19200, 9600};
    }

    Integer getPortSpeed() {
        return getInt(SERIAL_SPEED);
    }

    void setPortSpeed(int portSpeed) {
        setInt(SERIAL_SPEED, portSpeed);
    }

    String getPortName() {
        return getString(SERIAL_PORT);
    }

    void setPortName(String portName) {
        setString(SERIAL_PORT, portName);
    }

    int getChannel() {
        return getInt(CHANNEL);
    }

    void setChannel(int channel) {
        setInt(CHANNEL, channel);
    }

    int getNetworkID() {
        return getInt(NETWORK_ID);
    }

    public void setNetworkID(int networkID) {
        setInt(NETWORK_ID, networkID);
    }

    int getDeviceAddress() {
        return getInt(DEVICE_ADDRESS);
    }

    void setDeviceAddress(int deviceAddress) {
        setInt(DEVICE_ADDRESS, deviceAddress);
    }

    int getSecurityOption() {
        return getInt(SECURITY);
    }

    void setSecurityOption(int securityOption) {
        setInt(SECURITY, securityOption);
    }

    final void load() {
        setPortName(conf.get(SERIAL_PORT, ""));
        setPortSpeed(conf.getInt(SERIAL_SPEED, 38400));
        setChannel(conf.getInt(CHANNEL, 0));
        setNetworkID(conf.getInt(NETWORK_ID, 0xB547));
        setSecurityOption(conf.getInt(SECURITY, 0));
        setDeviceAddress(conf.getInt(DEVICE_ADDRESS, 1));
        mapCopy(set, loaded);
    }

    final void save() throws BackingStoreException {
        for (String key : set.keySet()) {
            Object val = set.get(key);
            if (val instanceof String) {
                conf.put(key, (String)val);
            }
            else if (val instanceof Integer) {
                conf.putInt(key, (Integer)val);
            }
        }
        conf.sync();
        mapCopy(set, loaded);
    }
    
    final void revert() {
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

}
