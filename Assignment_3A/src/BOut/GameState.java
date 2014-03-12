package BOut;

import java.io.Serializable;

/**
 * state values in common across the game;
 * some persist between levels, some are reset
 * @author Eliot Moss
 */
public class GameState implements Cloneable, Serializable
{
	/**
	 * the score
	 */
	private int score;

	/**
	 * score multiplier, for additions to the score
	 */
	private int scoreMultiplier;

	/**
	 * base value for the score multiplier
	 */
	private static final int baseMultiplier = 1;

	/**
	 * puts a bound on the multiplier value
	 */
	private static final int maxMultiplier = 3;

	/**
	 * current ball speed (magnitude) in pixels per frame
	 */
	private double ballSpeed;

	/**
	 * base speed of the ball
	 */
	private static final double baseSpeed = 0.35D;

	/**
	 * number of balls remaining on the level
	 */
	private int ballsRemaining;

	/**
	 * number of balls active on the screen (0 or 1 for our design)
	 */
	private int ballsActive;

	/**
	 * usual starting value for number of balls
	 */
	private static int baseBallsRemaining = 3;

	/**
	 * number of blocks remaining on the level
	 */
	private int blocksRemaining;

	/**
	 * current number of active PowerUps on the screen
	 */
	private int powerUpsActive;

	/**
	 * we follow a singleton model: a class with one instance,
	 * which anyone can get here; NOTE: This must go after
	 * other static declarations, so they are available
	 * when this runs!!
	 */
	private static GameState GAMESTATE = new GameState();

	/**
	 * accessor to get the singleton GameState
	 * @return the one GameState object
	 */
	public static GameState getGameState ()
	{
		return GAMESTATE;
	}

	/**
	 * accessor to set the singleton GameState,
	 * used only when restoring a saved game
	 * @param gs the GameState to install
	 */
	protected static void setGameState (GameState gs)
	{
		GAMESTATE = gs;
	}

	/**
	 * constructor is private to support the singleton model
	 */
	private GameState ()
	{
		// one-time initializations happen here;
		// see startLevel for those needed for every level
		score = 0;
	}

	/**
	 * method called when we are starting a level
	 */
	public void startLevel ()
	{
		ballSpeed = baseSpeed;
		scoreMultiplier = baseMultiplier;
		ballsRemaining = baseBallsRemaining;
		ballsActive = 0;
		blocksRemaining = 0;
		powerUpsActive = 0;
		connectEventSources();
	}

	/**
	 * called when we are resuming from a saved snapshot
	 */
	public void restart ()
	{
		connectEventSources();
	}

	/**
	 * form connections for ActiveChangedEvents
	 */
	private void connectEventSources ()
	{
		PowerUp.getActiveChangedSource().register(
				new BreakoutListener<ActiveChangedEvent<PowerUp>>()
				{
					public void happened (ActiveChangedEvent<PowerUp> event)
					{
						activeChanged(event.getSprite(), event.getDelta());
					}
				}
		);
		Ball.getActiveChangedSource().register(
				new BreakoutListener<ActiveChangedEvent<Ball>>()
				{
					public void happened (ActiveChangedEvent<Ball> event)
					{
						activeChanged(event.getSprite(), event.getDelta());
					}
				}
		);
		Block.getActiveChangedSource().register(
				new BreakoutListener<ActiveChangedEvent<Block>>()
				{
					public void happened (ActiveChangedEvent<Block> event)
					{
						activeChanged(event.getSprite(), event.getDelta());
					}
				}
		);
	}

	/**
	 * getter for score
	 * @return the current score, an int
	 */
	public int getScore ()
	{
		return score;
	}

	/**
	 * increments the score, taking current multiplier into account
	 * @param incr the amount by which to increment (is multiplied by the current multiplier)
	 */
	private void incrementScore (int incr)
	{
		score += incr * scoreMultiplier;
	}

	/**
	 * getter for the current score multiplier
	 * @return an int giving the current multiplier
	 */
	public int getMultiplier ()
	{
		return scoreMultiplier;
	}

	/**
	 * increment the score multiplier (but don't exceed maxMultiplier)
	 */
	public void incrementMultiplier ()
	{
		if (scoreMultiplier < maxMultiplier)
			scoreMultiplier++;
	}

	/**
	 * decrement the score multiplier (but don't go below 1)
	 */
	public void decrementMultiplier ()
	{
		if (scoreMultiplier > 1)
			scoreMultiplier--;
	}

	/**
	 * indicates whether it is legal to fire a ball now
	 * @return true iff balls remain and none are active
	 */
	public boolean canFireBall ()
	{
		return (ballsRemaining > 0) & (ballsActive == 0);
	}

	/**
	 * getter for the current ball speed
	 * @return a double giving the ball speed in pixels per millisecond
	 */
	public double getBallSpeed ()
	{
		return ballSpeed;
	}

	/**
	 * setter for the ball speed
	 * @param num a double giving the desired ball speed in pixels per millisecond
	 */
	public void setBallSpeed (double num)
	{
		ballSpeed = num;
	}

	/**
	 * gets the default ball speed
	 * @return a double giving the default ball speed in pixels per millisecond
	 */
	public double getBaseSpeed ()
	{
		return baseSpeed;
	}

	/**
	 * getter for the number of balls remaining on this level
	 * @return an int giving the number of balls remaining on this level
	 */
	public int getBallsRemaining ()
	{
		return ballsRemaining;
	}

	/**
	 * tests whether we are done (we have have won or we may not have)
	 * @return a boolean indicating whether the level is over
	 */
	public boolean levelDone ()
	{
		// not done until (a) no powerUpsActive, AND
		// (b) no blocks left OR no balls left
		return (powerUpsActive + blocksRemaining * (ballsActive + ballsRemaining)) == 0;
	}

	/**
	 * tests whether we won the level; meaningful only if levelDone
	 * returns true
	 * @return a boolean indicating whether we won the level
	 */
	public boolean wonLevel ()
	{
		return (blocksRemaining == 0);
	}

	/**
	 * called when a powerUp changes to/from being active
	 * @param powerUp the PowerUp that changed
	 * @param delta an int indicating the net change in number of active PowerUps
	 */
	private void activeChanged (PowerUp powerUp, int delta)
	{
		powerUpsActive += delta;
	}

	/**
	 * called when a ball changes to/from being active
	 * @param ball the ball that changed
	 * @param delta an int indicating the net change in number of active Balls
	 */
	private void activeChanged (Ball ball, int delta)
	{
		ballsActive += delta;
		if (delta > 0)
		{
			ballsRemaining -= delta;
		}
	}

	/**
	 * called when a blockUp changes to/from being active
	 * @param block the block that changed
	 * @param delta an int indicating the net change in number of active Blocks
	 */
	private void activeChanged (Block block, int delta)
	{
		blocksRemaining += delta;
		if (delta < 0)
		{
			incrementScore(block.getValue());
		}
	}

	/**
	 * @return a memento (copy) of this GameState
	 */
	public GameState memento ()
	{
		try
		{
			return (GameState)this.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			System.err.printf("Problem cloning a GameState:%n%s", exc);
			return null;
		}
	}


}
