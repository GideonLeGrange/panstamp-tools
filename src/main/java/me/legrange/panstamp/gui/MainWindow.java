package me.legrange.panstamp.gui;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import me.legrange.panstamp.Gateway;
import me.legrange.panstamp.gui.SWAPMessageModel.Direction;
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
        config.addListener(this);
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
        swapMessagesLabel = new javax.swing.JLabel();
        swapMessagesPane = new javax.swing.JScrollPane();
        swapMessagesTable = new javax.swing.JTable();
        bottomPanel = new javax.swing.JPanel();
        eventPanel = new javax.swing.JScrollPane();
        eventTable = new javax.swing.JTable();
        eventLabel = new javax.swing.JLabel();
        leftPanel = new javax.swing.JPanel();
        swapNetworkLabel = new javax.swing.JLabel();
        swapNetworkPane = new javax.swing.JScrollPane();
        networkTree = new javax.swing.JTree();
        mainMenu = new javax.swing.JMenuBar();
        panStampMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        configMenuItem = new javax.swing.JMenuItem();
        quitItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFont(new java.awt.Font("Courier", 0, 10)); // NOI18N

        leftRightSplitPane.setBorder(null);
        leftRightSplitPane.setDividerLocation(220);
        leftRightSplitPane.setDividerSize(4);

        topBottomSplitPane.setBorder(null);
        topBottomSplitPane.setDividerLocation(340);
        topBottomSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        swapMessagesLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        swapMessagesLabel.setText("SWAP Messages");

        swapMessagesTable.setFont(new java.awt.Font("Courier", 0, 12)); // NOI18N
        swapMessagesTable.setModel(smm);
        swapMessagesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        swapMessagesTable.getColumnModel().getColumn(1).setPreferredWidth(40);
        swapMessagesTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        swapMessagesTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        swapMessagesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        swapMessagesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        swapMessagesTable.setShowGrid(false);
        swapMessagesTable.getTableHeader().setReorderingAllowed(false);
        swapMessagesTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                swapMessagesTablePropertyChange(evt);
            }
        });
        swapMessagesPane.setViewportView(swapMessagesTable);

        org.jdesktop.layout.GroupLayout topPanelLayout = new org.jdesktop.layout.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(topPanelLayout.createSequentialGroup()
                .add(swapMessagesLabel)
                .add(0, 0, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(swapMessagesPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(topPanelLayout.createSequentialGroup()
                .add(swapMessagesLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(swapMessagesPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 296, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        topBottomSplitPane.setTopComponent(topPanel);

        eventTable.setModel(etm);
        eventPanel.setViewportView(eventTable);

        eventLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        eventLabel.setText("Network Events");

        org.jdesktop.layout.GroupLayout bottomPanelLayout = new org.jdesktop.layout.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(bottomPanelLayout.createSequentialGroup()
                        .add(eventPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(bottomPanelLayout.createSequentialGroup()
                        .add(eventLabel)
                        .add(0, 0, Short.MAX_VALUE))))
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(eventLabel)
                .add(10, 10, 10)
                .add(eventPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        topBottomSplitPane.setRightComponent(bottomPanel);

        leftRightSplitPane.setRightComponent(topBottomSplitPane);

        swapNetworkLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        swapNetworkLabel.setText("SWAP Network");

        networkTree.setCellRenderer(new SWAPNodeRenderer());
        swapNetworkPane.setViewportView(networkTree);

        org.jdesktop.layout.GroupLayout leftPanelLayout = new org.jdesktop.layout.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(leftPanelLayout.createSequentialGroup()
                .add(swapNetworkLabel)
                .add(0, 123, Short.MAX_VALUE))
            .add(leftPanelLayout.createSequentialGroup()
                .add(swapNetworkPane)
                .addContainerGap())
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(leftPanelLayout.createSequentialGroup()
                .add(swapNetworkLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(swapNetworkPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))
        );

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
                .add(leftRightSplitPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(leftRightSplitPane)
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

    private void swapMessagesTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_swapMessagesTablePropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_swapMessagesTablePropertyChange

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        showAbout();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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
    private javax.swing.JLabel eventLabel;
    private javax.swing.JScrollPane eventPanel;
    private javax.swing.JTable eventTable;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JSplitPane leftRightSplitPane;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JTree networkTree;
    private javax.swing.JMenu panStampMenu;
    private javax.swing.JMenuItem quitItem;
    private javax.swing.JLabel swapMessagesLabel;
    private javax.swing.JScrollPane swapMessagesPane;
    private javax.swing.JTable swapMessagesTable;
    private javax.swing.JLabel swapNetworkLabel;
    private javax.swing.JScrollPane swapNetworkPane;
    private javax.swing.JSplitPane topBottomSplitPane;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
    static {
        isOSX = System.getProperty("os.name", "").trim().equals("Mac OS X");
    }
    private static final boolean isOSX;

}
