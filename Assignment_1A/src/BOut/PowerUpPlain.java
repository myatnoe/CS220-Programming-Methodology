package BOut;

import com.golden.gamedev.object.*;
import java.awt.image.*;

/**
 * A PowerUpPlain is a concrete PowerUP that does nothing
 * if hit or not hit.
 * 
 * @author Eliot Moss
 */
public class PowerUpPlain extends PowerUp
{	
  /**
   * Constructor that requires a String to describe what kind of block it is, with a BufferedImage
   * and a specified location. 
   * @param image
   * @param x
   * @param y
   */
  public PowerUpPlain (BufferedImage image, double x, double y)
  {
    super(image, x, y);
  }
	
  /**
   * Activates the PowerUp: do nothing
   */
  public void activatePowerUp ()
  {
  }

  /**
   * handles the case of <i>not</i> getting the PowerUp;
   * does nothing
   */
  public void losePowerUp ()
  {
  }
  
}
