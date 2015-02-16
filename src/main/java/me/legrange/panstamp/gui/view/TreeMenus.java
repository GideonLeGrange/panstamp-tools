package me.legrange.panstamp.gui.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.GatewayException;
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
        NetworkTreeNode node = (NetworkTreeNode) path.getLastPathComponent();
        switch (node.getType()) {
            case WORLD:
                return getWorldPopupMenu((WorldNode) node);
            case GATEWAY:
                return getGatewayPopupMenu1((GatewayNode) node);
            case PANSTAMP:
                return getPanStampPopupMenu((PanStampNode) node);
            case ENDPOINT:
                return getEndpointPopupMenu((EndpointNode) node);
            default:
                return null;
        }
    }

    TreeMenus(View view) {
        this.view = view;

    }


    private JPopupMenu getWorldPopupMenu(final WorldNode wn) {
        JPopupMenu menu = popupMenus.get(wn);
        if (menu == null) {
            menu = new JPopupMenu(wn.toString());
            final JMenuItem addSerialItem = new JMenuItem("Add network...");
            addSerialItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    view.showNetworkAddDialog();
                }
            });
            menu.add(addSerialItem);
        }
        return menu;
    }
    
    private List<JComponent> getGatewayMenuItems() {
        List<JComponent> list = new LinkedList<>();
            final JMenuItem openItem = new JMenuItem() {
                
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
            final JMenuItem closeItem = new JMenuItem() {
                
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
            list.add(openItem);
            return list;
        }

    private Gateway getSelectedGateway() {
        TreePath path = view.getTree().getSelectionPath();
                NetworkTreeNode node = (NetworkTreeNode) path.getLastPathComponent();
        switch (node.getType()) {
            case WORLD:
                return null;
            case GATEWAY:
                return ((GatewayNode)node).getGateway();
            case PANSTAMP:
                return ((PanStampNode)node).getPanStamp().getGateway();
            case REGISTER : 
                return ((RegisterNode)node).getRegister().getDevice().getGateway();
            case ENDPOINT:
                return ((EndpointNode)node).getEndpoint().getRegister().getDevice().getGateway();
            default:
                return null;
        }
    }

    private JPopupMenu getGatewayPopupMenu1(final GatewayNode gn) {
        JPopupMenu menu = popupMenus.get(gn);
        if (menu == null) {

            menu = new JPopupMenu(gn.toString());

            final JMenuItem activateItem = new JMenuItem();
            activateItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Gateway gw = gn.getGateway();
                        if (gw.isOpen()) {
                            gw.close();
                        } else {
                            gw.open();
                        }
                        activateItem.setText(gw.isOpen() ? "Close" : "Open");
                    } catch (GatewayException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            activateItem.setText(gn.getGateway().isOpen() ? "Close" : "Open");
            menu.add(activateItem);
            popupMenus.put(gn, menu);
        }
        return menu;
    }
    
    private JPopupMenu getPanStampPopupMenu(final PanStampNode psn) {
        JPopupMenu menu = popupMenus.get(psn);
        if (menu == null) {
            menu = new JPopupMenu(psn.toString());
            for (JComponent item : getPanStampMenuItems(psn)) {
                menu.add(item);
            }
            popupMenus.put(psn, menu);
        }
                return menu;

    }

    private List<JComponent> getPanStampMenuItems(final PanStampNode psn) {
        List<JComponent> list = new LinkedList<>();
            list.add(getPanstampSettingsItem(psn));
            list.add(getPanstampParametersItem(psn));
            list.add(getPanstampLqiItem(psn));

            list.add(new JSeparator());
            // register selection
            list.add(getPanStampRegisterMenu(psn));
            return list;
    }

    /**
     * build the settings item from the panStamp menu
     */
    private JMenuItem getPanstampSettingsItem(final PanStampNode psn) {
        final JMenuItem settingsItem = new JMenuItem("Settings...");
        if (psn != null) {
            settingsItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    view.showPanStampSettingsDialog(psn.getPanStamp());
                }
            });
        } else {
            settingsItem.setEnabled(false);
        }
        return settingsItem;
    }

    /**
     * build the parameters item from the panStamp menu
     */
    private JMenuItem getPanstampParametersItem(final PanStampNode psn) {
        final JMenuItem paramItem = new JMenuItem("Parameters...");
        if (psn != null) {
            paramItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    view.showPanStampParamDialog(psn.getPanStamp());
                }
            });
            boolean hasParams = false;
            try {
                for (Register reg : psn.getPanStamp().getRegisters()) {
                    if (!reg.getParameters().isEmpty()) {
                        hasParams = true;
                        break;
                    }
                }
            } catch (GatewayException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            paramItem.setEnabled(hasParams);
        } else {
            paramItem.setEnabled(false);
        }
        return paramItem;
    }

    private JMenuItem getPanstampLqiItem(final PanStampNode psn) {
        final JMenuItem graphItem = new JMenuItem("RSSI/LQI Graph...");
        if (psn != null) {
            graphItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    view.showSignalChart(psn.getPanStamp());
                }
            });
        } else {
            graphItem.setEnabled(false);
        }
        return graphItem;
    }

    private JMenu getPanStampRegisterMenu(final PanStampNode psn) {
        final JMenu regsMenu = new JMenu("Show Standard Registers");
        if (psn != null) {
            final JRadioButtonMenuItem allItem = new JRadioButtonMenuItem("All");
            final JRadioButtonMenuItem intItem = new JRadioButtonMenuItem("Interesting");
            final JRadioButtonMenuItem noneItem = new JRadioButtonMenuItem("None", true);
            ActionListener regL = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    allItem.setSelected(false);
                    noneItem.setSelected(false);
                    intItem.setSelected(false);
                    if (e.getSource().equals(noneItem)) {
                        noneItem.setSelected(true);
                        psn.setRegisterDisplay(PanStampNode.RegisterDisplay.NONE);
                    }
                    if (e.getSource().equals(allItem)) {
                        allItem.setSelected(true);
                        psn.setRegisterDisplay(PanStampNode.RegisterDisplay.ALL);
                    }
                    if (e.getSource().equals(intItem)) {
                        intItem.setSelected(true);
                        psn.setRegisterDisplay(PanStampNode.RegisterDisplay.INTERESTING);
                    }
                }

            };

            allItem.addActionListener(regL);
            noneItem.addActionListener(regL);
            intItem.addActionListener(regL);

            regsMenu.add(allItem);
            regsMenu.add(intItem);
            regsMenu.add(noneItem);
        }
        else {
            regsMenu.setEnabled(false);
        }
        return regsMenu;
    }

    private JPopupMenu getEndpointPopupMenu(final EndpointNode epn) {
        JPopupMenu menu = popupMenus.get(epn);
        if (menu == null) {
            menu = new JPopupMenu(epn.toString());
            final JMenuItem graphItem = new JMenuItem("Data graph...");
            graphItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    view.showEndpointChart(epn.getEndpoint());
                }
            });
            menu.add(graphItem);
            popupMenus.put(epn, menu);
        }

        return menu;
    }

    private final View view;
    private final Map<NetworkTreeNode, JPopupMenu> popupMenus = new HashMap<>();

}
