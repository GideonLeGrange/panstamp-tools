
package me.legrange.panstamp.gui.model.tree;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.EndpointEvent;
import me.legrange.panstamp.EndpointListener;
import me.legrange.panstamp.GatewayException;
import static me.legrange.panstamp.gui.mvc.Format.formatValue;

/**
 *
 * @author gideon
 */
public class EndpointNode extends NetworkTreeNode implements EndpointListener {

    public EndpointNode(Endpoint ep) {
        super(ep);
    }
    
    public Endpoint getEndpoint() {
        return (Endpoint)getUserObject();
    }
    
    @Override
    public String toString() { 
        try {
            return String.format("%s = %s", getEndpoint().getName(), formatValue(getEndpoint()));
        } catch (GatewayException ex) {
            Logger.getLogger(EndpointNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    protected void start() {
        getEndpoint().addListener(this);
    }

    @Override
    protected void stop() {
        getEndpoint().removeListener(this);
        super.stop(); 
    }
    
    
    
    @Override
    public void endpointUpdated(EndpointEvent ev) {
        switch (ev.getType()) {
            case VALUE_RECEIVED : 
                reload();
                break;
        }
    }

    @Override
    public Type getType() {
        return Type.ENDPOINT;
    }

    
}
