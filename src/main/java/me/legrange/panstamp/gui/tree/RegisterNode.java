/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.legrange.panstamp.gui.tree;

import me.legrange.panstamp.Register;

/**
 *
 * @author gideon
 */
public class RegisterNode  extends SWAPNode {

    public RegisterNode(Register reg) {
        super(reg);
    }
 @Override
    Type getType() {
        return Type.REGISTER;
    }   
}
