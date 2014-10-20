package me.legrange.panstamp.gui.model;

import java.util.HashMap;
import java.util.Map;
import me.legrange.swap.MessageListener;
import me.legrange.swap.SwapMessage;

/**
 *
 * @author gideon
 */
public class SignalCollector implements MessageListener {


    public SignalDataSet getDataSet(int addr) {
        SignalDataSet sd = sets.get(addr);
        if (sd == null) {
            sd = new SignalDataSet();
            sets.put(addr, sd);
        }
        return sd;
    }

    @Override
    public void messageReceived(SwapMessage msg) {
        int addr = msg.getSender();
        SignalDataSet sd = getDataSet(addr);
        sd.addSample(msg.getRssi(), msg.getLqi());
        System.out.printf("addr: 5 rssi/lqi = %d/%d\n", msg.getSender(), msg.getRssi(), msg.getLqi());
    }

    @Override
    public void messageSent(SwapMessage msg) {
    }

    public SignalCollector() {
    }

    private final Map<Integer, SignalDataSet> sets = new HashMap<>();

}
