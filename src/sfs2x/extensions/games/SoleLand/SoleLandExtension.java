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
import sfs2x.extensions.games.SoleLand.Handler.GetTimeHandler;
import sfs2x.extensions.games.SoleLand.Model.Player;
import sfs2x.extensions.games.SoleLand.Model.World;
import sfs2x.extensions.games.SoleLand.Utils.RoomHelper;
import sfs2x.extensions.games.SoleLand.Utils.UserHelper;

public class SoleLandExtension extends SFSExtension
{
	private class TaskUpdate implements Runnable
    {
        private Instant lastSpawnMole;

		public TaskUpdate(World world){
			lastSpawnMole = Instant.now();
		}
         
        public void run()
        {
        	trace("schedule run "+gameStarted);
        	//if (!gameStarted) return;
        	try {
    			World world = getWorld();
    			
    			world.CheckPutDown();

    			Instant curInstant = Instant.now();
    			if (Duration.between(lastSpawnMole, curInstant).getSeconds() >= 3){
    				trace("SoleLand Game Extension spawn mole");
    				world.randomSpawnMole();
    				lastSpawnMole = curInstant;
    			}        		
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }
    }

	private World world;

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
	    this.world = new World(this);
		
	    addRequestHandler("sendTransform", SendTransformHandler.class);	
	    addRequestHandler("sendAnim", SendAnimHandler.class);
	    addRequestHandler("spawnMe", SpawnMeHandler.class);
	    addRequestHandler("hitMole", HitMoleHandler.class);
	    addRequestHandler("getTime", GetTimeHandler.class);
		
	    addEventHandler(SFSEventType.USER_DISCONNECT, OnUserGoneHandler.class);
	    addEventHandler(SFSEventType.USER_LEAVE_ROOM, OnUserGoneHandler.class);
	    addEventHandler(SFSEventType.USER_LOGOUT, OnUserGoneHandler.class);
		
		trace("SoleLand Game Extension launched");
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
	 * Start the game and send a notification to all users in the Room (both players and spectators).
	 */
	public void startGame()
	{
		trace("Start game ");
		
		//if (gameStarted)
		//	throw new IllegalStateException("Game is already started");
				
		if (!gameStarted) {
			world.spawnItems();

			SmartFoxServer sfs = SmartFoxServer.getInstance();
	        // Schedule the task to run every second, with no initial delay
	        taskHandle = sfs.getTaskScheduler().scheduleAtFixedRate(new TaskUpdate(getWorld()), 0, 1, TimeUnit.SECONDS);

		}
		
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
		trace("Stop game ");
		if (gameStarted) {
			
			trace("stop game "+result);
			
			gameStarted = false;
			
			taskHandle.cancel(true);

			SFSObject sFSObject = new SFSObject();
			sFSObject.putInt("result", result);
			
			Room currentRoom = RoomHelper.getCurrentRoom(this);
			List<User> userList = UserHelper.getRecipientsList(currentRoom);
			send("stopGame", (ISFSObject)sFSObject, userList);			
		}
	}
	  
	// Send message to clients when the score value of a player is updated
	public void sendSpawnMoleUp(int moleIndex) {

		trace("Send spawn mole ");
		
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
	    sFSObject.putInt("position", pl.getPosition());
	    Room currentRoom = RoomHelper.getCurrentRoom(this);
	    List<User> userList = UserHelper.getRecipientsList(currentRoom);
	    send("score", (ISFSObject)sFSObject, userList);
	}
}
