package me.legrange.panstamp.gui.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.EndpointListener;
import me.legrange.panstamp.GatewayException;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author gideon
 */
public class EndpointDataSet implements XYDataset, EndpointListener {

    public Endpoint getEndpoint() {
        return ep;
    }

    @Override
    public void valueReceived(Endpoint ep, Object value) {
                addSample(asDouble(ep));
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
            case SAMPLE:
                return data.get(index).sample;
            default:
                throw new IndexOutOfBoundsException(String.format("No series %d", series));
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
        return 1;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        switch (series) {
            case SAMPLE:
                return ep.getName();
            default:
                throw new IndexOutOfBoundsException(String.format("No series %d", series));
        }
    }

    @Override
    public int indexOf(Comparable key) {
        if (key.equals(ep.getName())) {
            return SAMPLE;
        }
        throw new IndexOutOfBoundsException("No series for key '" + key + "'");
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

    public EndpointDataSet(Endpoint ep) {
        this(ep, null);
    }
    
    public EndpointDataSet(Endpoint ep, String unit) {
        this.ep = ep;
        if (unit == null) { 
            List<String> units = ep.getUnits();
            if (!units.isEmpty()) {
                unit = units.get(0);
            }
        }
        this.unit = unit;
        ep.addListener(this);
    }

    private double asDouble(Endpoint ep) {
        try {
            switch (ep.getType()) {
                case BINARY:
                    Boolean b = (Boolean) ep.getValue();
                    return b ? 1.0 : 0.0;
                case INTEGER:
                    Integer i;
                    if (unit == null) {
                        i = (Integer) ep.getValue();
                    } else {
                        i = (Integer) ep.getValue(unit);
                    }
                    return i.doubleValue();
                case NUMBER:
                    Double d;
                    if (unit == null) {
                        d = (Double) ep.getValue();
                    } else {
                        d = (Double) ep.getValue(unit);
                    }
                    return d;
                case STRING:
                    return 0.0;
            }
        } catch (GatewayException e) {
            
        }
        return 0.0;
    }

    private void addSample(double sample) {
         data.add(new Entry(sample));
        for (DatasetChangeListener l : listeners) {
            l.datasetChanged(new DatasetChangeEvent(this, this));
        }
    }
    private final List<Entry> data = new CopyOnWriteArrayList<>();
    private final List<DatasetChangeListener> listeners = new CopyOnWriteArrayList<>();
    private final Endpoint ep;
    private final String unit;
    private DatasetGroup group;

    private class Entry {

        public Entry(double sample) {
            this.sample = sample;
            this.time = System.currentTimeMillis();
        }

        final double sample;
        final long time;
    }

    private static final int SAMPLE = 0;
}
