package me.legrange.panstamp.gui.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.EndpointListener;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.RegisterListener;
import me.legrange.panstamp.StandardEndpoint;
import me.legrange.panstamp.StandardRegister;
import me.legrange.panstamp.core.AbstractEndpointListener;
import me.legrange.panstamp.core.AbstractGatewayListener;
import me.legrange.panstamp.core.AbstractPanStampListener;
import me.legrange.panstamp.core.AbstractRegisterListener;

/**
 *
 * @author gideon
 */
class EndpointTableModel implements TableModel {

    @Override
    public int getRowCount() {
        synchronized (data) {
            return data.size();
        }
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Entry entry;
        synchronized (data) {
            if (rowIndex < data.size()) {
                entry = data.get(rowIndex);
            } else {
                return null;
            }
        }
        switch (columnIndex) {
            case 0:
                return timeFormat.format(new Date(entry.timestamp));
            case 1:
                return entry.message;
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    static EndpointTableModel create() {
        return new EndpointTableModel();
    }

    void addGateway(Gateway gw) {
        gw.addListener(gatewayL);
        for (PanStamp ps : gw.getDevices()) {
            add(ps);
        }
    }

    void removeGateway(Gateway gw) throws GatewayException {
        gw.removeListener(gatewayL);
        for (PanStamp ps : gw.getDevices()) {
            remove(ps);
        }
    }

    void add(String msg) {
        long timeStamp = System.currentTimeMillis();
        synchronized (data) {
            data.add(0, new Entry(timeStamp, msg));
            if (data.size() > 1000) {
                for (int i = 0; i < 100; ++i) {
                    data.remove(data.size() - 1);
                }
            }
        }
        for (TableModelListener l : listeners) {
            l.tableChanged(new TableModelEvent(this, 0, 0, -1, TableModelEvent.INSERT));
        }
    }

    EndpointTableModel() {
        this.listeners = new LinkedList<>();
    }

    private String productCodeMessage(PanStamp ps) throws GatewayException {
        Register reg = ps.getRegister(StandardRegister.PRODUCT_CODE.getId());
        return String.format("Device %d identified as %s/%s", ps.getAddress(),
                reg.getEndpoint(StandardEndpoint.MANUFACTURER_ID.getName()).getValue(),
                reg.getEndpoint(StandardEndpoint.PRODUCT_ID.getName()).getValue());
    }

    private String syncStateMessage(PanStamp ps) throws GatewayException {
        Register reg = ps.getRegister(StandardRegister.SYSTEM_STATE.getId());
        Endpoint<Integer> ep = reg.getEndpoint(StandardEndpoint.SYSTEM_STATE.getName());
        int state = ep.getValue();
        String mode;
        switch (state) {
            case 0:
                mode = "Restarting";
                break;
            case 1:
                mode = "Wireless reception enabled";
                break;
            case 2:
                mode = "Wireless reception disabled";
                break;
            case 3:
                mode = "Synchronization mode, wireless reception enabled";
                break;
            case 4:
                mode = "Low battery state";
                break;
            default:
                mode = "" + state;
        }
        return String.format("Device %d reported state: %s", ps.getAddress(), mode);
    }

    private String formatValue(Endpoint ep) throws GatewayException {
        return Format.formatValue(ep);
    }

    private void add(PanStamp ps) {
        add(String.format("Detected new device with address %d", ps.getAddress()));
        ps.addListener(panStampL);
        try {
            for (Register reg : ps.getRegisters()) {
                add(reg);
            }
        } catch (GatewayException ex) {
            Logger.getLogger(EndpointTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void remove(PanStamp ps) throws GatewayException {
        ps.removeListener(panStampL);
        for (Register reg : ps.getRegisters()) {
            remove(reg);
        }
    }

    private void add(Register reg) {
        add(String.format("Learnt of register %d for device %d", reg.getId(), reg.getDevice().getAddress()));
        reg.addListener(registerL);
        try {
            for (Endpoint ep : reg.getEndpoints()) {
                add(ep);
            }
        } catch (GatewayException ex) {
            Logger.getLogger(EndpointTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void remove(Register reg) throws GatewayException {
        reg.removeListener(registerL);
        for (Endpoint ep : reg.getEndpoints()) {
            remove(ep);
        }
    }

    private void add(Endpoint ep) {
        add(String.format("Learnt of endpoint '%s' for register %d on device %d",
                ep.getName(), ep.getRegister().getId(), ep.getRegister().getDevice().getAddress()));
        ep.addListener(endpointL);
    }

    private void remove(Endpoint ep) {
        ep.removeListener(endpointL);
    }

    private static class Entry {

        public Entry(long timestamp, String message) {
            this.timestamp = timestamp;
            this.message = message;
        }

        private long timestamp;
        private String message;

    }

    private final List<Entry> data = new ArrayList<>();

    private final List<TableModelListener> listeners;
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static final String columnNames[] = {"Timestamp", "Event"};
    private static final Class columnClasses[] = {String.class, String.class};

    // GatewayListener, PanStampListener, RegisterListener, EndpointListener
    private final GatewayListener gatewayL = new AbstractGatewayListener() {

        @Override
        public void deviceDetected(Gateway gw, PanStamp dev) {
            add(dev);
        }
    };

    private final PanStampListener panStampL = new AbstractPanStampListener() {

        @Override
        public void registerDetected(PanStamp dev, Register reg) {
            add(reg);
        }

        @Override
        public void syncStateChange(PanStamp dev, int syncState) {
            try {
                add(syncStateMessage(dev));
            } catch (GatewayException ex) {
                Logger.getLogger(EndpointTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void productCodeChange(PanStamp dev, int manufacturerId, int productId) {
            try {
                add(productCodeMessage(dev));
            } catch (GatewayException ex) {
                Logger.getLogger(EndpointTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    };

    private final RegisterListener registerL = new AbstractRegisterListener() {

        @Override
        public void endpointAdded(Register reg, Endpoint ep) {
            add(ep);
        }

    };

    private final EndpointListener endpointL = new AbstractEndpointListener() {

        @Override
        public void valueReceived(Endpoint ep, Object value) {
            try {
                add(String.format("%s in address %d on node %d changed to %s",
                        ep.getName(), ep.getRegister().getId(),
                        ep.getRegister().getDevice().getAddress(), formatValue(ep)));
            } catch (GatewayException ex) {
                Logger.getLogger(EndpointTableModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    };

}
