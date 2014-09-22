/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.legrange.panstamp.gui.tree;

import me.legrange.panstamp.Endpoint;
import me.legrange.panstamp.EndpointEvent;
import me.legrange.panstamp.EndpointListener;

/**
 *
 * @author gideon
 */
public class EndpointNode extends SWAPNode implements EndpointListener {

    public EndpointNode(Endpoint ep) {
        super(ep);
    }
    
    public Endpoint getEndpoint() {
        return (Endpoint)getUserObject();
    }

    @Override
    protected void start() {
        getEndpoint().addListener(this);
    }
    
    @Override
    public void endpointUpdated(EndpointEvent ev) {
        reload();
    }

    @Override
    Type getType() {
        return Type.ENDPOINT;
    }

    
}
