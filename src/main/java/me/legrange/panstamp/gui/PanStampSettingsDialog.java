package me.legrange.panstamp.gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.gui.config.HexDocument;
import me.legrange.panstamp.gui.config.IntegerDocument;
import me.legrange.panstamp.gui.model.DataModel;

/**
 *
 * @author gideon
 */
public class PanStampSettingsDialog extends javax.swing.JDialog {

    /**
     * Creates new form PanStampSettingsDialog
     */
    public PanStampSettingsDialog(java.awt.Frame parent, DataModel model, PanStamp ps) {
        super(parent, false);
        this.ps = ps;
        this.model = model;
        initComponents();
    }

    private String getChannel() {
        try {
            return "" + ps.getConfig().getChannel();
        } catch (GatewayException ex) {
            Logger.getLogger(PanStampSettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<unknown>";
    }

    private String getSecurityOption() {
        try {
            return "" + ps.getConfig().getSecurityOption();
        } catch (GatewayException ex) {
            Logger.getLogger(PanStampSettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<unknown>";
    }
    
    
    private String getTxInterval() {
        try {
            return "" + ps.getConfig().getTxInterval();
        } catch (GatewayException ex) {
            Logger.getLogger(PanStampSettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<unknown>";
    }
        
    private String getNetwork() {
        try {
            return String.format("%4x", ps.getConfig().getNetwork());
        } catch (GatewayException ex) {
            Logger.getLogger(PanStampSettingsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<unknown>";
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        configTabs = new javax.swing.JTabbedPane();
        networkPanel = new javax.swing.JPanel();
        channelLabel = new javax.swing.JLabel();
        networkILabel = new javax.swing.JLabel();
        addressLabel = new javax.swing.JLabel();
        securityLabel = new javax.swing.JLabel();
        intervalLabel = new javax.swing.JLabel();
        channelField = new javax.swing.JTextField();
        networkField = new javax.swing.JTextField();
        addressField = new javax.swing.JTextField();
        securityField = new javax.swing.JTextField();
        intervalField = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(String.format("Mote %d - Settings", ps.getAddress()));
        setResizable(false);

        channelLabel.setText("Frequency channel:");

        networkILabel.setText("Network ID:");

        addressLabel.setText("Device address:");

        securityLabel.setText("Security option:");

        intervalLabel.setText("Periodic TX interval:");

        channelField.setColumns(4);
        channelField.setDocument(new IntegerDocument(0,255)
        );
        channelField.setText(String.format(getChannel()));

        networkField.setColumns(4);
        networkField.setDocument(new HexDocument(0,0xFFFe));
        networkField.setText(getNetwork());
        networkField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                networkFieldActionPerformed(evt);
            }
        });

        addressField.setColumns(4);
        addressField.setDocument(new IntegerDocument(0,255));
        addressField.setText(String.format("%d", ps.getAddress()));

        securityField.setColumns(4);
        securityField.setDocument(new IntegerDocument(0,255));
        securityField.setText(getSecurityOption());

        intervalField.setColumns(4);
        intervalField.setDocument(new IntegerDocument(0,65535));
        intervalField.setText(getTxInterval());

        javax.swing.GroupLayout networkPanelLayout = new javax.swing.GroupLayout(networkPanel);
        networkPanel.setLayout(networkPanelLayout);
        networkPanelLayout.setHorizontalGroup(
            networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(networkPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(networkPanelLayout.createSequentialGroup()
                        .addComponent(intervalLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(intervalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(networkPanelLayout.createSequentialGroup()
                        .addGroup(networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(channelLabel)
                            .addComponent(networkILabel)
                            .addComponent(addressLabel)
                            .addComponent(securityLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(securityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(networkField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(channelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(162, Short.MAX_VALUE))
        );
        networkPanelLayout.setVerticalGroup(
            networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(networkPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(channelLabel)
                    .addComponent(channelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(networkILabel)
                    .addComponent(networkField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addressLabel)
                    .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(securityLabel)
                    .addComponent(securityField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(networkPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intervalLabel)
                    .addComponent(intervalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        configTabs.addTab("Network Settings", networkPanel);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(configTabs)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(configTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void networkFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_networkFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_networkFieldActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        int addr = getIntValue(addressField);
        int net = Integer.parseInt(networkField.getText(), 16);
        int cha = getIntValue(channelField);
        int sec = getIntValue(securityField);
        int txi = getIntValue(intervalField);
        dispose();
        
    }//GEN-LAST:event_okButtonActionPerformed

    private int getIntValue(JTextField field) {
        return Integer.parseInt(field.getText());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField channelField;
    private javax.swing.JLabel channelLabel;
    private javax.swing.JTabbedPane configTabs;
    private javax.swing.JTextField intervalField;
    private javax.swing.JLabel intervalLabel;
    private javax.swing.JTextField networkField;
    private javax.swing.JLabel networkILabel;
    private javax.swing.JPanel networkPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField securityField;
    private javax.swing.JLabel securityLabel;
    // End of variables declaration//GEN-END:variables
    private final PanStamp ps;
    private final DataModel model;
}