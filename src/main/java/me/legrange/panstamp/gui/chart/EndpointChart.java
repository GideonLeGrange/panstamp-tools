package me.legrange.panstamp.gui.chart;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import javax.swing.JPanel;
import me.legrange.panstamp.gui.model.EndpointDataSet;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

/**
 *
 * @author gideon
 */
public class EndpointChart extends JPanel {

    EndpointChart(EndpointDataSet eds) {
        String title = String.format("%s", eds.getName());
        chart = org.jfree.chart.ChartFactory.createXYLineChart(title, title, "Time",
                eds, PlotOrientation.HORIZONTAL, true, false, false);
        DateAxis rangeAxis = new DateAxis();
        rangeAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(rangeAxis);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setSize(500, 270);
        add(chartPanel);
        setPreferredSize(new java.awt.Dimension(500, 270));
        setSize(500, 270);
        setMinimumSize(new Dimension(320, 200));
    }

    private final JFreeChart chart;

}
