package me.legrange.panstamp.gui.task;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public interface Task extends Runnable {
    
    int getProgress();
    
    String getPhase();

}
