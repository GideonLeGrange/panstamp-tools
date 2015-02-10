package me.legrange.panstamp.gui.view;

import java.text.DecimalFormat;
import me.legrange.panstamp.gui.model.SignalDataSet;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;

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

    @Override
    protected ValueAxis[] getYAxisFormat() {
                NumberAxis domainAxis = new NumberAxis();
        domainAxis.setNumberFormatOverride(new DecimalFormat("0"));
        domainAxis.setAutoRange(true);
        return new ValueAxis[]{domainAxis, domainAxis};
    }

}
