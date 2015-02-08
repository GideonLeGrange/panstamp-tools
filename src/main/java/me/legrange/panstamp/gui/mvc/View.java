package me.legrange.panstamp.gui.mvc;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.gui.network.NetworkAddDialog;

/**
 * Controlling the visual bits of the application
 *
 * @author gideon
 */
public class View {

    public void showNetworkAddDialog() {
        if (networkAdd == null) {
            if (!networkAdd.isVisible()) {
                networkAdd = new NetworkAddDialog(null, model);
                networkAdd.setVisible(true);
            }
        }
    }
    
    public TreeCellRenderer getTreeCellRenderer() {
        return treeRender;
    }

    public JPopupMenu getTreePopupMenu(TreePath path) {
        return treeRender.getPopupMenu(path);
    }

    View(DataModel model) {
        this.model = model;
    }
        
    private NetworkAddDialog networkAdd;
    private TreeCellRenderer treeRender;
    private final DataModel model;
}
