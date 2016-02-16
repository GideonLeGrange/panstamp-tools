package me.legrange.panstamp.gui.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.Network;
import me.legrange.panstamp.NetworkException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.Register;
import me.legrange.panstamp.gui.WaitDialog;
import me.legrange.panstamp.gui.model.tree.EndpointNode;
import me.legrange.panstamp.gui.model.tree.NetworkNode;
import me.legrange.panstamp.gui.model.tree.NetworkTreeNode;
import me.legrange.panstamp.gui.model.tree.PanStampNode;
import me.legrange.panstamp.gui.model.tree.RegisterNode;
import me.legrange.panstamp.gui.task.Task;

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
                        case NETWORK:
                            return getNetworkPopupMenu();
                        case PANSTAMP:
                            return getPanStampPopupMenu();
                        case REGISTER:
                            return getRegisterPopupMenu();
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
        JMenu menu = new JMenu("Networks");
        for (JMenuItem c : getWorldMenuItems()) {
            menu.add(c);
        }
        menu.addMenuListener(menuEnableListener);
        return menu;
    }

    JMenu getNetworkMenu() {
        final JMenu menu = new JMenu("Network");
        for (JMenuItem c : getNetworkMenuItems()) {
            menu.add(c);
        }
        menu.addMenuListener(menuEnableListener);
        return menu;
    }

    JMenu getPanStampMenu() {
        JMenu menu = new JMenu("Device");
        for (JComponent c : getPanStampMenuItems()) {
            menu.add(c);
        }
        menu.addMenuListener(menuEnableListener);
        return menu;
    }

    JMenu getRegisterMenu() {
        JMenu menu = new JMenu("Register");
        for (JComponent c : getRegisterMenuItems()) {
            menu.add(c);
        }
        menu.addMenuListener(menuEnableListener);
        return menu;
    }

    JMenu getEndpointMenu() {
        JMenu menu = new JMenu("Endpoint");
        for (JComponent c : getEndpointMenuItems()) {
            menu.add(c);
        }
        menu.addMenuListener(menuEnableListener);
        return menu;
    }

    Menus(View view) {
        this.view = view;

    }

    private JPopupMenu getWorldPopupMenu() {
        JPopupMenu menu = getWorldMenu().getPopupMenu();
        menu.addPopupMenuListener(popupDisplayListener);
        return menu;
    }

    private JPopupMenu getNetworkPopupMenu() {
        JPopupMenu menu = getNetworkMenu().getPopupMenu();
        menu.addPopupMenuListener(popupDisplayListener);
        return menu;
    }

    private JPopupMenu getPanStampPopupMenu() {
        JPopupMenu menu = getPanStampMenu().getPopupMenu();
        menu.addPopupMenuListener(popupDisplayListener);
        return menu;
    }

    private JPopupMenu getRegisterPopupMenu() {
        JPopupMenu menu = getRegisterMenu().getPopupMenu();
        menu.addPopupMenuListener(popupDisplayListener);
        return menu;
    }

    private JPopupMenu getEndpointPopupMenu() {
        JPopupMenu menu = getEndpointMenu().getPopupMenu();
        menu.addPopupMenuListener(popupDisplayListener);
        return menu;
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

    private List<JMenuItem> getNetworkMenuItems() {
        List<JMenuItem> list = new LinkedList<>();
        final JMenuItem openItem = new JMenuItem("Open") {

            @Override
            public boolean isEnabled() {
                Network gw = getSelectedGateway();
                return (gw != null) && (!gw.isOpen());
            }
        };
        openItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                WaitDialog wd = new WaitDialog(null,
                        new Task() {

                            @Override
                            protected Object run() throws Throwable {
                                update(10, "Opening");
                                Network nw = getSelectedGateway();
                                nw.open();
                                return nw;
                            }

                        });
                try {
                    wd.start();
                } catch (Throwable ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                }
            }
        });
        list.add(openItem);
        final JMenuItem closeItem = new JMenuItem("Close") {

            @Override
            public boolean isEnabled() {
                Network gw = getSelectedGateway();
                return (gw != null) && (gw.isOpen());
            }
        };
        closeItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                WaitDialog wd = new WaitDialog(null,
                        new Task() {

                            @Override
                            protected Object run() throws Throwable {
                                update(10, "Closing");
                                Network nw = getSelectedGateway();
                                nw.close();
                                return nw;
                            }

                        });
                try {
                    wd.start();
                } catch (Throwable ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

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
                } catch (NetworkException ex) {
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
    private Network getSelectedGateway() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case WORLD:
                    return null;
                case NETWORK:
                    return ((NetworkNode) node).getNetwork();
                case PANSTAMP:
                    return ((PanStampNode) node).getPanStamp().getNetwork();
                case REGISTER:
                    return ((RegisterNode) node).getRegister().getDevice().getNetwork();
                case ENDPOINT:
                    return ((EndpointNode) node).getEndpoint().getRegister().getDevice().getNetwork();
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
     * Determine which register, if any, is currently selected in the tree view.
     * A register is considered selected if a register node or one of it's descendants
     * is selected.
     *
     * @return The selected register, or null if none is selected.
     */
    private Register getSelectedRegister() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            if (node.getType() == NetworkTreeNode.Type.REGISTER) {
                return ((RegisterNode) node).getRegister();
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
                    psn.getPanStamp().getNetwork().removeDevice(psn.getPanStamp().getAddress());
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
                return (dev != null) && dev.getNetwork().isOpen();
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
                if ((dev != null) && dev.getNetwork().isOpen()) {
                    for (Register reg : getSelectedDevice().getRegisters()) {
                        if (!reg.getParameters().isEmpty()) {
                            return true;
                        }

                    }
                }
                return false;
            }
        };
        final JMenuItem graphItem = new JMenuItem("RSSI/LQI Graph...") {
            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && (dev.getNetwork().isOpen());
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
        // Disabled for now, have issue with OSX menu. 
        return list;
    }

    private List<JComponent> getRegisterMenuItems() {
        List<JComponent> items = new LinkedList<>();
        final JMenuItem reqItem = new JMenuItem("Request value") {
            @Override
            public boolean isEnabled() {
                Register reg = getSelectedRegister();
                return (reg != null) && reg.getDevice().getNetwork().isOpen();
            }
        };
        reqItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getSelectedRegister().requestValue();
                } catch (NetworkException ex) {
                    Logger.getLogger(Menus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        items.add(reqItem);
        return items;
    }

    private List<JComponent> getEndpointMenuItems() {
        List<JComponent> items = new LinkedList<>();
        final JMenuItem setItem = new JMenuItem("Set value...") {
            @Override
            public boolean isEnabled() {
                Endpoint ep = getSelectedEndpoint();
                return (ep != null) && ep.getRegister().getDevice().getNetwork().isOpen() && ep.isOutput();
            }
        };
        setItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                view.showSetValueDialog(getSelectedEndpoint());
            }
        });
        items.add(setItem);
        final JMenuItem reqItem = new JMenuItem("Request value") {
            @Override
            public boolean isEnabled() {
                Endpoint ep = getSelectedEndpoint();
                return (ep != null) && ep.getRegister().getDevice().getNetwork().isOpen();
            }
        };
        reqItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getSelectedEndpoint().requestValue();
                } catch (NetworkException ex) {
                    Logger.getLogger(Menus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        items.add(reqItem);
        items.add(new JSeparator());
        final JMenuItem graphItem = new JMenuItem("Data graph...") {
            @Override
            public boolean isEnabled() {
                Endpoint ep = getSelectedEndpoint();
                return (ep != null) && (ep.getRegister().getDevice().getNetwork().isOpen());
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
        final JRadioButtonMenuItem allItem = getPanStampRegisterMenuItem("All", PanStampNode.RegisterDisplay.ALL);
        final JRadioButtonMenuItem intItem = getPanStampRegisterMenuItem("Interesting", PanStampNode.RegisterDisplay.INTERESTING);
        final JRadioButtonMenuItem noneItem = getPanStampRegisterMenuItem("None", PanStampNode.RegisterDisplay.NONE);
        ButtonGroup group = new ButtonGroup();
        group.add(allItem);
        group.add(intItem);
        group.add(noneItem);
        regsMenu.add(allItem);
        regsMenu.add(intItem);
        noneItem.setSelected(true);
        regsMenu.add(noneItem);
        regsMenu.addMenuListener(new MenuListener() {

            @Override
            public void menuSelected(MenuEvent e) {
                allItem.setSelected(allItem.isSelected());
                intItem.setSelected(intItem.isSelected());
                noneItem.setSelected(noneItem.isSelected());
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        return regsMenu;
    }

    private JRadioButtonMenuItem getPanStampRegisterMenuItem(String label, final PanStampNode.RegisterDisplay regD) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(label) {
            @Override
            public boolean isSelected() {
                PanStampNode.RegisterDisplay rd = getSelectedRegisterDisplay();
                return (rd != null) && (rd == regD);
            }

            @Override
            public boolean isEnabled() {
                PanStamp dev = getSelectedDevice();
                return (dev != null) && (dev.getNetwork().isOpen());
            }
        };
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component com : ((JMenuItem) e.getSource()).getParent().getComponents()) {
                    JMenuItem item = (JMenuItem) com;
                    item.setSelected(item == e.getSource());
                }
                setSelectedRegisterDisplay(PanStampNode.RegisterDisplay.valueOf(e.getActionCommand()));
            }
        });
        item.setActionCommand(regD.name());
        return item;
    }

    private PanStampNode.RegisterDisplay getSelectedRegisterDisplay() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case WORLD:
                case NETWORK:
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

    private NetworkNode getSelectedGatewayNode() {
        NetworkTreeNode node = getSelectedNode();
        if (node != null) {
            switch (node.getType()) {
                case NETWORK:
                    return (NetworkNode) node;
                case PANSTAMP:
                    return (NetworkNode) node.getParent();
                case REGISTER:
                    return (NetworkNode) node.getParent().getParent();
                case ENDPOINT:
                    return (NetworkNode) node.getParent().getParent().getParent();
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
    private final MenuListener menuEnableListener = new MenuListener() {

        @Override
        public void menuSelected(MenuEvent e) {
            if (e.getSource() instanceof JMenu) {
                JMenu menu = (JMenu) e.getSource();
                int count = menu.getItemCount();
                for (int i = 0; i < count; ++i) {
                    JMenuItem item = menu.getItem(i);
                    if (item != null) {
                        item.setEnabled(item.isEnabled());
                    }
                }
            }
        }

        @Override
        public void menuDeselected(MenuEvent e) {
        }

        @Override
        public void menuCanceled(MenuEvent e) {
        }
    };

    private final PopupMenuListener popupDisplayListener = new PopupMenuListener() {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            if (e.getSource() instanceof JPopupMenu) {
                JPopupMenu menu = (JPopupMenu) e.getSource();
                for (Component com : menu.getComponents()) {
                    if (com instanceof JMenuItem) {
                        JMenuItem item = (JMenuItem) com;
                        item.setEnabled(item.isEnabled());
                    }
                }
            }

        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            if (e.getSource() instanceof JPopupMenu) {
                JPopupMenu menu = (JPopupMenu) e.getSource();
                menu.removePopupMenuListener(this);
            }
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
    };
    private static final ThreadLocal<NetworkTreeNode> selectedNode = new ThreadLocal<>();

}
