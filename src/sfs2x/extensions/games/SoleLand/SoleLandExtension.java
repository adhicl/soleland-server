package sfs2x.extensions.games.SoleLand;


import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

import sfs2x.extensions.games.SoleLand.Handler.HitMoleHandler;
import sfs2x.extensions.games.SoleLand.Handler.OnUserGoneHandler;
import sfs2x.extensions.games.SoleLand.Handler.SendAnimHandler;
import sfs2x.extensions.games.SoleLand.Handler.SendTransformHandler;
import sfs2x.extensions.games.SoleLand.Handler.SpawnMeHandler;
import sfs2x.extensions.games.SoleLand.Model.Player;
import sfs2x.extensions.games.SoleLand.Model.World;
import sfs2x.extensions.games.SoleLand.Utils.RoomHelper;
import sfs2x.extensions.games.SoleLand.Utils.UserHelper;

public class SoleLandExtension extends SFSExtension
{
	private class TaskUpdate implements Runnable
    {
        private Instant lastSpawnMole;
		private World world;

		public TaskUpdate(World world){
			this.world = world;
		}
         
        public void run()
        {
			world.CheckPutDown();

			Instant curInstant = Instant.now();
			if (Duration.between(lastSpawnMole, curInstant).getSeconds() >= 3){
				world.randomSpawnMole();
				lastSpawnMole = curInstant;
			}
        }
    }

	private World world;
	private User whoseTurn;
	private volatile boolean gameStarted;
	ScheduledFuture<?> taskHandle;
	
	public World getWorld() {  
		return world;
	}

	/**
	 * Initialize the Extension; set initial state and add request and event listeners.
	 */
	@Override
	public void init()
	{
		trace("SoleLand Game Extension launched");
		
	    addRequestHandler("sendTransform", SendTransformHandler.class);	
	    addRequestHandler("sendAnim", SendAnimHandler.class);
	    addRequestHandler("spawnMe", SpawnMeHandler.class);
	    addRequestHandler("hitMole", HitMoleHandler.class);
		
	    addEventHandler(SFSEventType.USER_DISCONNECT, OnUserGoneHandler.class);
	    addEventHandler(SFSEventType.USER_LEAVE_ROOM, OnUserGoneHandler.class);
	    addEventHandler(SFSEventType.USER_LOGOUT, OnUserGoneHandler.class);

		SmartFoxServer sfs = SmartFoxServer.getInstance();         
        // Schedule the task to run every second, with no initial delay
        taskHandle = sfs.getTaskScheduler().scheduleAtFixedRate(new TaskUpdate(getWorld()), 0, 1, TimeUnit.SECONDS);
	}
	
	/**
	 * Destroy the Extension.
	 */
	@Override
	public void destroy() 
	{
		this.world = null;
		super.destroy();		
		trace("SoleLand Game Extension destroyed");
	}
	  
	// Send instantiate new player message to all the clients
	public void clientInstantiatePlayer(Player player) {
		clientInstantiatePlayer(player, (User)null);
	}
	  
	//Send the player instantiation message to all the clients or to a specified user only
	public void clientInstantiatePlayer(Player player, User targetUser) {
		SFSObject sFSObject = new SFSObject();
		player.toSFSObject((ISFSObject)sFSObject);
		Room currentRoom = RoomHelper.getCurrentRoom(this);
		if (targetUser == null) {
			List<User> userList = UserHelper.getRecipientsList(currentRoom);
			// Sending to all the users
			send("spawnPlayer", (ISFSObject)sFSObject, userList);
		} else {
			// Sending to the specified user
			send("spawnPlayer", (ISFSObject)sFSObject, targetUser);
		} 
	}
	
	/**
	 * Return game running state, accessed by request/event handlers.
	 */
	public boolean isGameStarted()
	{
		return gameStarted;
	}
	
	/**
	 * Return active player, accessed by request/event handlers.
	 */
	public User getWhoseTurn()
    {
	    return whoseTurn;
    }
	
	/**
	 * Start the game and send a notification to all users in the Room (both players and spectators).
	 */
	public void startGame()
	{
		if (gameStarted)
			throw new IllegalStateException("Game is already started");
		
		// Reset state
		gameStarted = true;

		SFSObject sFSObject = new SFSObject();

		Room currentRoom = RoomHelper.getCurrentRoom(this);
		List<User> userList = UserHelper.getRecipientsList(currentRoom);
		send("startGame", (ISFSObject)sFSObject, userList);
	}
	
	/**
	 * Stop the game if its ending was signaled (tie or win or user lost).
	 */
	public void stopGame(int result)
	{
		gameStarted = false;

		SFSObject sFSObject = new SFSObject();
		sFSObject.putInt("result", result);
		
		Room currentRoom = RoomHelper.getCurrentRoom(this);
		List<User> userList = UserHelper.getRecipientsList(currentRoom);
		send("stopGame", (ISFSObject)sFSObject, userList);
	}
	  
	// Send message to clients when the score value of a player is updated
	public void sendSpawnMoleUp(int moleIndex) {
		SFSObject sFSObject = new SFSObject();
		sFSObject.putInt("index", moleIndex);

		Room currentRoom = RoomHelper.getCurrentRoom(this);
		List<User> userList = UserHelper.getRecipientsList(currentRoom);
		send("moleSpawn", (ISFSObject)sFSObject, userList);
	}
	  
	// Send message to clients when the score value of a player is updated
	public void updatePlayerScore(Player pl) {
	    SFSObject sFSObject = new SFSObject();
	    sFSObject.putInt("id", pl.getSfsUser().getId());
	    sFSObject.putInt("score", pl.getScore());
	    Room currentRoom = RoomHelper.getCurrentRoom(this);
	    List<User> userList = UserHelper.getRecipientsList(currentRoom);
	    send("score", (ISFSObject)sFSObject, userList);
	}
}
