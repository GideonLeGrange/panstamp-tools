package me.legrange.panstamp.gui.chart;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JPanel;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.gui.model.chart.EndpointDataSet;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

/**
 *
 * @author gideon
 */
public class EndpointChart extends JPanel {

    EndpointChart(EndpointDataSet eds) {
        Endpoint ep = eds.getEndpoint();
        chart = org.jfree.chart.ChartFactory.createXYLineChart(title(ep), yAxisName(ep), "Time", eds, PlotOrientation.HORIZONTAL, true, false, false);
        DateAxis rangeAxis = new DateAxis();
        rangeAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(rangeAxis);
        LegendItemCollection lic = new LegendItemCollection();
        lic.add(new LegendItem(yAxisName(ep), ChartColor.DARK_RED));
        plot.setFixedLegendItems(lic);
        ChartPanel chartPanel = new ChartPanel(chart, false, false, false, false, false);
   //     ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setSize(500, 270);
//        chartPanel.setPopupMenu(null);
        add(chartPanel);
        setPreferredSize(new java.awt.Dimension(500, 270));
        setSize(500, 270);
        setMinimumSize(new Dimension(320, 200));

    }

    private String title(Endpoint ep) {
        PanStamp dev = ep.getRegister().getDevice();
        return String.format("%s[%d] - %s - %s", dev.getName(), dev.getAddress(), ep.getRegister().getName(), ep.getName());
    }

    private String yAxisName(Endpoint ep) {
        List<String> units = ep.getUnits();
        if (units.isEmpty()) {
            return ep.getName();
        }
        return String.format("%s (%s)", ep.getName(), units.get(0));
    }

    private final JFreeChart chart;

}
