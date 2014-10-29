package me.legrange.panstamp.gui.chart;

import java.text.DecimalFormat;
import java.util.List;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.gui.model.chart.EndpointDataSet;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;

/**
 *
 * @author gideon
 */
public class EndpointChart extends Chart {

    EndpointChart(EndpointDataSet eds) {
        super(title(eds.getEndpoint()), "Time", yAxisName(eds.getEndpoint()), eds);
    }

    @Override
    protected ValueAxis[] getYAxisFormat() {
        NumberAxis domainAxis = new NumberAxis();
        domainAxis.setNumberFormatOverride(new DecimalFormat("0.0"));
        domainAxis.setAutoRange(true);
        return new ValueAxis[]{domainAxis};
    }

    protected String title(Endpoint ep) {
        PanStamp dev = ep.getRegister().getDevice();
        return String.format("%s[%d] - %s - %s", dev.getName(), dev.getAddress(), ep.getRegister().getName(), ep.getName());
    }

    private static String yAxisName(Endpoint ep) {
        List<String> units = ep.getUnits();
        if (units.isEmpty()) {
            return ep.getName();
        }
        return String.format("%s (%s)", ep.getName(), units.get(0));
    }
    
    private final Endpoint ep;

}
