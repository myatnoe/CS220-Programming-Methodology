package BOut;

import java.awt.image.BufferedImage;
import static BOut.BreakOutEngine.*;

/**
 * Takes care of creating a PowerUP from a String description of it
 * @author Eliot Moss
  */
public class PowerUpFactory {
  
  /**
   * make it impossible to instantiate this class, which is here
   * only for its static method(s)
   */
  private PowerUpFactory () { }
  
  /**
   * creates a PowerUp from a description
   * @param desc a String giving the kind of PowerUp desired
   * @return a new PowerUP of that kind
   */
  public static PowerUp createPowerUp (String desc, BreakOutGame theGame, double x, double y)
  {
    BufferedImage image;
    if (desc.equals("incrOnHit"))
    {
      image = theGame.getImage(GraphicsDirectory + "PowerUpPurple.png"); 
      return new PowerUpIncrOnHit(image, x, y);
    }
    else if (desc.equals("decrOnMiss"))
    {
      image = theGame.getImage(GraphicsDirectory + "PowerUpCyan.png"); 
      return new PowerUpDecrOnMiss(image, x, y);
    }
    else if (desc.equals("incrDecr"))
    {
      image = theGame.getImage(GraphicsDirectory + "PowerUpBlue.png"); 
      return new PowerUpIncrDecr(image, x, y);
    }
    else
    {
      image = theGame.getImage(GraphicsDirectory + "PowerUpYellow.png"); 
      return new PowerUpPlain(image, x, y);
    }
  }

}
