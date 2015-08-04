package me.legrange.panstamp.gui.task;

import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.swap.SwapException;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public class CreateTcpNetworkTask extends CreateNetworkTask {

    public CreateTcpNetworkTask(String tcpHost, int tcpPort, int channel, int address, int networkId) {
        super(channel, address, networkId);
        this.tcpPort = tcpPort;
        this.tcpHost = tcpHost;
    }

  
    @Override
    protected Network openNetwork() throws SwapException, NetworkException {
        update(5, "Opening TCP modem");
        return Network.openTcp(tcpHost, tcpPort);
    }

    private final String tcpHost;
    private final int tcpPort;

}
