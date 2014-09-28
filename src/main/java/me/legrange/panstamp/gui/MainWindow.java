package me.legrange.panstamp.gui;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.gui.SWAPMessageModel.Direction;
import me.legrange.panstamp.gui.chart.SignalCollector;
import me.legrange.panstamp.gui.tree.SWAPNodeRenderer;
import me.legrange.panstamp.gui.tree.SWAPTreeModel;
import me.legrange.panstamp.impl.ModemException;
import me.legrange.swap.MessageListener;
import me.legrange.swap.ModemSetup;
import me.legrange.swap.SWAPException;
import me.legrange.swap.SwapMessage;

/**
 *
 * @author gideon
 */
public class MainWindow extends javax.swing.JFrame implements MessageListener, ConfigListener {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            if (isOSX) {
                System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                Application.getApplication().setEnabledPreferencesMenu(true);
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("System".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainWindow mw = new MainWindow();
                if (isOSX) {
                    Application.getApplication().addApplicationListener(new ApplicationAdapter() {

                        @Override
                        public void handleQuit(ApplicationEvent ae) {
                            ae.setHandled(true);
                            mw.quit();
                        }

                        @Override
                        public void handlePreferences(ApplicationEvent ae) {
                            ae.setHandled(true);
                            mw.showPrefs();
                        }

                        @Override
                        public void handleAbout(ApplicationEvent ae) {
                            ae.setHandled(true);
                            mw.showAbout();
                        }

                    });
                    mw.panStampMenu.setVisible(false);
                }
                mw.setVisible(true);
                mw.start();
            }
        });
    }

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        config = new Config();
        stm = SWAPTreeModel.create();
        etm = EndpointTableModel.create();
        smm = SWAPMessageModel.create();
        initComponents();
        setLocationRelativeTo(null);
    }

    @Override
    public void messageReceived(SwapMessage msg) {
        displaySWAPMessage(msg, Direction.IN);
    }

    @Override
    public void messageSent(SwapMessage msg) {
        displaySWAPMessage(msg, Direction.OUT);
    }

    @Override
    public void configUpdated(ConfigEvent ev) {
        switch (ev.getType()) {
            case NETWORK: {
                try {
                    gw.getSWAPModem().setSetup(new ModemSetup(config.getChannel(), config.getNetworkID(), config.getDeviceAddress()));
                } catch (SWAPException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case SERIAL:
                start();
                break;
        }

    }

    /**
     * start the application
     */
    private void start() {
        config.addListener(this);
        if (!config.hasValidPort()) {
            ConfigDialog cd = new ConfigDialog(config, this);
            cd.setVisible(true);
        }
        try {
            gw = Gateway.openSerial(config.getPortName(), config.getPortSpeed());
            stm.addGateway(gw);
            etm.addGateway(gw);
            // add listener to capture SWAP messages
            gw.getSWAPModem().addListener(this);
            gw.getSWAPModem().addListener(SignalCollector.getInstance());
        } catch (ModemException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void displaySWAPMessage(SwapMessage msg, Direction dir) {
        SWAPMessageModel mod = (SWAPMessageModel) swapMessagesTable.getModel();
        mod.add(msg, System.currentTimeMillis(), dir);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        leftRightSplitPane = new javax.swing.JSplitPane();
        topBottomSplitPane = new javax.swing.JSplitPane();
        topPanel = new javax.swing.JPanel();
        swapMessagesPane = new javax.swing.JScrollPane();
        swapMessagesTable = new javax.swing.JTable();
        bottomPanel = new javax.swing.JPanel();
        eventPanel = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();
        leftPanel = new javax.swing.JPanel();
        swapNetworkPane = new javax.swing.JScrollPane();
        networkTree = new javax.swing.JTree();
        mainMenu = new javax.swing.JMenuBar();
        panStampMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        configMenuItem = new javax.swing.JMenuItem();
        quitItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFont(new java.awt.Font("Courier", 0, 10)); // NOI18N
        setMinimumSize(new java.awt.Dimension(800, 600));

        leftRightSplitPane.setBorder(null);
        leftRightSplitPane.setDividerLocation(290);
        leftRightSplitPane.setDividerSize(0);

        topBottomSplitPane.setBorder(null);
        topBottomSplitPane.setDividerLocation(280);
        topBottomSplitPane.setDividerSize(0);
        topBottomSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        topPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "SWAP Messages", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13)), javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 4))); // NOI18N
        topPanel.setLayout(new java.awt.BorderLayout());

        swapMessagesTable.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        swapMessagesTable.setModel(smm);
        swapMessagesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        swapMessagesTable.getColumnModel().getColumn(0).setMinWidth(100);
        swapMessagesTable.getColumnModel().getColumn(0).setMaxWidth(100);
        swapMessagesTable.getColumnModel().getColumn(1).setPreferredWidth(61);
        swapMessagesTable.getColumnModel().getColumn(1).setMinWidth(61);
        swapMessagesTable.getColumnModel().getColumn(1).setMaxWidth(61);
        swapMessagesTable.getColumnModel().getColumn(2).setPreferredWidth(48);
        swapMessagesTable.getColumnModel().getColumn(2).setMinWidth(48);
        swapMessagesTable.getColumnModel().getColumn(2).setMaxWidth(48);
        swapMessagesTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        swapMessagesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        swapMessagesTable.setMinimumSize(new java.awt.Dimension(480, 250));
        swapMessagesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        swapMessagesTable.setShowGrid(false);
        swapMessagesTable.getTableHeader().setReorderingAllowed(false);
        swapMessagesTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                swapMessagesTablePropertyChange(evt);
            }
        });
        swapMessagesPane.setViewportView(swapMessagesTable);

        topPanel.add(swapMessagesPane, java.awt.BorderLayout.CENTER);

        topBottomSplitPane.setTopComponent(topPanel);

        bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Network Events"));
        bottomPanel.setLayout(new java.awt.BorderLayout());

        eventTable.setModel(etm);
        eventTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        eventTable.setMinimumSize(new java.awt.Dimension(480, 250));
        eventTable.getColumnModel().getColumn(0).setMinWidth(100);
        eventTable.getColumnModel().getColumn(0).setMaxWidth(100);
        eventPanel.setViewportView(eventTable);

        bottomPanel.add(eventPanel, java.awt.BorderLayout.CENTER);

        topBottomSplitPane.setRightComponent(bottomPanel);

        leftRightSplitPane.setRightComponent(topBottomSplitPane);

        leftPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder(null, "SWAP Network", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13)), javax.swing.BorderFactory.createEmptyBorder(2, 4, 2, 4))); // NOI18N
        leftPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        leftPanel.setLayout(new java.awt.BorderLayout());

        swapNetworkPane.setPreferredSize(null);

        networkTree.setModel(stm);
        networkTree.setCellRenderer(new SWAPNodeRenderer());
        networkTree.setSelectionModel(new DefaultTreeSelectionModel());
        networkTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                networkTreeMouseClicked(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                networkTreeMouseClicked(evt);
            }
        });
        swapNetworkPane.setViewportView(networkTree);

        leftPanel.add(swapNetworkPane, java.awt.BorderLayout.CENTER);

        leftRightSplitPane.setLeftComponent(leftPanel);

        panStampMenu.setText("panStamp");

        jMenuItem1.setText("About");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        panStampMenu.add(jMenuItem1);

        configMenuItem.setText("Preferences");
        configMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configMenuItemActionPerformed(evt);
            }
        });
        panStampMenu.add(configMenuItem);

        quitItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        quitItem.setText("Quit");
        quitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitItemActionPerformed(evt);
            }
        });
        panStampMenu.add(quitItem);

        mainMenu.add(panStampMenu);

        setJMenuBar(mainMenu);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(leftRightSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(leftRightSplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void quitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitItemActionPerformed
        quit();
    }//GEN-LAST:event_quitItemActionPerformed

    private void configMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configMenuItemActionPerformed
        showPrefs();
    }//GEN-LAST:event_configMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        showAbout();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void networkTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_networkTreeMouseClicked
        TreePath path = networkTree.getClosestPathForLocation(evt.getX(), evt.getY());
        SWAPNodeRenderer snr = (SWAPNodeRenderer) networkTree.getCellRenderer();
        JPopupMenu menu = snr.getPopupMenu(path);
        networkTree.setComponentPopupMenu(menu);
    }//GEN-LAST:event_networkTreeMouseClicked

    private void swapMessagesTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_swapMessagesTablePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_swapMessagesTablePropertyChange

    private void quit() {
        try {
            config.save();
            System.exit(0);
        } catch (BackingStoreException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void showPrefs() {
        ConfigDialog cd = new ConfigDialog(config, this);
        cd.setVisible(true);
    }

    private void showAbout() {
        AboutDialog ad = new AboutDialog(this, true);
        ad.setVisible(true);
    }

    private final Config config;
    private Gateway gw;
    private final SWAPTreeModel stm;
    private final EndpointTableModel etm;
    private final SWAPMessageModel smm;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JMenuItem configMenuItem;
    private javax.swing.JScrollPane eventPanel;
    private javax.swing.JTable eventTable;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JSplitPane leftRightSplitPane;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JTree networkTree;
    private javax.swing.JMenu panStampMenu;
    private javax.swing.JMenuItem quitItem;
    private javax.swing.JScrollPane swapMessagesPane;
    private javax.swing.JTable swapMessagesTable;
    private javax.swing.JScrollPane swapNetworkPane;
    private javax.swing.JSplitPane topBottomSplitPane;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
    static {
      isOSX = System.getProperty("os.name", "").trim().equals("Mac OS X");
    }
    private static final boolean isOSX;

}
