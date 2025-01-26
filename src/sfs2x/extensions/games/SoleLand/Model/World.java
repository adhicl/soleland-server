package sfs2x.extensions.games.SoleLand.Model;

import com.smartfoxserver.v2.entities.User;

import sfs2x.extensions.games.SoleLand.SoleLandExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

//The main World server model. Contains players, items, and all the other needed world objects
public class World {
	private static Random rnd = new Random();	  
	
	private SoleLandExtension extension;   // Reference to the server extension
	
	private List<Player> players = new ArrayList<>();  	// Players
	
	private Mole[] moles = new Mole[16];  	// Moles
	
	public World(SoleLandExtension extension) {
		this.extension = extension;
		rnd.setSeed((new Date()).getTime());
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	// Add new player if he doesn't exist
	public boolean addOrRespawnPlayer(User user) {
		if (players.size() >= 2) return false;

		Player player = getPlayer(user);
		if (player == null) {
			player = new Player(user, players.size());
			this.players.add(player);
			this.extension.clientInstantiatePlayer(player);
			return true;
		}
		
		return false;
	}
	
	public PlayerTransform getTransform(User u) {
		Player player = getPlayer(u);
		return player.getTransform();
	}
	
	// Gets the player corresponding to the specified SFS user
	private Player getPlayer(User u) {
		for (Player player : this.players) {
			if (player.getSfsUser().getId() == u.getId())
			return player; 
		} 
		return null;
	}
	
	// When user lefts the room or disconnects - removing him from the players list 
	public void userLeft(User user) {
		Player player = getPlayer(user);
		if (player == null)
			return; 
		this.players.remove(player);
	}
	  
	// Spawning new items
	public void spawnItems() {
		for (int i = 0; i < 16; i++){
			moles[i] = new Mole();
		}
	}

	private int previousRandom;
	public void randomSpawnMole(){
		int randomIndex = rnd.nextInt(16);
		while (previousRandom == randomIndex){
			randomIndex = rnd.nextInt(16);
		}
		moles[randomIndex].ShowUp();
	}

	public void CheckPutDown(){
		for (int i = 0; i < 16; i++){
			moles[i].CheckPutDown();
		}
	}
	
	// Process the shot from client
	public void processHitMole(User fromUser, int moleHit) {
		Player player = getPlayer(fromUser);
		player.IncreaseScore();
	}
}
