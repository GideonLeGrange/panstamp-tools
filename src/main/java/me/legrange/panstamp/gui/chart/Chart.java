package me.legrange.panstamp.gui.chart;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

/**
 * Abstract super class for a panel with a chart.
 * @author gideon
 */
abstract class Chart extends JPanel {
    
    public String getTitle() {
        return title;
    }
    
    protected Chart(String title, String xAxisLabel, String yAxisLabel,  XYDataset ds) {
        this.title = title;
        chart = org.jfree.chart.ChartFactory.createXYLineChart("", yAxisLabel, xAxisLabel,
                ds, PlotOrientation.HORIZONTAL, true, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis rangeAxis = new DateAxis();
        rangeAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        plot.setRangeAxis(rangeAxis);
        plot.setDomainAxes(getYAxisFormat());
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(_WIDTH, _HEIGHT));
        chartPanel.setSize(_WIDTH, _HEIGHT);
        chartPanel.setPopupMenu(null);
        add(chartPanel);
        setSize(new Dimension(_WIDTH, _HEIGHT+35));
    }

    protected abstract ValueAxis[] getYAxisFormat();
    
    private final String title;
    private final JFreeChart chart;    
    private static final int _WIDTH = 500;
    private static final int _HEIGHT = 320;
}

