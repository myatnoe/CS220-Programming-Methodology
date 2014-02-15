package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.object.collision.*;

/**
 * Logic for when a Ball hits a Block
 * 
 * @author Eliot Moss
 */
public class BallBlockCollision extends CollisionGroup 
{

  /**
   * @param ball the Ball that is colliding
   * @param block the Block that is colliding
   */
  public void collided (Sprite ball, Sprite block) 
  {
    Block theBlock = (Block) block;
    Ball theBall = (Ball) ball;
    // tell the block it was hit by a Ball
    theBlock.collisionWithBall();
    // compute side of the Block that the Ball hit
    // (determines how Ball bounces)
    boolean onBottom = false;
    boolean onTop    = false;
    boolean onLeft   = false;
    boolean onRight  = false;
    // need to take direction into account to avoid double collisions
    // (because of how Golden T computes collisions)
    if        (((getCollisionSide() & BOTTOM_TOP_COLLISION) != 0) && (theBall.getVerticalSpeed() > 0))
    {
      onBottom = true;
    } else if (((getCollisionSide() & TOP_BOTTOM_COLLISION) != 0) && (theBall.getVerticalSpeed() < 0))
    {
      onTop = true;
    } else if (((getCollisionSide() & LEFT_RIGHT_COLLISION) != 0) && (theBall.getHorizontalSpeed() < 0))
    {
      onLeft = true;
    } else if (((getCollisionSide() & RIGHT_LEFT_COLLISION) != 0) && (theBall.getHorizontalSpeed() > 0))
    {
      onRight = true;
    }
    // tell the Ball about its collision
    theBall.collisionWithBlock(onTop, onBottom, onLeft, onRight);
  }

}
