package me.legrange.panstamp.gui.model;

import javax.swing.JPopupMenu;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.EndpointEvent;
import me.legrange.panstamp.EndpointListener;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayEvent;
import me.legrange.panstamp.GatewayListener;
import me.legrange.panstamp.PanStampEvent;
import me.legrange.panstamp.PanStampListener;
import me.legrange.panstamp.RegisterEvent;
import me.legrange.panstamp.RegisterListener;
import me.legrange.panstamp.gui.chart.SignalCollector;
import me.legrange.panstamp.gui.model.SWAPMessageModel.Direction;
import me.legrange.swap.MessageListener;
import me.legrange.swap.SwapMessage;

/**
 *
 * @author gideon
 */
public final class DataModel  implements GatewayListener, PanStampListener, RegisterListener, EndpointListener {
    
    public DataModel() { 
    }
        
    @Override
    public void gatewayUpdated(GatewayEvent ev) {
        switch (ev.getType()) {
            case DEVICE_DETECTED : 
                break;
        }
    }

    @Override
    public void deviceUpdated(PanStampEvent ev) {
        switch (ev.getType()) {
            case PRODUCT_CODE_UPDATE : 
                break;
            case REGISTER_DETECTED : 
                break;
            case SYNC_STATE_CHANGE : 
                break;
        }
    }

    @Override
    public void registerUpdated(RegisterEvent ev) {
        switch (ev.getType()) {
            case ENDPOINT_ADDED : 
                break;
            case VALUE_RECEIVED : 
                break;
        }
    }

    @Override
    public void endpointUpdated(EndpointEvent ep) {
        switch (ep.getType()) {
            case VALUE_RECEIVED : 
                break;
        }
    }

    public void addGateway(Gateway gw) {
        stm.addGateway(gw);
        etm.addGateway(gw);
        gw.getSWAPModem().addListener(sc);
        gw.getSWAPModem().addListener(new MessageListener() {

            @Override
            public void messageReceived(SwapMessage msg) {
               smm.add(msg, System.currentTimeMillis(), Direction.IN);
            }

            @Override
            public void messageSent(SwapMessage msg) {
               smm.add(msg, System.currentTimeMillis(), Direction.OUT);
            }
        });
    }
    
    public TreeModel getTreeModel() {
        return stm;
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
     
    private final SWAPMessageModel smm = SWAPMessageModel.create();
    private final SWAPTreeModel stm = SWAPTreeModel.create();
    private final EndpointTableModel etm = new EndpointTableModel();
    private final SWAPNodeRenderer snr = new SWAPNodeRenderer();
    private final SignalCollector sc = SignalCollector.getInstance();

}
