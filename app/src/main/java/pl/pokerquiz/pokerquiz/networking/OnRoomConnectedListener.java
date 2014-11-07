package pl.pokerquiz.pokerquiz.networking;

public abstract class OnRoomConnectedListener {
    private PokerRoom mRoom;

    public OnRoomConnectedListener(PokerRoom room) {
        mRoom = room;
    }
    public PokerRoom getRoom() {
        return mRoom;
    }

    public abstract void onRoomConnected(boolean success, PokerRoom room);
}
