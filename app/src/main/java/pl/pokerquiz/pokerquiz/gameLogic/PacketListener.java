package pl.pokerquiz.pokerquiz.gameLogic;

public interface PacketListener {
    public void onPacketRecived(String ipAddres, SocketPacket packet);
}
