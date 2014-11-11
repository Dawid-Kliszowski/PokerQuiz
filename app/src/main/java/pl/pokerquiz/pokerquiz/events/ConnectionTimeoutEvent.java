package pl.pokerquiz.pokerquiz.events;

public class ConnectionTimeoutEvent {
    private String mIpAddress;

    public ConnectionTimeoutEvent(String ipAddress) {
        mIpAddress = ipAddress;
    }
}
