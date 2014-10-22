package me.legrange.panstamp.gui.model;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JPopupMenu;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.PanStamp;

/**
 * A data model that provides the different view models required.
 *
 * @author gideon
 */
public final class DataModel {

    public DataModel() {
    }

    public synchronized void addGateway(Gateway gw) throws GatewayException {
        SignalCollector sc = new SignalCollector();
        gw.getSWAPModem().addListener(sc);
        signalCollectors.put(gw, sc);
        endpointCollectors.put(gw, new EndpointCollector(gw));
        ntm.addGateway(gw);
        etm.addGateway(gw);
        gw.getSWAPModem().addListener(smm);

    }

    public TreeModel getTreeModel() {
        return ntm;
    }

    public TableModel getEndpointTableModel() {
        return etm;
    }

    public TableModel getSWAPTableModel() {
        return smm;
    }

    public TreeCellRenderer getTreeCellRenderer() {
        return snr;
    }

    public JPopupMenu getTreePopupMenu(TreePath path) {
        return snr.getPopupMenu(path);
    }

    public SignalDataSet getSignalDataSet(PanStamp ps) {
        SignalCollector sc = signalCollectors.get(ps.getGateway());
        return sc.getDataSet(ps.getAddress());
    }

    public EndpointDataSet getEndpointDataSet(Endpoint ep) {
        EndpointCollector ec = endpointCollectors.get(ep.getRegister().getDevice().getGateway());
        return ec.getDataSet(ep);
    }

    private final Map<Gateway, SignalCollector> signalCollectors = new HashMap<>();
    private final Map<Gateway, EndpointCollector> endpointCollectors = new HashMap<>();
    private final MessageTableModel smm = new MessageTableModel();
    private final NetworkTreeModel ntm = NetworkTreeModel.create();
    private final EndpointTableModel etm = EndpointTableModel.create();
    private final NetworkTreeNodeRenderer snr = new NetworkTreeNodeRenderer(this);
    private final Map<Endpoint, EndpointDataSet> epds = new HashMap<>();

}
