package sfs2x.extensions.games.SoleLand.Handler;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import sfs2x.extensions.games.SoleLand.Model.World;
import sfs2x.extensions.games.SoleLand.Utils.RoomHelper;

public class OnUserGoneHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		User user = (User) event.getParameter(SFSEventParam.USER);
		World world = RoomHelper.getWorld(this);
		world.userLeft(user);
	}

}
