package me.legrange.panstamp.gui.view;

import javax.swing.JMenu;
import me.legrange.panstamp.gui.model.Model;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.gui.PanStampToolsGUI;
import me.legrange.panstamp.gui.PanStampParamDialog;
import me.legrange.panstamp.gui.PanStampSettingsDialog;
import me.legrange.panstamp.gui.NetworkAddDialog;
import me.legrange.panstamp.gui.SetValueDialog;

/**
 * Controlling the visual bits of the application
 *
 * @author gideon
 */
public class View {

    public void showNetworkAddDialog() {
        if ((networkAdd == null) || !networkAdd.isVisible()) {
            networkAdd = new NetworkAddDialog(null, model);
            networkAdd.setVisible(true);
        }
    }

    public void showPanStampSettingsDialog(PanStamp panStamp) {
        new PanStampSettingsDialog(null, model, panStamp).setVisible(true);
    }

    public void showPanStampParamDialog(PanStamp panStamp) {
        new PanStampParamDialog(null, model, panStamp).setVisible(true);

    }

    public TreeCellRenderer getTreeCellRenderer() {
        return treeRender;
    }

    public JPopupMenu getTreePopupMenu(TreePath path) {
        return menus.getPopupMenu(path);
    }

    public JMenu getGatewayMenu() {
        return menus.getNetworkMenu();
    }

    public JMenu getWorldMenu() {
        return menus.getWorldMenu();
    }

    public JMenu getDeviceMenu() {
        return menus.getPanStampMenu();
    }

    public JMenu getRegisterMenu() {
        return menus.getRegisterMenu();
    }

    public JMenu getEndpointMenu() {
        return menus.getEndpointMenu();
    }

    public View(PanStampToolsGUI window, Model model) {
        this.model = model;
        this.window = window;
        this.treeRender = new NetworkTreeNodeRenderer(model, this);

        menus = new Menus(this);
    }

    public Model getModel() {
        return model;
    }
    
    void showSetValueDialog(Endpoint ep) {
        SetValueDialog d = new SetValueDialog(window, ep);
        d.setVisible(true);
    }

    void showEndpointChart(Endpoint endpoint) {
        ChartFactory.getFactory(getModel()).getEndpointChart(endpoint).setVisible(true);

    }

    void showSignalChart(PanStamp panStamp) {
        ChartFactory.getFactory(getModel()).getSignalChart(panStamp).setVisible(true);
    }

    JTree getTree() {
        return window.getNetworkTree();
    }

    private NetworkAddDialog networkAdd;
    private final TreeCellRenderer treeRender;
    private final Model model;
    private final PanStampToolsGUI window;
    private final Menus menus;


}
