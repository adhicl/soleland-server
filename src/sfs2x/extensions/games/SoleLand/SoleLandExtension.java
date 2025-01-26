package sfs2x.extensions.games.SoleLand;


import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.SFSExtension;

public class SoleLandExtension extends SFSExtension
{
	private User whoseTurn;
	private volatile boolean gameStarted;
	
	/**
	 * Initialize the Extension; set initial state and add request and event listeners.
	 */
	@Override
	public void init()
	{
		trace("SoleLand Game Extension launched");		
		
	}
	
	/**
	 * Destroy the Extension.
	 */
	@Override
	public void destroy() 
	{
		super.destroy();
		
		trace("SoleLand Game Extension destroyed");
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
	}
	
	/**
	 * Stop the game if its ending was signaled (tie or win or user lost).
	 */
	public void stopGame(boolean resetPlayers)
	{
		gameStarted = false;
	}
	
	/**
	 * Update game state (moves count and turn) and check if the game ended. 
	 */
	public void updateGameState()
	{
	}
	
	/**
	 * Check if the game is over based on the board state.
	 * If game is over, send outcome to all users in the Room (players and spectators).
	 */
	private void checkGameOver()
	{
	}
}
