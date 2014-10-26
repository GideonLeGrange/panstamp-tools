package me.legrange.panstamp.gui.model.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author gideon
 */
public class SignalDataSet implements XYDataset {
    
    public void addSample(int rssi, int lqi) {
        data.add(new Entry(rssi, lqi));
        fire();
    }

    @Override
    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    @Override
    public int getItemCount(int i) {
        return data.size();
    }

    @Override
    public Number getX(int series, int index) {
        return getXValue(series, index);
    }

    @Override
    public double getXValue(int series, int index) {
        switch (series) {
            case RSSI : return data.get(index).rssi;
            case LQI : return data.get(index).lqi;
            default : throw new IndexOutOfBoundsException(String.format("No series %d", series));
        }
    }

    @Override
    public Number getY(int series, int index) {
        return getYValue(series, index);
    }

    @Override
    public double getYValue(int series, int index) {
        return data.get(index).time;
    }

    @Override
    public int getSeriesCount() {
        return 2;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        switch (series) {
            case LQI : return "LQI";
            case RSSI : return "RSSI";
            default : throw new IndexOutOfBoundsException(String.format("No series %d", series));
        }
    }

    @Override
    public int indexOf(Comparable key) {
        switch (key.toString()) {
            case "LQI" : return LQI;
            case "RSSI" : return RSSI;
            default : throw new IndexOutOfBoundsException("No series for key '" + key + "'");
        }
    }

    @Override
    public void addChangeListener(DatasetChangeListener dl) {
        listeners.add(dl);
    }

    @Override
    public void removeChangeListener(DatasetChangeListener dl) {
        listeners.remove(dl);
    }

    @Override
    public DatasetGroup getGroup() {
        return group;
    }

    @Override
    public void setGroup(DatasetGroup dg) {
        this.group = dg;
    }
    
    private void fire()    {
        for (DatasetChangeListener l :   listeners) {
            l.datasetChanged(new DatasetChangeEvent(this, this));
        }
    }    
    private final List<Entry> data = new ArrayList<>();
    private final List<DatasetChangeListener> listeners = new CopyOnWriteArrayList<>();
    private DatasetGroup group;
    
    private  class Entry { 

        public Entry(int rssi, int lqi) {
            this.rssi = rssi;
            this.lqi = lqi;
            this.time = tick; 
            tick++;
        }
        
        final int rssi;
        final int lqi;
        final long time;
    }
    
    private int tick = 0;
    private static final int RSSI = 0;
    private static final int LQI = 1;
}
