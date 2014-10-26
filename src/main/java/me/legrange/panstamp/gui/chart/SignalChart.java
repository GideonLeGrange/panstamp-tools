package me.legrange.panstamp.gui.chart;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import me.legrange.panstamp.gui.model.chart.SignalDataSet;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

/**
 *
 * @author gideon
 */
public class SignalChart extends JPanel {

    private static final int _WIDTH = 500;
    private static final int _HEIGHT = 270;

    SignalChart(int addr, SignalDataSet sds) {
        String title = String.format("Mote %2x RSSI/LQI", addr);
        chart = org.jfree.chart.ChartFactory.createXYLineChart(title, "RSSI/LQI", "Time",
                sds, PlotOrientation.HORIZONTAL, true, false, false);
        DateAxis rangeAxis = new DateAxis();
        rangeAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(rangeAxis);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(_WIDTH, _HEIGHT));
        chartPanel.setSize(_WIDTH, _HEIGHT);
        chartPanel.setPopupMenu(null);
        add(chartPanel);
        setMinimumSize(new Dimension(_WIDTH, _HEIGHT));
        setSize(new Dimension(_WIDTH, _HEIGHT));
        setMaximumSize(new Dimension(_WIDTH, _HEIGHT));
    }

    private final JFreeChart chart;
}
