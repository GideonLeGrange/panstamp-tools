package me.legrange.panstamp.gui.model.tree;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.EndpointListener;
import me.legrange.panstamp.NetworkException;
import static me.legrange.panstamp.gui.model.Format.formatValue;

/**
 *
 * @author gideon
 */
public class EndpointNode extends NetworkTreeNode<Endpoint, Object> implements EndpointListener {

    public EndpointNode(Endpoint ep) {
        super(ep);
    }

    public Endpoint getEndpoint() {
        return (Endpoint) getUserObject();
    }

    @Override
    public String toString() {
        try {
            return String.format("%s = %s", getEndpoint().getName(), formatValue(getEndpoint()));
        } catch (NetworkException ex) {
            return "";
        }
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
    public void valueReceived(Endpoint ep, Object value) {
        reload();
    }

    @Override
    public Type getType() {
        return Type.ENDPOINT;
    }

    @Override
    void addChild(Object child) {
    }

}
