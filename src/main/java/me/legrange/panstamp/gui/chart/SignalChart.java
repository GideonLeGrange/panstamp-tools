package me.legrange.panstamp.gui.chart;

import me.legrange.panstamp.gui.model.SignalDataSet;

/**
 *
 * @author gideon
 */
public class SignalChart extends Chart {

     SignalChart(int addr, SignalDataSet sds) {
        super(String.format("Mote %2x RSSI/LQI", addr), sds);
    }
    
}
