package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.engine.*;
import static BOut.BreakOutEngine.*;

import java.awt.image.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Block extends Sprite
{	
  /**
   * default score value of a block
   */
  private static int defaultValue = 50;
  
  /**
   * the point value of the block; should not be negative
   */
  private int value = defaultValue;
	
  /**
   * horiz position of the (center of) the block
   */
  private double xPos;
  
  /**
   * vert position of the (center of) the block
   */
  private double yPos;
  
  /**
   * we need to know the game we're part of for when we
   * create a power-up
   */
  private BreakOutGame ourGame;
  
  /**
   * default number of times the block needs to be hit to destroy it
   */
  private static final int defaultHitsNeeded = 1;
  
  /**
   * number of times still needs to be hit to destroy block
   */
  private int hitsNeeded = defaultHitsNeeded;
  
  /**
   * the powerups that we may drop
   */
  private List<String> powerups = new LinkedList<String>();
	
  /**
   * the probability for each powerup
   */
  private List<Float> probabilities = new LinkedList<Float>();
  
  /**
   * random number stream for determining powerups
   */
  private static Random dice = new Random(); //if we decide to use random numbers elsewhere, they should probably
	//all be unified into 1 class.

  /**
   * for blocks to play a sound when hit
   */
  private BaseAudio audio;
	
  /**
   * a specific sound for when a block is destroyed
   */
  private static final String BlockDestroySound = SoundsDirectory + "bang_1.wav";
	
  /**
   * event source for hearing about changes in status of Blocks 
   */
  private static BreakoutEventSource<ActiveChangedEvent<Block>> activitySource =
    new BreakoutEventSource<ActiveChangedEvent<Block>>();
  
  /**
   * getter of event source for changes in activity status of Block
   * @return the current BreakoutEventSource<ActiveChangedEvent<Block>>
   */
  public static BreakoutEventSource<ActiveChangedEvent<Block>> getActiveChangedSource ()
  {
    return activitySource;
  }
  
  /**
   * should be called as we start a new game, so we can get ready
   */
  public static void newGame () {
    activitySource.reset();
  }
  
  /**
   * Empty Constructor
   */
  public Block ()
  {
    super();
    notifyActivityChanged();
  }

  /**
   * Constructor that takes in a BufferedImage and sets a location.
   * @param image a BufferedImage for displaying the block
   * @param x a double giving the x position of the block
   * @param y a double giving the y position of the block
   */
  public Block (BufferedImage image, double x, double y)
  {
    super(image, x, y);
    xPos = x;
    yPos = y;
    notifyActivityChanged();
  }
	
  /**
   * Called when there is a collision with a ball.  It sets the Block to inactive, and sets the score
   * in the Gamestate.
   */
  public void collisionWithBall ()
  {
    --hitsNeeded;
    if (hitsNeeded <= 0) {
      this.setActive(false);
      if (audio != null)
      {
        audio.play(BlockDestroySound);
      }
      String kind = this.getPowerup();
      if (kind != null)
      {
        ourGame.dropPowerUp(getXVal(), getYVal(), kind);
      }
    }
  }
	
  /**
   * Returns the score that should be awarded on destroying this block
   * @return the score value of the block
   */
  public int getValue ()
  {
    return value;
  }
	
  /**
   * Sets the block's score value
   * @param v an int giving the score value of the block
   */
  public void setValue (int v)
  {
    value = v;
  }
	
  /**
   * Returns the X position of the Block.
   * @return a double giving the x position of the block
   */
  public double getXVal()
  {
    return xPos;
  }
	
  /**
   * Returns the Y position of the Block
   * @return a double giving the y position of the block
   */
  public double getYVal ()
  {
    return yPos;
  }
	
  /**
   * Set the BaseAudio to use for playing sounds
   * @param audio a BaseAudio for playing sounds
   */
  public void setAudio (BaseAudio audio)
  {
    this.audio = audio;
  }
	
  /**
   * Sets the BreakOutGame variable for when the class needs to reference the level.
   * @param game the BreakOutGame to use for powerups, etc.
   */
  public void setGame (BreakOutGame game)
  {
    ourGame = game;
  }
  
  /**
   * Adds a certain type of powerup to this block and sets the probability it will drop.
   * @param powerup a Powerup to add as a possibility; note, we drop at most one Powerup
   * @param probability a float giving its probability 
   */
  public void addPowerup (String kind, float probability)
  {
    powerups.add(kind);
    probabilities.add(probability);
  }
  
  /**
   * Call once on block destruction. Returns a random powerup, or null, based on the block settings.
   * @return a String giving the kind of chosen according to the probabilities (may be null, for none)
   */
  private String getPowerup ()
  {
    // if(powerups.size() == 0) return null;
    float percentile = dice.nextFloat();
    int idx = 0;
    for (float prob : probabilities)
    {
      percentile -= prob;
      if (percentile < 0.0f)
      {
        return powerups.get(idx);
      }
      ++idx;
    }
    return null;
  }

  /**
   * wraps activity changes so that we can notify listeners
   * @param newValue a boolean giving the new value for whether the Block is active
   */
  public void setActive (boolean newValue)
  {
    boolean changed = (this.isActive() ^ newValue);
    super.setActive(newValue);
    if (changed)
    {
      notifyActivityChanged();
    }
  }
  
  /**
   * use to notify activity change listeners (if any)
   */
  private void notifyActivityChanged ()
  {
    if (activitySource != null && activitySource.anyListeners())
    {
      activitySource.notify(new ActiveChangedEvent<Block>(this));
    }
  }

}
