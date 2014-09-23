package me.legrange.panstamp.gui;

import java.util.List;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.GatewayException;

/**
 *
 * @author gideon
 */
public final class Format {
    
    public static  String formatValue(Endpoint ep) throws GatewayException {
        List<String> units = ep.getUnits();
        String unit = "";
        Object val;
        if (ep.hasValue()) {
            val = ep.getValue();
            if (!units.isEmpty()) {
                unit = units.get(0);
                val = ep.getValue(unit);
            }
            if (val instanceof Double) {
                return String.format("%.1f %s", ((Double) val), unit);
            }
            if (val instanceof Boolean) {
                return ((Boolean) val) ? "on" : "off";
            }
        }
        else {
            val = "<unknown>";
        }
        return String.format("%s%s", val.toString(), unit);
    }
}
