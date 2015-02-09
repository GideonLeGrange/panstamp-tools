package me.legrange.panstamp.gui.mvc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.gui.model.chart.EndpointCollector;
import me.legrange.panstamp.gui.model.chart.EndpointDataSet;
import me.legrange.panstamp.gui.model.chart.SignalCollector;
import me.legrange.panstamp.gui.model.chart.SignalDataSet;
import me.legrange.panstamp.gui.model.tree.NetworkTreeModel;
import me.legrange.panstamp.tools.store.DataStoreException;
import me.legrange.panstamp.tools.store.DataUpdater;
import me.legrange.panstamp.tools.store.Store;

/**
 * A data model that provides the different view models required.
 *
 * @author gideon
 */
public final class Model {

    public Model() throws DataStoreException {
        store = Store.openFile(dataFileName());
        updater = new DataUpdater(store);
    }

    public void start() throws DataStoreException, GatewayException {
        List<Gateway> stored = store.load();
        for (Gateway gw : stored) {
            addGateway(gw);
        }
    }


    public synchronized void addGateway(Gateway gw) throws GatewayException {
        SignalCollector sc = new SignalCollector();
        gw.getSWAPModem().addListener(sc);
        signalCollectors.put(gw, sc);
        endpointCollectors.put(gw, new EndpointCollector(gw));
        ntm.addGateway(gw);
        etm.addGateway(gw);
        gw.getSWAPModem().addListener(smm);
        updater.addGateway(gw);
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

    public SignalDataSet getSignalDataSet(PanStamp ps) {
        SignalCollector sc = signalCollectors.get(ps.getGateway());
        return sc.getDataSet(ps.getAddress());
    }

    public EndpointDataSet getEndpointDataSet(Endpoint ep) {
        EndpointCollector ec = endpointCollectors.get(ep.getRegister().getDevice().getGateway());
        return ec.getDataSet(ep);
    }

    private static String dataFileName() {
        String name = System.getProperty("user.home") + File.separator;
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            name = name + DATA_PATH;
        } else {
            name = name + "." + DATA_PATH;
        }
        File path = new File(name);
        if (!path.exists()) {
            path.mkdir();
        }
        return name + File.separator + "panstamp.json";
    }

    private final Map<Gateway, SignalCollector> signalCollectors = new HashMap<>();
    private final Map<Gateway, EndpointCollector> endpointCollectors = new HashMap<>();
    private final MessageTableModel smm = new MessageTableModel();
    private final NetworkTreeModel ntm = NetworkTreeModel.create();
    private final EndpointTableModel etm = EndpointTableModel.create();
    private final Map<Endpoint, EndpointDataSet> epds = new HashMap<>();
    private final Map<PanStamp, Boolean> hasParams = new HashMap<>();
    private final Store store;
    private final DataUpdater updater;
    private static final String DATA_PATH = "panstamp";

}
