package BOut;

/**
 * state values in common across the game;
 * some persist between levels, some are reset
 * @author Eliot Moss
 */
public class GameState
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
   * set the current score
   * @param num an int value for the new score
   */
  public void setScore (int num)
  {
    score = num;
  }

  /**
   * increments the score, taking current multiplier into account
   * @param incr the amount by which to increment (is multiplied by the current multiplier)
   */
  public void incrementScore (int incr)
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
   * call when a ball is fired, to note that there is now one active
   */
  public void noteBallActive ()
  {
    assert(ballsActive == 0);
    assert(ballsRemaining > 0);
    ++ballsActive;
    --ballsRemaining;
  }
  
  /**
   * call when a ball goes inactive
   */
  public void noteBallInactive ()
  {
    assert(ballsActive > 0);
    --ballsActive;
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
	
}
