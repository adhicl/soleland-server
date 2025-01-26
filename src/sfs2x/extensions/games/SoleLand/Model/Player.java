package sfs2x.extensions.games.SoleLand.Model;

import javax.xml.crypto.dsig.Transform;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class Player {
    private PlayerTransform transform;
    private int score;
    private int position;
    private User sfsUser;
	  
    public User getSfsUser() {
        return this.sfsUser;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public int getPosition() {
        return this.position;
    }

    public PlayerTransform getTransform() {
	    return this.transform;
    }

    public Player(User sfsUser, int position) {
	    this.sfsUser = sfsUser;
        this.position = position;
	    this.transform = PlayerTransform.GetSpawnPosition(position);
    }
	  
    public void toSFSObject(ISFSObject data) {
        SFSObject sFSObject = new SFSObject();
        sFSObject.putInt("id", this.sfsUser.getId());
        sFSObject.putInt("score", this.score);
        sFSObject.putInt("position", this.position);
        this.transform.toSFSObject((ISFSObject)sFSObject);
        data.putSFSObject("player", (ISFSObject)sFSObject);
    }

    public void IncreaseScore(){
        this.score++;
    }

}
