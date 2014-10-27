package me.legrange.panstamp.gui.chart;

import me.legrange.panstamp.gui.model.chart.SignalDataSet;

/**
 *
 * @author gideon
 */
public class SignalChart extends Chart {

    private static final int _WIDTH = 500;
    private static final int _HEIGHT = 320;

    SignalChart(int addr, SignalDataSet sds) {
        super(String.format("Mote %2x RSSI/LQI", addr), "Time", "RSSI/LQI", sds);
    }

}
