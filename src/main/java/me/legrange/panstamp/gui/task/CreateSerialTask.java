package me.legrange.panstamp.gui.task;

import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.swap.SwapException;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public class CreateSerialTask extends CreateNetworkTask {

    public CreateSerialTask(String port, int speed, int channel, int address, int networkId) {
        super(channel, address, networkId);
        this.port = port;
        this.speed = speed;
    }

    @Override
    protected Network openNetwork() throws SwapException, NetworkException {
        update(5, "Opening serial modem");
        return Network.openSerial(port, speed);
    }

    private final String port;
    private final int speed;

}
