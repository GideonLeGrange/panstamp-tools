package me.legrange.panstamp.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayEvent;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStampEvent;
import me.legrange.panstamp.PanStampListener;

/**
 *
 * @author gideon
 */
public class EndpointTableModel implements TableModel, GatewayListener, PanStampListener {
   

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
        System.out.printf("set value at called: %s %d %d", aValue, rowIndex, columnIndex);
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
        this.gw = gw;
        gw.addListener(this);
    }
    void add(String msg) {
        long timeStamp = System.currentTimeMillis();
        synchronized (data) {
            data.add(0, new Entry(timeStamp, msg));
        }
        for (TableModelListener l : listeners) {
            l.tableChanged(new TableModelEvent(this, 0, 0, -1, TableModelEvent.INSERT));
        }
    }

    EndpointTableModel() {
        this.listeners = new LinkedList<>();
    }

    @Override
    public void gatewayUpdated(GatewayEvent ev) {
        switch (ev.getType()) {
            case DEVICE_DETECTED  :
                ev.getDevice().addListener(this);
                add(String.format("Detected new device with address %d", ev.getDevice().getAddress()));
                break;
        }
    }

    @Override
    public void deviceUpdated(PanStampEvent ev) {
        switch (ev.getType()) {
            case PRODUCT_CODE_UPDATE : 
                add(String.format("Device %d has a new product code", ev.getDevice().getAddress()));
                break;
            case SYNC_STATE_CHANGE : 
                add(String.format("Sync state changed for device %d", ev.getDevice().getAddress()));
        }
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
    private Gateway gw;

}
