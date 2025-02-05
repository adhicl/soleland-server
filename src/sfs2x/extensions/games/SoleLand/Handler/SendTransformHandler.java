package sfs2x.extensions.games.SoleLand.Handler;

import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import sfs2x.extensions.games.SoleLand.Model.PlayerTransform;
import sfs2x.extensions.games.SoleLand.Utils.RoomHelper;
import sfs2x.extensions.games.SoleLand.Utils.UserHelper;

import java.util.Date;
import java.util.List;


public class SendTransformHandler extends BaseClientRequestHandler {
	
	@Override
    public void handleClientRequest(User u, ISFSObject data) {
        // The transform we received from player
        PlayerTransform receivedTransform = PlayerTransform.fromSFSObject(data);
        // Server accepted transform - send it to all the clients
        sendTransform(u, receivedTransform);    
    }

    // Send the transform to all the clients
    private void sendTransform(User fromUser, PlayerTransform resultTransform) {
        SFSObject sFSObject = new SFSObject();
        
        // Adding server timestamp to transform here
        long time = (new Date()).getTime();
        resultTransform.setTimeStamp(time);
        resultTransform.toSFSObject((ISFSObject)sFSObject);
        sFSObject.putInt("id", fromUser.getId());
        Room currentRoom = RoomHelper.getCurrentRoom(this);
        List<User> userList = UserHelper.getRecipientsList(currentRoom);
        send("transform", (ISFSObject)sFSObject, userList, true);    // Use UDP = true
    }

    
    /*
    // Sending rejected transform message to specified user
    private void sendRejectedTransform(User u) {
        SFSObject sFSObject = new SFSObject();
        RoomHelper.getWorld(this).getTransform(u).toSFSObject((ISFSObject)sFSObject);
        sFSObject.putInt("id", u.getId());
        send("notransform", (ISFSObject)sFSObject, u, true);    // Use UDP = true
    }
    //*/
}
