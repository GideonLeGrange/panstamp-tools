package me.legrange.panstamp.gui.chart;

import java.util.List;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.gui.model.chart.EndpointDataSet;

/**
 *
 * @author gideon
 */
public class EndpointChart extends Chart {

    EndpointChart(EndpointDataSet eds) {
        super(title(eds.getEndpoint()), "Time", yAxisName(eds.getEndpoint()), eds);
    }

    private static String title(Endpoint ep) {
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

}
