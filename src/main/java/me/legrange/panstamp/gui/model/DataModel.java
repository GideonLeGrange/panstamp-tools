package me.legrange.panstamp.gui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JPopupMenu;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.PanStamp;

/**
 *
 * @author gideon
 */
public final class DataModel {
    
    public DataModel() { 
    }

    public synchronized void addGateway(Gateway gw) {
        SignalCollector sc = new SignalCollector();
        gw.getSWAPModem().addListener(sc);
        collectors.put(gw, sc);
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
       SignalCollector sc = collectors.get(ps);
       return sc.getDataSet(ps.getAddress());
    }
        
    private final Map<Gateway, SignalCollector> collectors = new HashMap<>();
    private final MessageTableModel smm = new MessageTableModel();
    private final NetworkTreeModel ntm = NetworkTreeModel.create();
    private final EndpointTableModel etm = EndpointTableModel.create();
    private final NetworkTreeNodeRenderer snr = new NetworkTreeNodeRenderer(this);
}
