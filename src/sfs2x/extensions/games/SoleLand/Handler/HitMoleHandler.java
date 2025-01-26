package sfs2x.extensions.games.SoleLand.Handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import sfs2x.extensions.games.SoleLand.Utils.RoomHelper;

public class HitMoleHandler extends BaseClientRequestHandler {
	
    @Override
    public void handleClientRequest(User u, ISFSObject data) {
        int moleHit = data.getInt("target").intValue();
        RoomHelper.getWorld(this).processHitMole(u, moleHit);
    }
}
