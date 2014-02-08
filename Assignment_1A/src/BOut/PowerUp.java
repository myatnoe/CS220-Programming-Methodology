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
public abstract class PowerUp extends Sprite
{	

  /**
   * audio object for playing sounds
   */
  private BaseAudio audio;
    
  /**
   * file with sound for when a PowerUp falls off the bottom of the screen
   */
  private String PowerUpLoseSound = SoundsDirectory + "bang_1.wav";
  
  /*
   * file with sound for when a PowerUp hits the paddle
   */
  private String PowerUpHitSound = SoundsDirectory + "cling_1.wav";
  
  /**
   * Constructor that requires a String to describe what kind of block it is, with a BufferedImage
   * and a specified location. 
   * @param image
   * @param x
   * @param y
   */
  protected PowerUp (BufferedImage image, double x, double y)
  {
    super(image, x, y);
  }
	
  /**
   * Activates the PowerUp (is special action) and makes its Sprite inactive.
   */
  public final void collisionWithPaddle ()
  {
    activatePowerUp();
    playHitSound();
    this.setActive(false);
  }
	
  /**
   * Handles the case of activating PowerUp
   */
  public abstract void activatePowerUp ();

  /**
   * Handles the collision with the bottom of the screen;
   * performs the losePowerUp special action and makes its
   * Sprite inactive
   */
  public final void collisionWithBounds ()
  {		
    losePowerUp();
    playLoseSound();
    this.setActive(false); // fell off the bottom
  }

  /**
   * handles the case of <i>not</i> getting the PowerUp
   */
  public abstract void losePowerUp ();
  
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
