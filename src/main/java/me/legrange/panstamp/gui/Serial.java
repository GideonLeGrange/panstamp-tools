package me.legrange.panstamp.gui;

import gnu.io.CommPortIdentifier;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author gideon
 */
class Serial {
    
    /** return the list of serial ports */
    List<String> getPorts() {
        List<String> serials = new LinkedList<>();
        Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier cpi = ports.nextElement();
            if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                serials.add(cpi.getName());
            }
        }
        return serials;
    }
    
    String getPortName() {
        return portName;
    }
    
    void setPortName(String portName) {
        this.portName = portName;
    }
    
    private String portName;
    
}
