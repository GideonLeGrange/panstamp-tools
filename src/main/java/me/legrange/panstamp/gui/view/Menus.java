package me.legrange.panstamp.gui.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.gui.model.tree.EndpointNode;
import me.legrange.panstamp.gui.model.tree.GatewayNode;
import me.legrange.panstamp.gui.model.tree.NetworkTreeNode;
import me.legrange.panstamp.gui.model.tree.PanStampNode;
import me.legrange.panstamp.gui.model.tree.RegisterNode;
import me.legrange.panstamp.gui.model.tree.WorldNode;

/**
 *
 * @author gideon
 */
public class Menus {

    public JPopupMenu getPopupMenu(TreePath path) {
        if (path != null) {
            try {
                NetworkTreeNode node = (NetworkTreeNode) path.getLastPathComponent();
                selectedNode.set(node);
                if (node != null) {
                    switch (node.getType()) {
                        case WORLD:
                            return getWorldPopupMenu();
                        case GATEWAY:
                            return getGatewayPopupMenu();
                        case PANSTAMP:
                            return getPanStampPopupMenu();
                        case ENDPOINT:
                            return getEndpointPopupMenu();
                    }
                }
            } finally {
                selectedNode.remove();
            }
        }
        return null;
    }

    JMenu getWorldMenu() {
        JMenu menu = new JMenu("panStamp");
        for (JMenuItem c : getWorldMenuItems()) {
            menu.add(c);
        }
        return menu;
    }

    JMenu getGatewayMenu() {
        JMenu menu = new JMenu("Gateway");
        for (JMenuItem c : getGatewayMenuItems()) {
            menu.add(c);
        }
        return menu;
    }

    JMenu getPanStampMenu() {
        JMenu menu = new JMenu("Device");
        for (JComponent c : getPanStampMenuItems()) {
            menu.add(c);
        }
        return menu;
    }

    JMenu getRegisterMenu() {
        JMenu menu = new JMenu("Register");
        for (JComponent c : getRegisterMenuItems()) {
            menu.add(c);
        }
        return menu;
    }

    JMenu getEndpointMenu() {
        JMenu menu = new JMenu("Endpoint");
        for (JComponent c : getEndpointMenuItems()) {
            menu.add(c);
        }
        return menu;
    }

    Menus(View view) {
        this.view = view;

    }

    private JPopupMenu getWorldPopupMenu() {
        JPopupMenu worldPopupMenu = new JPopupMenu(((WorldNode) getSelectedNode()).toString());
        for (JMenuItem item : getWorldMenuItems()) {
            worldPopupMenu.add(item);
            item.setEnabled(item.isEnabled());

        }
        return worldPopupMenu;
    }

    private JPopupMenu getGatewayPopupMenu() {
        JPopupMenu gatewayPopupMenu = new JPopupMenu(((GatewayNode) getSelectedNode()).toString());
        for (JMenuItem item : getGatewayMenuItems()) {
            gatewayPopupMenu.add(item);
            item.setEnabled(item.isEnabled());
        }
        return gatewayPopupMenu;
    }

    private JPopupMenu getPanStampPopupMenu() {
        JPopupMenu panstampPopupMenu = new JPopupMenu(((PanStampNode) getSelectedNode()).toString());
        for (JComponent item : getPanStampMenuItems()) {
            panstampPopupMenu.add(item);
            item.setEnabled(item.isEnabled());
        }
        return panstampPopupMenu;
    }

    private JPopupMenu getEndpointPopupMenu() {
        JPopupMenu endpointPopupMenu = new JPopupMenu(((EndpointNode) getSelectedNode()).toString());
        final JMenuItem graphItem = new JMenuItem("Data graph...");

        for (JComponent item : getEndpointMenuItems()) {
            endpointPopupMenu.add(item);
            item.setEnabled(item.isEnabled());
        }
        return endpointPopupMenu;
    }

    private List<JMenuItem> getWorldMenuItems() {
        List<JMenuItem> items = new LinkedList<>();
        final JMenuItem addSerialItem = new JMenuItem("Add network...");
        addSerialItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showNetworkAddDialog();
            }
        });
        items.add(addSerialItem);
        return items;
    }

    private List<JMenuItem> getGatewayMenuItems() {
        List<JMenuItem> list = new LinkedList<>();
        final JMenuItem openItem = new JMenuItem("Open") {

            @Override
            public boolean isEnabled() {
                Gateway gw = getSelectedGateway();
                return (gw != null) && (!gw.isOpen());
            }
        };
        openItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getSelectedGateway().open();
                } catch (GatewayException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        list.add(openItem);
        final JMenuItem closeItem = new JMenuItem("Close") {

            @Override
            public boolean isEnabled() {
                Gateway gw = getSelectedGateway();
                return (gw != null) && (gw.isOpen());
            }
        };
        closeItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getSelectedGateway().close();
                } catch (GatewayException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        list.add(closeItem);

        final JMenuItem deleteItem = new JMenuItem("Delete") {
            @Override
            public boolean isEnabled() {
                return (getSelectedGateway() != null);
            }
        };

        deleteItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    view.getModel().deleteGateway(getSelectedGateway());
                } catch (GatewayException ex) {
                    Logger.getLogger(Menus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );
        list.add(deleteItem);
        return list;
    }

    /**
     * Determine which gateway, if any, is currently selected in the tree view.
     * A gateway is considered selected if a gateway node or one of it's
     * descendants is selected.
     *
     * @return The selected gateway, or null if none is selected.
     */
    private Gateway getSelectedGateway() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case WORLD:
                    return null;
                case GATEWAY:
                    return ((GatewayNode) node).getGateway();
                case PANSTAMP:
                    return ((PanStampNode) node).getPanStamp().getGateway();
                case REGISTER:
                    return ((RegisterNode) node).getRegister().getDevice().getGateway();
                case ENDPOINT:
                    return ((EndpointNode) node).getEndpoint().getRegister().getDevice().getGateway();
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Determine which device, if any, is currently selected in the tree view. A
     * device is considered selected if a device node or one of it's descendants
     * is selected.
     *
     * @return The selected device, or null if none is selected.
     */
    private PanStamp getSelectedDevice() {
        PanStampNode psn = getSelectedPanStampNode();
        if (psn != null) {
            return psn.getPanStamp();
        }
        return null;
    }

    /**
     * Determine which endpoint, if any, is currently selected in the tree view.
     * A endpoint is considered selected if a endpoint node is selected.
     *
     * @return The selected endpoint, or null if none is selected.
     */
    private Endpoint getSelectedEndpoint() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            if (node.getType() == NetworkTreeNode.Type.ENDPOINT) {
                return ((EndpointNode) node).getEndpoint();
            }
        }
        return null;
    }

    private List<JComponent> getPanStampMenuItems() {
        List<JComponent> list = new LinkedList<>();

        final JMenuItem deleteItem = new JMenuItem("Delete") {
            @Override
            public boolean isEnabled() {
                return (getSelectedDevice() != null);
            }
        };
        deleteItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PanStampNode psn = getSelectedPanStampNode();
                if (psn != null) {
                    psn.getPanStamp().getGateway().removeDevice(psn.getPanStamp().getAddress());
                }
            }
        }
        );
        list.add(deleteItem);
        list.add(new JSeparator());

        final JMenuItem settingsItem = new JMenuItem("Settings...") {
            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && dev.getGateway().isOpen();
            }
        };

        settingsItem.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        view.showPanStampSettingsDialog(getSelectedDevice());
                    }
                }
        );
        final JMenuItem paramItem = new JMenuItem("Parameters...") {
            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                if ((dev != null) && dev.getGateway().isOpen()) {
                    try {
                        for (Register reg : getSelectedDevice().getRegisters()) {
                            if (!reg.getParameters().isEmpty()) {
                                return true;
                            }

                        }
                    } catch (GatewayException ex) {
                        Logger.getLogger(Menus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return false;
            }
        };
        final JMenuItem graphItem = new JMenuItem("RSSI/LQI Graph...") {
            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && (dev.getGateway().isOpen());
            }
        };

        graphItem.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e
                    ) {
                        view.showSignalChart(getSelectedDevice());
                    }
                }
        );

        list.add(settingsItem);
        list.add(paramItem);
        list.add(graphItem);
        list.add(new JSeparator());
        // register selection
        list.add(getPanStampRegisterMenu());
        return list;
    }

    private List<JComponent> getRegisterMenuItems() {
        return Collections.EMPTY_LIST;
    }

    private List<JComponent> getEndpointMenuItems() {
        List<JComponent> items = new LinkedList<>();
        final JMenuItem graphItem = new JMenuItem("Data graph...") {
            @Override
            public boolean isEnabled() {
                Endpoint ep = getSelectedEndpoint();
                return (ep != null) && (ep.getRegister().getDevice().getGateway().isOpen());
            }
        };
        graphItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showEndpointChart(getSelectedEndpoint());
            }
        });
        items.add(graphItem);
        return items;
    }

    private JMenu getPanStampRegisterMenu() {
        final JMenu regsMenu = new JMenu("Show Standard Registers");
        final JRadioButtonMenuItem allItem = new JRadioButtonMenuItem("All") {
            @Override
            public boolean isSelected() {
                PanStampNode.RegisterDisplay rd = getSelectedRegisterDisplay();
                return (rd != null) && (rd == PanStampNode.RegisterDisplay.ALL);
            }

            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && (dev.getGateway().isOpen());
            }
        };
        final JRadioButtonMenuItem intItem = new JRadioButtonMenuItem("Interesting") {
            @Override
            public boolean isSelected() {
                PanStampNode.RegisterDisplay rd = getSelectedRegisterDisplay();
                return (rd != null) && (rd == PanStampNode.RegisterDisplay.INTERESTING);
            }

            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && (dev.getGateway().isOpen());
            }
        };
        final JRadioButtonMenuItem noneItem = new JRadioButtonMenuItem("None", true) {
            @Override
            public boolean isSelected() {
                PanStampNode.RegisterDisplay rd = getSelectedRegisterDisplay();
                return (rd != null) && (rd == PanStampNode.RegisterDisplay.NONE);
            }

            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && (dev.getGateway().isOpen());
            }
        };
        ActionListener regL = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                noneItem.setSelected(false);
                allItem.setSelected(false);
                intItem.setSelected(false);
                if (e.getSource().equals(noneItem)) {
                    setSelectedRegisterDisplay(PanStampNode.RegisterDisplay.NONE);
                    noneItem.setSelected(true);
                }
                if (e.getSource().equals(allItem)) {
                    setSelectedRegisterDisplay(PanStampNode.RegisterDisplay.ALL);
                    allItem.setSelected(true);
                }
                if (e.getSource().equals(intItem)) {
                    setSelectedRegisterDisplay(PanStampNode.RegisterDisplay.INTERESTING);
                    intItem.setSelected(true);
                }
            }

        };

        allItem.addActionListener(regL);
        noneItem.addActionListener(regL);
        intItem.addActionListener(regL);

        regsMenu.add(allItem);
        regsMenu.add(intItem);
        regsMenu.add(noneItem);

        return regsMenu;
    }

    private PanStampNode.RegisterDisplay getSelectedRegisterDisplay() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case WORLD:
                case GATEWAY:
                    return null;
                case PANSTAMP:
                    return ((PanStampNode) node).getRegisterDisplay();
                case REGISTER:
                    return ((PanStampNode) ((RegisterNode) node).getParent()).getRegisterDisplay();
                case ENDPOINT:
                    return ((PanStampNode) ((RegisterNode) ((EndpointNode) node).getParent()).getParent()).getRegisterDisplay();
                default:
                    return null;
            }
        }
        return null;
    }

    private void setSelectedRegisterDisplay(PanStampNode.RegisterDisplay rd) {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            PanStampNode psn = null;
            switch (node.getType()) {
                case PANSTAMP:
                    psn = ((PanStampNode) node);
                    break;
                case REGISTER:
                    psn = ((PanStampNode) ((RegisterNode) node).getParent());
                    psn.setRegisterDisplay(rd);
                    break;
                case ENDPOINT:
                    psn = ((PanStampNode) ((RegisterNode) ((EndpointNode) node).getParent()).getParent());
                    break;
                default:
                    return;
            }
            psn.setRegisterDisplay(rd);
        }
    }

    private PanStampNode getSelectedPanStampNode() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case PANSTAMP:
                    return (PanStampNode) node;
                case REGISTER:
                    return (PanStampNode) node.getParent();
                case ENDPOINT:
                    return (PanStampNode) node.getParent().getParent();
            }
        }
        return null;
    }

    private GatewayNode getSelectedGatewayNode() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case GATEWAY:
                    return (GatewayNode) node;
                case PANSTAMP:
                    return (GatewayNode) node.getParent();
                case REGISTER:
                    return (GatewayNode) node.getParent().getParent();
                case ENDPOINT:
                    return (GatewayNode) node.getParent().getParent().getParent();
            }
        }
        return null;

    }

    private NetworkTreeNode getSelectedNode() {
        if (selectedNode.get() != null) {
            return selectedNode.get();
        } else if (view != null) {
            TreePath path = view.getTree().getSelectionPath();
            if (path != null) {
                return (NetworkTreeNode) path.getLastPathComponent();
            }
        }
        return null;
    }

    private final View view;
    private static final ThreadLocal<NetworkTreeNode> selectedNode = new ThreadLocal<>();

}
