package me.legrange.panstamp.gui.model;

import me.legrange.panstamp.gui.model.tree.NetworkTreeModel;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import me.legrange.panstamp.DeviceLibrary;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.definition.CompoundDeviceLibrary;
import me.legrange.panstamp.tools.store.DataStoreException;
import me.legrange.panstamp.tools.store.Store;
import me.legrange.panstamp.xml.ClassLoaderLibrary;
import me.legrange.panstamp.xml.FileLibrary;

/**
 * A data model that provides the different view models required.
 *
 * @author gideon
 */
public final class Model {

    public Model() throws DataStoreException {
        store = Store.openFile(dataFileName());
        setDeviceLibary(store.getLibary());
    }

    public void start() throws DataStoreException, NetworkException {
        List<Network> stored = store.loadNetworks();
        for (Network gw : stored) {
            addGateway(gw);
        }
    }
    public synchronized void addGateway(Network gw) throws NetworkException {
        networks.add(gw);
        store.addGateway(gw);
        gw.setDeviceLibrary(devLib);
        SignalCollector sc = new SignalCollector();
        gw.getSwapModem().addListener(sc);
        signalCollectors.put(gw, sc);
        endpointCollectors.put(gw, new EndpointCollector(gw));
        ntm.addGateway(gw);
        etm.addGateway(gw);
        gw.getSwapModem().addListener(smm);
    }

    public void deleteGateway(Network gw) throws NetworkException {
        if (gw.isOpen()) {
            gw.close();
        }
        gw.getSwapModem().removeListener(smm);
        etm.removeGateway(gw);
        ntm.removeGateway(gw);
        EndpointCollector ec = endpointCollectors.remove(gw);
        ec.stop();
        SignalCollector sc = signalCollectors.get(gw);
        gw.getSwapModem().removeListener(sc);
        store.removeGateway(gw);
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
        SignalCollector sc = signalCollectors.get(ps.getNetwork());
        return sc.getDataSet(ps.getAddress());
    }

    public EndpointDataSet getEndpointDataSet(Endpoint ep) {
        EndpointCollector ec = endpointCollectors.get(ep.getRegister().getDevice().getNetwork());
        return ec.getDataSet(ep);
    }

    public void setFileLibrary(String dir) throws DataStoreException {
        FileLibrary lib = new FileLibrary(new File(dir));
        store.setLibrary(lib);
        setDeviceLibary(lib);
    }

    public void clearFileLibry() throws DataStoreException {
        setDeviceLibary(null);
        store.setLibrary(null);
    }
    
    public String getFileLibary() throws DataStoreException {
        FileLibrary lib = store.getLibary();
        if (lib != null) {
            return lib.getDirectory();
        }
        else {
            return "";
        }
     }

    private void setDeviceLibary(DeviceLibrary lib) {
        if (lib != null) {
            devLib = new CompoundDeviceLibrary(lib, new ClassLoaderLibrary());
        } else {
            devLib = new ClassLoaderLibrary();

        }
        for (Network nw : networks) {
            nw.setDeviceLibrary(devLib);
        }
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

    private final Map<Network, SignalCollector> signalCollectors = new HashMap<>();
    private final Map<Network, EndpointCollector> endpointCollectors = new HashMap<>();
    private final MessageTableModel smm = new MessageTableModel();
    private final NetworkTreeModel ntm = NetworkTreeModel.create();
    private final EndpointTableModel etm = EndpointTableModel.create();
    private final Map<Endpoint, EndpointDataSet> epds = new HashMap<>();
    private final Map<PanStamp, Boolean> hasParams = new HashMap<>();
    private final List<Network> networks = new LinkedList<>();
    private final Store store;
    private DeviceLibrary devLib;
    private static final String DATA_PATH = "panstamp";


}
