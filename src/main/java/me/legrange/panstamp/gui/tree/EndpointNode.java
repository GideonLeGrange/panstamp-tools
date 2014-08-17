/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.legrange.panstamp.gui.tree;

import me.legrange.panstamp.Endpoint;

/**
 *
 * @author gideon
 */
public class EndpointNode extends SWAPNode {

    public EndpointNode(Endpoint ep) {
        super(ep);
    }

    @Override
    Type getType() {
        return Type.ENDPOINT;
    }
    
}
