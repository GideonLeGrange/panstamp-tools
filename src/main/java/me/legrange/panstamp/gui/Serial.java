package me.legrange.panstamp.gui;

import gnu.io.CommPortIdentifier;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple container for the serial configuration data 
 * @author gideon
 */
class Serial {

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

    private String portName = "";
    private int portSpeed = 38400;

}
