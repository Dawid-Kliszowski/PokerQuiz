package pl.pokerquiz.pokerquiz;

import android.app.Application;

import com.google.common.eventbus.EventBus;

import pl.pokerquiz.pokerquiz.gameLogic.GamerManager;

public class PokerQuizApplication extends Application {
    private static final EventBus EVENT_BUS = new EventBus();

    private static GamerManager sGamerManager;

    private static void setGamerManager(GamerManager manager) {
        sGamerManager = manager;
    }
    private static GamerManager getGamerManager() {
        return sGamerManager;
    }
    public static EventBus getEventBus() {
        return EVENT_BUS;
    }
}
