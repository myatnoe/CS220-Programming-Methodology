package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.engine.*;
import java.awt.image.*;
import static BOut.BreakOutEngine.*;

/**
 * A PowerUp is a Sprite that drops when a Block is destroyed.
 * It causes some special action in the game if hit with the
 * paddle (or if you fail to). At present, PowerUp objects
 * do not interact with Ball objects.
 * 
 * @author Eliot Moss
 */
public final class PowerUp extends Sprite
{	

  /**
   * audio object for playing sounds
   */
  private BaseAudio audio;
    
  /**
   * file with sound for when a PowerUp falls off the bottom of the screen
   */
  private static final String PowerUpLoseSound = SoundsDirectory + "bang_1.wav";
  
  /**
   * file with sound for when a PowerUp hits the paddle
   */
  private static final String PowerUpHitSound = SoundsDirectory + "cling_1.wav";
  
  /**
   * strategy object for PowerUp's varying behavior 
   */
  private final PowerUpStrategy strategy;
  
  /**
   * Constructor that requires a String to describe what kind of block it is, with a BufferedImage
   * and a specified location. 
   * @param image
   * @param x
   * @param y
   */
  protected PowerUp (BufferedImage image, double x, double y, PowerUpStrategy strategy)
  {
    super(image, x, y);
    this.strategy = strategy;
  }
	
  /**
   * Activates the PowerUp (is special action) and makes its Sprite inactive.
   */
  public final void collisionWithPaddle ()
  {
    strategy.activate();
    playHitSound();
    this.setActive(false);
  }
	
  /**
   * Handles the collision with the bottom of the screen;
   * performs the losePowerUp special action and makes its
   * Sprite inactive
   */
  public final void collisionWithBounds ()
  {		
    strategy.lose();
    playLoseSound();
    this.setActive(false); // fell off the bottom
  }

  /**
   * set the object for playing sounds
   * @param audio the BaseAudio for playing sounds
   */
  public void setAudio (BaseAudio audio)
  {
    this.audio = audio;
  }
  
  /**
   * plays the sound that indicates the PowerUp was lost
   */
  protected void playLoseSound ()
  {
    if (audio != null)
    {
      audio.play(PowerUpLoseSound);
    }
  }
  
  /**
   * plays the sound that indicates the PowerUp was hit (won)
   */
  protected void playHitSound ()
  {
    if (audio != null)
    {
      audio.play(PowerUpHitSound);
    }
  }
  
}
