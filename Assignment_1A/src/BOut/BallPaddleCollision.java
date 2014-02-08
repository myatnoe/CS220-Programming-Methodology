package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.object.collision.*;

/**
 * Handles collision of Ball with Paddle
 * 
 * @author Eliot Moss
 */
public class BallPaddleCollision extends CollisionGroup
{

  /**
   * Handles collision of ball with paddle
   * @param ball the Ball that is colliding
   * @param paddle the Paddle that is colliding
   */
  public void collided (Sprite ball, Sprite paddle)
  {
    Paddle thePaddle = (Paddle) paddle;
    Ball theBall = (Ball) ball;

    // tell the Ball of the collision (the Paddle doesn't care)
    theBall.collisionWithPaddle(thePaddle);		
  }

}
