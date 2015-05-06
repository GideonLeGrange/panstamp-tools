package me.legrange.panstamp.gui.task;

/**
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public abstract class Task<T> {
    
    public final void start(final TaskRunner monitor) {
        this.monitor = monitor;
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    T res = Task.this.run();
                    monitor.completed(res);
                }
                catch (Throwable e) {
                    monitor.error(e);
                }
            }
            
        }, "Task thread");
        thread.setDaemon(true);
        running = true;
        thread.start();
    }
    
    public final void cancel() {
        running = false;
        thread.interrupt();
    }
    
    public final boolean isRunning() {
        return running;
    }
    
    protected final void update(int progress, String stage) {
        monitor.update(progress, stage);
    }
    
    protected abstract T run() throws Throwable;

    private TaskRunner monitor;
    private Thread thread; 
    protected boolean running;

}
