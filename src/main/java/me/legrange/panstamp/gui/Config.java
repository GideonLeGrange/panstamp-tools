package me.legrange.panstamp.gui;

import gnu.io.CommPortIdentifier;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple container for the configuration data 
 * @author gideon
 */
class Config {

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
    
    Integer[] getSpeeds() {
        return new Integer[]{ 38400, 19200, 9600 };
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

    private String portName = "";
    private int portSpeed = 38400;
    private int channel;
    private int networkID;
    private int deviceAddress;
    private int securityOption;

}
