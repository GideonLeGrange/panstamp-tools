package me.legrange.panstamp.gui;

/**
 * Handle the Apple extentions to isolate the imports from com.apple.eawt
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
class AppleExtension {

    static void apply(final MainWindow mw) throws NoSuchMethodException {    
        OSXAdapter.setAboutHandler(mw, mw.getClass().getMethod("showAbout", new Class[]{}));
        OSXAdapter.setQuitHandler(mw, mw.getClass().getMethod("quit", new Class[]{}));
    }
    
}
