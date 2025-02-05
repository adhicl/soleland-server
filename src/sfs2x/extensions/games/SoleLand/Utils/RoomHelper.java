package sfs2x.extensions.games.SoleLand.Utils;

import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import sfs2x.extensions.games.SoleLand.SoleLandExtension;
import sfs2x.extensions.games.SoleLand.Model.World;


// Helper methods to easily get current room or zone and precache the link to ExtensionHelper
public class RoomHelper {

	public static Room getCurrentRoom(BaseClientRequestHandler handler) {
		return handler.getParentExtension().getParentRoom();
	}

	public static Room getCurrentRoom(SFSExtension extension) {
		return extension.getParentRoom();
	}

	public static World getWorld(BaseClientRequestHandler handler) {
		SoleLandExtension ext = (SoleLandExtension) handler.getParentExtension();
		return ext.getWorld();
	}

	public static World getWorld(BaseServerEventHandler handler) {
		SoleLandExtension ext = (SoleLandExtension) handler.getParentExtension();
		return ext.getWorld();
	}


}
