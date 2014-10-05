package me.legrange.panstamp.gui.model;

import me.legrange.panstamp.Gateway;

/**
 *
 * @author gideon
 */
class WorldNode extends SWAPNode {

    public WorldNode() {
        super("");
    }

    @Override
    public String toString() { return ""; }
    
    @Override
    protected void start() {
    }


    

    @Override
    Type getType() {
        return Type.WORLD;
    }
       
    public synchronized void addGateway(Gateway gw) {
        GatewayNode gn = new GatewayNode(gw);
        addToTree(gn, this);
        gn.start();
    }
    
    
    @Override
    protected void addToTree(SWAPNode childNode, SWAPNode parentNode) {
            tm.addToTree(childNode, parentNode);
    }

    @Override
    protected void reload(SWAPNode childNode) {
        tm.reload(childNode);
    }
    
    void setModel(SWAPTreeModel tm) {
        this.tm = tm;
    }
   
    private  SWAPTreeModel tm;

    
}
