package me.legrange.panstamp.gui.chart;

import me.legrange.panstamp.gui.model.EndpointDataSet;

/**
 *
 * @author gideon
 */
public class EndpointChart extends Chart {

     EndpointChart(EndpointDataSet eds) {
        super(String.format("%s", eds.getName()), eds);
    }
    
}
