package BOut;

import java.awt.image.BufferedImage;

/**
 * A PowerUp that decrements the score multiplier if you get it
 * @author Eliot Moss
 */
public class PowerUpDecrOnMiss extends PowerUp {

  /**
   * the sole constructor
   * @param image a Buffered image to display
   * @param x a double giving the starting x position
   * @param y a double giving the starting y position
   */
  public PowerUpDecrOnMiss (BufferedImage image, double x, double y) {
    super(image, x, y);
  }

  /**
   * activate: do nothing
   */
  public void activatePowerUp ()
  {
  }

  /**
   * action for this PowerUp is to decrement the multiplier if missed
   */
  public void losePowerUp ()
  {
    GameState.getGameState().decrementMultiplier();
  }
  
}
