package pl.pokerquiz.pokerquiz.networking;

import pl.pokerquiz.pokerquiz.datamodel.GamerInfo;

public interface CroupierInteractingInterface {
    public void onGamerConnected(GamerInfo gamerInfo, AcceptingManager acceptManager);
}
