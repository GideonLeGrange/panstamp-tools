package me.legrange.panstamp.gui.task;

import me.legrange.swap.ModemSetup;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.swap.SwapException;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
 abstract class CreateNetworkTask extends Task<Network> {

    protected CreateNetworkTask(int channel, int address, int networkId) {
        this.channel = channel;
        this.address = address;
        this.networkId = networkId;
    }

    @Override
    protected final Network run() throws SwapException, NetworkException {
        Network gw = openNetwork();
        update(40, "Configuring modem");
        ModemSetup setup = gw.getSWAPModem().getSetup();
        update(60, "Configuring modem");
        setup.setChannel(channel);
        update(67, "Configuring modem");
        setup.setDeviceAddress(address);
        update(73, "Configuring modem");
        setup.setNetworkID(networkId);
        update(80, "Configuring modem");
        gw.getSWAPModem().setSetup(setup);
        update(90, "Completed");
        return gw;
    }
    
    protected abstract Network openNetwork()  throws SwapException, NetworkException;

    protected final int channel;
    protected final int address;
    protected final int networkId;

}
