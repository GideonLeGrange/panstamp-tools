package me.legrange.panstamp.gui.task;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public interface TaskRunner<T> {
    
    void completed(T result);
    
    void error(Throwable e);
    
    void update(int progress, String stage);
    

}
