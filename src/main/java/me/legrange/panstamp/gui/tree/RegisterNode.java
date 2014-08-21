/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.legrange.panstamp.gui.tree;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.GatewayException;
import me.legrange.panstamp.Register;

/**
 *
 * @author gideon
 */
public class RegisterNode extends SWAPNode {

    public RegisterNode(Register reg) {
        super(reg);
    }
    
    public Register getRegister() {
        return (Register)getUserObject();
    }

    @Override
    protected void start() {
        try {
            for (Endpoint ep : getRegister().getEndpoints()) {
                EndpointNode epn = new EndpointNode(ep);
                addToTree(epn, this);
                epn.start();
            }
        } catch (GatewayException ex) {
            Logger.getLogger(RegisterNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    @Override
    Type getType() {
        return Type.REGISTER;
    }
}
