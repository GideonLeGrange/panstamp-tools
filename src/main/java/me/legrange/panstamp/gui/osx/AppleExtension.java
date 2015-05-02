 package me.legrange.panstamp.gui.osx;

import me.legrange.panstamp.gui.MainWindow;

/**
 * Handle the Apple extentions to isolate the imports from com.apple.eawt
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
public class AppleExtension {

   public static void apply(final MainWindow mw) throws NoSuchMethodException {    
        OSXAdapter.setAboutHandler(mw, mw.getClass().getMethod("showAbout", new Class[]{}));
        OSXAdapter.setQuitHandler(mw, mw.getClass().getMethod("quit", new Class[]{}));
        OSXAdapter.setPreferencesHandler(mw, mw.getClass().getMethod("showPreferences", new Class[]{}));
    }
    
   public static boolean isOSX() {
       return isOSX;
   }

   static {
        isOSX = System.getProperty("os.name", "").trim().equals("Mac OS X");
    }
   
    private static final boolean isOSX;
}
