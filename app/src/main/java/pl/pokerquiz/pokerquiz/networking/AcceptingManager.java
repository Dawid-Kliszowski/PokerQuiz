package pl.pokerquiz.pokerquiz.networking;

public class AcceptingManager {
    private boolean mIsUsed;
    private OnAcceptListener mAcceptListener;
    private OnTimeoutListener mTimeoutListener;

    public AcceptingManager(OnAcceptListener acceptListener) {
        mAcceptListener = acceptListener;
    }

    public void setTimeoutListener(OnTimeoutListener listener) {
        mTimeoutListener = listener;
    }

    public boolean setAccept(boolean accept) {
        if (!mIsUsed) {
            mIsUsed = true;
            mAcceptListener.onAccept(accept);
        }
        return false;
    }

    void onTimeout() {
        if (!mIsUsed) {
            mIsUsed = true;
            if (mTimeoutListener != null) {
                mTimeoutListener.onTimeout();
            }
        }
    }
}
