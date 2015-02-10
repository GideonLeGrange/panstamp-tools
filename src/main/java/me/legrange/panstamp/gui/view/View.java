package me.legrange.panstamp.gui.view;

import me.legrange.panstamp.gui.model.Model;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.gui.MainWindow;
import me.legrange.panstamp.gui.PanStampParamDialog;
import me.legrange.panstamp.gui.PanStampSettingsDialog;
import me.legrange.panstamp.gui.NetworkAddDialog;

/**
 * Controlling the visual bits of the application
 *
 * @author gideon
 */
public class View {

    public void showNetworkAddDialog() {
        if ((networkAdd == null) ||  !networkAdd.isVisible()) {
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
        return treeMenus.getPopupMenu(path);
    }

    public View(MainWindow window, Model model) {
        this.model = model;
        this.window = window;
        this.treeRender = new NetworkTreeNodeRenderer(model, this);
        treeMenus = new TreeMenus(this);
    }

    public Model getModel() {
        return model;
    }

    void showEndpointChart(Endpoint endpoint) {
        ChartFactory.getFactory(getModel()).getEndpointChart(endpoint).setVisible(true);

    }

    void showSignalChart(PanStamp panStamp) {
        ChartFactory.getFactory(getModel()).getSignalChart(panStamp).setVisible(true);
    }
    private NetworkAddDialog networkAdd;
    private final TreeCellRenderer treeRender;
    private final Model model;
    private final MainWindow window;
    private final TreeMenus treeMenus;

}