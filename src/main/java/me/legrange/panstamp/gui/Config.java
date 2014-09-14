package me.legrange.panstamp.gui;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import java.util.Enumeration;
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
        if (!portName.equals("")) {
            try {
                CommPortIdentifier.getPortIdentifier(portName);
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
        set.put(SERIAL_SPEED, portSpeed);
    }

    String getPortName() {
        return (String) set.get(SERIAL_PORT);
    }

    void setPortName(String portName) {
        set.put(SERIAL_PORT, portName);
    }

    int getChannel() {
        return (int) set.get(CHANNEL);
    }

    void setChannel(int channel) {
        set.put(CHANNEL, channel);
    }

    int getNetworkID() {
        return (int) set.get(NETWORK_ID);
    }

    public void setNetworkID(int networkID) {
        this.networkID = networkID;
    }

    int getDeviceAddress() {
        return deviceAddress;
    }

    void setDeviceAddress(int deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    int getSecurityOption() {
        return securityOption;
    }

    void setSecurityOption(int securityOption) {
        this.securityOption = securityOption;
    }

    final void  load() {
        portName = conf.get(SERIAL_PORT, "");
        portSpeed = conf.getInt(SERIAL_SPEED, 38400);
        channel = conf.getInt(CHANNEL, 0);
        networkID = conf.getInt(NETWORK_ID, 0xB547);
        securityOption = conf.getInt(SECURITY, 0);
        deviceAddress = conf.getInt(DEVICE_ADDRESS, 1);
    }

    void save() throws BackingStoreException {
        if (!portName.equals("")) {
            conf.put(SERIAL_PORT, portName);
        }
        conf.putInt(SERIAL_SPEED, portSpeed);
        conf.sync();
    }
    
    private Integer getInt(String name) {
        return (Integer) set.get(name);
        
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
    private  Map<String, Object> loaded;
    private Map<String, Object> set; 

}
