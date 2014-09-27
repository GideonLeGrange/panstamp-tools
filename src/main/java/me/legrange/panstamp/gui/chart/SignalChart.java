package me.legrange.panstamp.gui.chart;

import javax.swing.JFrame;
import me.legrange.panstamp.PanStamp;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author gideon
 */
public class SignalChart extends JFrame {

    public SignalChart(int addr) {
        super("RSSI/LQI");
        
        XYDataset dataset = createDataset(addr);
        JFreeChart chart = createChart(dataset, "RSSI/LQI for " + addr);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
        
    }

    private JFreeChart createChart(XYDataset data, String title) {

        return ChartFactory.createXYLineChart(title, "RSSI & LQI", "Time",
                data, PlotOrientation.HORIZONTAL, true, false, false);
    }

    private XYDataset createDataset(int addr) {
        SignalDataSet ds = SignalCollector.getInstance().getDataSet(addr);
        return ds;
    }
}
