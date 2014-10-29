package me.legrange.panstamp.gui.chart;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.gui.model.DataModel;

/**
 * Create and lookup charts in frames for different data sources. 
 * @author gideon
 */
public class ChartFactory {

    public static ChartFactory getFactory(DataModel model) {
        ChartFactory fact = facts.get(model);
        if (fact == null) {
            fact = new ChartFactory(model);
            facts.put(model, fact);
        }
        return fact;
    }

    public JFrame getSignalChart(PanStamp ps) {
        JFrame frame = signalCharts.get(ps);
        if (frame == null) {
            frame = frameChart(new SignalChart(ps.getAddress(), model.getSignalDataSet(ps)));
            signalCharts.put(ps, frame);
        }
        return frame;
    }

    public JFrame getEndpointChart(Endpoint ep) {
        JFrame frame = endpointCharts.get(ep);
        if (frame == null) {
            frame = frameChart(new EndpointChart(model.getEndpointDataSet(ep)));
            endpointCharts.put(ep, frame);
        }
        return frame;
    }

    private JFrame frameChart(Chart chart) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle(chart.getTitle());
        frame.setContentPane(chart);
        frame.setSize(chart.getSize());
        frame.setResizable(false);
        return frame;
    }

    private ChartFactory(DataModel model) {
        this.model = model;
    }

    private final DataModel model;
    private final Map<PanStamp, JFrame> signalCharts = new HashMap<>();
    private final Map<Endpoint, JFrame> endpointCharts = new HashMap<>();
    private final static Map<DataModel, ChartFactory> facts = new HashMap<>();
}
