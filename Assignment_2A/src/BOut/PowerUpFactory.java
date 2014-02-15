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
   * Strategy object for do-nothing behavior
   */
  private static final PowerUpStrategy plain = new PowerUpStrategyPlain();
  
  /**
   * Strategy object for increment-score-multiplier when hit behavior
   */
  private static final PowerUpStrategy incr = new PowerUpStrategyIncrHit();
  
  /**
   * Strategy object for decrement-score-multipler when lost behavior
   */
  private static final PowerUpStrategy decr = new PowerUpStrategyDecrLose();
  
  /**
   * Strategy object for increment-when-hit/decrement-when-lost behavior
   */
  private static final PowerUpStrategy incrDecr = new PowerUpStrategyIncrDecr();
  
  /**
   * creates a PowerUp from a description
   * @param desc a String giving the kind of PowerUp desired
   * @return a new PowerUP of that kind
   */
  public static PowerUp createPowerUp (String desc, BreakOutGame theGame, double x, double y)
  {
    BufferedImage image;
    PowerUpStrategy strategy = plain;
    if (desc.equals("incrOnHit"))
    {
      image = theGame.getImage(GraphicsDirectory + "PowerUpPurple.png"); 
      strategy = incr;
    }
    else if (desc.equals("decrOnMiss"))
    {
      image = theGame.getImage(GraphicsDirectory + "PowerUpCyan.png"); 
      strategy = decr;
    }
    else if (desc.equals("incrDecr"))
    {
      image = theGame.getImage(GraphicsDirectory + "PowerUpBlue.png"); 
      strategy  = incrDecr;
    }
    else
    {
      image = theGame.getImage(GraphicsDirectory + "PowerUpYellow.png"); 
    }
    return new PowerUp(image, x, y, strategy);
  }

}
