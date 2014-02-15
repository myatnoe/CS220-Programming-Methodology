package BOut;

/**
 * The IncrDecr strategy:
 *   increment the score multiplier if hit;
 *   decrement the score multiplier if lost
 * 
 * @author Eliot Moss
 */
public final class PowerUpStrategyIncrDecr extends PowerUpStrategyAbstract {

  /* (non-Javadoc)
   * @see BOut.PowerUpStrategyAbstract#activate()
   */
  @Override
  public void activate () {
    GameState.getGameState().incrementMultiplier();
  }

  /* (non-Javadoc)
   * @see BOut.PowerUpStrategyAbstract#lose()
   */
  @Override
  public void lose () {
    GameState.getGameState().decrementMultiplier();
  }

}
