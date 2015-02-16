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
import me.legrange.panstamp.gui.model.EndpointNode;
import me.legrange.panstamp.gui.model.GatewayNode;
import me.legrange.panstamp.gui.model.NetworkTreeNode;
import me.legrange.panstamp.gui.model.PanStampNode;
import me.legrange.panstamp.gui.model.RegisterNode;
import me.legrange.panstamp.gui.model.WorldNode;

/**
 *
 * @author gideon
 */
public class TreeMenus {

    public JPopupMenu getPopupMenu(TreePath path) {
        NetworkTreeNode node = getSelectedNode();
        switch (node.getType()) {
            case WORLD:
                return getWorldPopupMenu();
            case GATEWAY:
                return getGatewayPopupMenu();
            case PANSTAMP:
                return getPanStampPopupMenu();
            case ENDPOINT:
                return getEndpointPopupMenu();
            default:
                return null;
        }
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

    TreeMenus(View view) {
        this.view = view;

    }

    private JPopupMenu getWorldPopupMenu() {
        if (worldPopupMenu == null) {
            worldPopupMenu = new JPopupMenu(((WorldNode)getSelectedNode()).toString());
            for (JMenuItem i : getWorldMenuItems()) {
                worldPopupMenu.add(i);
            }
        }
        return worldPopupMenu;
    }

    private JPopupMenu getGatewayPopupMenu() {
        if (gatewayPopupMenu == null) {

            gatewayPopupMenu = new JPopupMenu(((GatewayNode)getSelectedNode()).toString());
            for (JMenuItem c : getGatewayMenuItems()) {
                gatewayPopupMenu.add(c);
            }
        }
        return gatewayPopupMenu;
    }

    private JPopupMenu getPanStampPopupMenu() {
        if (panstampPopupMenu == null) {
            panstampPopupMenu = new JPopupMenu(((PanStampNode)getSelectedDevice()).toString());
            for (JComponent item : getPanStampMenuItems()) {
                panstampPopupMenu.add(item);
            }

        }
        return panstampPopupMenu;
    }

    private JPopupMenu getEndpointPopupMenu() {
        if (endpointPopupMenu == null) {
            endpointPopupMenu = new JPopupMenu(((EndpointNode)getSelectedNode()).toString());
            final JMenuItem graphItem = new JMenuItem("Data graph...");

            for (JComponent item : getEndpointMenuItems()) {
                endpointPopupMenu.add(item);
            }
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
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case WORLD:
                case GATEWAY:
                    return null;
                case PANSTAMP:
                    return ((PanStampNode) node).getPanStamp();
                case REGISTER:
                    return ((RegisterNode) node).getRegister().getDevice();
                case ENDPOINT:
                    return ((EndpointNode) node).getEndpoint().getRegister().getDevice();
                default:
                    return null;
            }
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
        list.add(getPanstampSettingsItem());
        list.add(getPanstampParametersItem());
        list.add(getPanstampLqiItem());

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

    /**
     * build the settings item from the panStamp menu
     */
    private JMenuItem getPanstampSettingsItem() {
        final JMenuItem settingsItem = new JMenuItem("Settings...") {
            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && dev.getGateway().isOpen();
            }
        };
        settingsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showPanStampSettingsDialog(getSelectedDevice());
            }
        });
        return settingsItem;
    }

    /**
     * build the parameters item from the panStamp menu
     */
    private JMenuItem getPanstampParametersItem() {
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
                        Logger.getLogger(TreeMenus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return false;
            }
        };
        return paramItem;
    }

    private JMenuItem getPanstampLqiItem() {
        final JMenuItem graphItem = new JMenuItem("RSSI/LQI Graph...") {
            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && (dev.getGateway().isOpen());
            }
        };
        graphItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showSignalChart(getSelectedDevice());
            }
        });
        return graphItem;
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
                allItem.setSelected(false);
                noneItem.setSelected(false);
                intItem.setSelected(false);
                if (e.getSource().equals(noneItem)) {
                    noneItem.setSelected(true);
                    setSelectedRegisterDisplay(PanStampNode.RegisterDisplay.NONE);
                }
                if (e.getSource().equals(allItem)) {
                    allItem.setSelected(true);
                    setSelectedRegisterDisplay(PanStampNode.RegisterDisplay.ALL);
                }
                if (e.getSource().equals(intItem)) {
                    intItem.setSelected(true);
                    setSelectedRegisterDisplay(PanStampNode.RegisterDisplay.INTERESTING);
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
                    return ((PanStampNode) ((EndpointNode) node).getEndpoint()).getRegisterDisplay();
                default:
                    return null;
            }
        }
        return null;
    }

    private void setSelectedRegisterDisplay(PanStampNode.RegisterDisplay rd) {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case PANSTAMP:
                    ((PanStampNode) node).setRegisterDisplay(rd);
                    break;
                case REGISTER:
                    ((PanStampNode) ((RegisterNode) node).getParent()).setRegisterDisplay(rd);
                    break;
                case ENDPOINT:
                    ((PanStampNode) ((EndpointNode) node).getEndpoint()).setRegisterDisplay(rd);
            }
        }
    }

    private NetworkTreeNode getSelectedNode() {
        TreePath path = view.getTree().getSelectionPath();
        if (path != null) { 
            return (NetworkTreeNode) path.getLastPathComponent();
        }
        return null;
    }

    private final View view;
    private JPopupMenu gatewayPopupMenu;
    private JPopupMenu worldPopupMenu;
    private JPopupMenu panstampPopupMenu;
    private JPopupMenu endpointPopupMenu;
}
