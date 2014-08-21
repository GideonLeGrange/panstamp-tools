/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.legrange.panstamp.gui.tree;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.PanStamp;
import me.legrange.panstamp.Register;

/**
 *
 * @author gideon
 */
public class PanStampNode extends SWAPNode {

    public PanStampNode(PanStamp ps) {
        super(ps);
    }
    
    public PanStamp getPanStamp() { 
        return (PanStamp)getUserObject();
    }

    @Override
    protected void start() {
        try {
            List<Register> regs = getPanStamp().getRegisters();
            for (Register reg : regs) {
                RegisterNode rn = new RegisterNode(reg);
                addToTree(rn, this);
                rn.start();
            }
        } catch (GatewayException ex) {
            Logger.getLogger(PanStampNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @Override
    Type getType() {
        return Type.PANSTAMP;
    }
    
    
    
}
