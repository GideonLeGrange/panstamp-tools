package me.legrange.panstamp.gui.chart;

/**
 *
 * @author gideon
 */
public class SignalChart extends Chart {

    public SignalChart(int addr) {
        super(String.format("Mote %2x RSSI/LQI", addr), SignalCollector.getInstance().getDataSet(addr));
    }
}
