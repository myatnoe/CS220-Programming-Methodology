package BOut;

import java.awt.image.BufferedImage;

/**
 * PowerUp that increments the score multiplier if hit, and
 * decrement the multiplier it not hit
 * 
 * @author Eliot
 */
public class PowerUpIncrDecr extends PowerUp {

  /**
   * the sole constructor
   * @param image a BufferedImage for displaying the PowerUP
   * @param x a double giving the starting x position
   * @param y a double giving the starting y position
   */
  public PowerUpIncrDecr (BufferedImage image, double x, double y) {
    super(image, x, y);
  }

  /**
   * increments multiplier if gotten
   */
  public void activatePowerUp ()
  {
    GameState.getGameState().incrementMultiplier();
  }
  
  /**
   * decrements multiplier if lost
   */
  public void losePowerUp ()
  {
    GameState.getGameState().decrementMultiplier();
  }
  
}
