package BOut;

/**
 * The IncrHit strategy:
 *   increment the score multiplier if hit
 * 
 * @author Eliot Moss
 */
public final class PowerUpStrategyIncrHit extends PowerUpStrategyAbstract {

  /* (non-Javadoc)
   * @see BOut.PowerUpStrategyAbstract#activate()
   */
  @Override
  public void activate () {
    GameState.getGameState().incrementMultiplier();
  }

}
