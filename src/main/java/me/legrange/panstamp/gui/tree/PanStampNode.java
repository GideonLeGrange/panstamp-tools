/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.legrange.panstamp.gui.tree;

import me.legrange.panstamp.PanStamp;

/**
 *
 * @author gideon
 */
public class PanStampNode extends SWAPNode {

    public PanStampNode(PanStamp ps) {
        super(ps);
    }
    @Override
    Type getType() {
        return Type.PANSTAMP;
    }
    
    
    
}
