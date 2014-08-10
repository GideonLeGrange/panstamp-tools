package me.legrange.panstamp.gui;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
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
        return portSpeed;
    }

    void setPortSpeed(int portSpeed) {
        this.portSpeed = portSpeed;
    }

    String getPortName() {
        return portName;
    }

    void setPortName(String portName) {
        this.portName = portName;
    }

    int getChannel() {
        return channel;
    }

    void setChannel(int channel) {
        this.channel = channel;
    }

    int getNetworkID() {
        return networkID;
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
    }

    void save() throws BackingStoreException {
        if (!portName.equals("")) {
            conf.put(SERIAL_PORT, portName);
        }
        conf.putInt(SERIAL_SPEED, portSpeed);
        conf.sync();
    }

    /**
     * config variable names
     */
    private static final String SERIAL_PORT = "serial.port";
    private static final String SERIAL_SPEED = "serial.speed";

    private final Preferences conf;
    private String portName = "";
    private int portSpeed = 38400;
    private int channel = 0;
    private int networkID = 0xB547;
    private int deviceAddress;
    private int securityOption;

}
