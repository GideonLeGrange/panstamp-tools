package me.legrange.panstamp.gui.model.tree;

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

    @Override
    public int compareTo(NetworkTreeNode<Endpoint, Object> node) {
        EndpointNode epn = (EndpointNode)node;
        return getEndpoint().getName().compareTo(epn.getEndpoint().getName()); // FIXME: Alpha ordering is so it compiles, but we need natural ordering
//        return getEndpoint().compareTo(epn.getEndpoint());
    }

}
