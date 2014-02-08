package BOut;

import java.awt.image.BufferedImage;

/**
 * A PowerUp that increments the score multiplier if you get it
 * 
 * @author Eliot Moss
 */
public class PowerUpIncrOnHit extends PowerUp {

  /**
   * the sole constructor
   * @param image a BufferedImage to display
   * @param x a double giving the starting x position
   * @param y a double giving the starting y position
   */
  public PowerUpIncrOnHit (BufferedImage image, double x, double y) {
    super(image, x, y);
  }

  /**
   * action for this PowerUp is to increment the multiplier if gotten
   */
  public void activatePowerUp ()
  {
    GameState.getGameState().incrementMultiplier();
  }
  
  /**
   * lose PowerUp: do nothing
   */
  public void losePowerUp ()
  {
  }
  
}
