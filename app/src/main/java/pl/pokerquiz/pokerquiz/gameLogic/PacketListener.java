package pl.pokerquiz.pokerquiz.gameLogic;

public interface PacketListener {
    public void onPacketRecived(String ipAddres, String messageType, String message);
}
