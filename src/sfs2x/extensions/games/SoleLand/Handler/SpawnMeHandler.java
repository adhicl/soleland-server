package sfs2x.extensions.games.SoleLand.Handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import sfs2x.extensions.games.SoleLand.SoleLandExtension;
import sfs2x.extensions.games.SoleLand.Model.Player;
import sfs2x.extensions.games.SoleLand.Model.World;
import sfs2x.extensions.games.SoleLand.Utils.RoomHelper;

public class SpawnMeHandler extends BaseClientRequestHandler {
	
	@Override
    public void handleClientRequest(User u, ISFSObject data) {
        World world = RoomHelper.getWorld(this);
        boolean newPlayer = world.addOrRespawnPlayer(u);
        if (newPlayer)
            // Send this player data about all the other players
            sendOtherPlayersInfo(u);         
    }
	  
    // Send the data for all the other players to the newly joined client
    private void sendOtherPlayersInfo(User targetUser) {
        World world = RoomHelper.getWorld(this);
        for (Player player : world.getPlayers()) {            
            if (player.getSfsUser().getId() != targetUser.getId()) {
                SoleLandExtension ext = (SoleLandExtension)getParentExtension();
                ext.clientInstantiatePlayer(player, targetUser);
            } 
        } 
    }
    
}
