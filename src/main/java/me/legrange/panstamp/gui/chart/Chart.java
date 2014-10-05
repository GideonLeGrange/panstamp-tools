package me.legrange.panstamp.gui.chart;

import java.awt.Dimension;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author gideon
 */
abstract class Chart extends JPanel {

    Chart(String title, XYDataset data) {
        super();
        JFreeChart chart = createChart(data, title);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        add(chartPanel);
        setMinimumSize(new Dimension(320, 200));
        setSize(new Dimension(320,200));
        setMaximumSize(new Dimension(320,200));
    }

    private JFreeChart createChart(XYDataset data, String title) {
        return ChartFactory.createXYLineChart(title, title, "Time",
                data, PlotOrientation.HORIZONTAL, true, false, false);
    }

}
