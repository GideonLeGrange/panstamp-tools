package me.legrange.panstamp.gui;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;

/**
 * Handle the Apple extentions to isolate the imports from com.apple.eawt
 *
 * @since 1.0
 * @author Gideon le Grange https://github.com/GideonLeGrange
 */
class AppleExtension {

    static void apply(final MainWindow mw) {
        Application app = Application.getApplication();

        app.setAboutHandler(new AboutHandler() {

            @Override
            public void handleAbout(AppEvent.AboutEvent ae) {
                mw.showAbout();
            }
        });
        app.setQuitHandler(new QuitHandler() {

            @Override
            public void handleQuitRequestWith(AppEvent.QuitEvent qe, QuitResponse qr) {
                mw.quit();
            }
        });

        //           mw.panStampMenu.setVisible(false);
    }
}
