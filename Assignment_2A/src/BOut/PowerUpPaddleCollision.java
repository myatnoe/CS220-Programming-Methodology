package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.object.collision.*;

/**
 * Handles collision of PowerUp with Paddle
 * 
 * @author Eliot Moss
  */
public class PowerUpPaddleCollision extends CollisionGroup
{
  /**
   * @param powerUp the colliding PowerUp
   * @param paddle the colliding paddle
   */
  public void collided (Sprite powerUp, Sprite paddle)
  {
    PowerUp thePowerUp = (PowerUp) powerUp;
    thePowerUp.collisionWithPaddle();
  }
}
