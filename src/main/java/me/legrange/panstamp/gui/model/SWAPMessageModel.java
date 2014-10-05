package me.legrange.panstamp.gui.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import me.legrange.swap.SwapMessage;

/**
 * A table model that deals with SWAP messages
 *
 * @author gideon
 */
class SWAPMessageModel implements TableModel {

    static SWAPMessageModel create() { 
        return new SWAPMessageModel();
    }
    
    private SWAPMessageModel() {
        this.listeners = new LinkedList<>();
    }

    enum Direction {

        IN, OUT
    };

    @Override
    public int getRowCount() {
        synchronized (data) {
            return data.size();
        }
    }

    @Override
    public int getColumnCount() {
        return 4;
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
            }
            else {
                return null;
            }
        }
        switch (columnIndex) {
            case 0:
                return timeFormat.format(new Date(entry.timestamp));
            case 1:
                return entry.dir.name();
            case 2:
                return entry.msg.getType().name();
            case 3:
                return entry.msg.toString();
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

    void add(SwapMessage msg, long timestamp, Direction dir) {
        synchronized (data) {
            data.add(0, new Entry(msg, timestamp, dir));
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

    private static class Entry {

        public Entry(SwapMessage msg, long timestamp, Direction dir) {
            this.msg = msg;
            this.timestamp = timestamp;
            this.dir = dir;
        }

        private final SwapMessage msg;
        private final long timestamp;
        private final Direction dir;

    }

    private final List<Entry> data = new ArrayList<>();
    private final List<TableModelListener> listeners;
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static final String columnNames[] = {"Timestamp", "Direction", "Type", "Message"};
    private static final Class columnClasses[] = {String.class, String.class, String.class, String.class};

}
