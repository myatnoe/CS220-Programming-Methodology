package BOut;

/**
 * The strategy decorator that increments the score multiplier
 * if activated.
 * 
 * @author Eliot Moss
 *
 */
public final class PowerUpStrategyDecoratorIncrMini
extends PowerUpStrategyDecorator
{

	/**
	 * Constructor just passes strategy we are decorating to the superclass
	 * @param decorated PowerUpStrategy we are decorating
	 */
	PowerUpStrategyDecoratorIncrMini (PowerUpStrategy decorated)
	{
		super(decorated);
	}

	/* (non-Javadoc)
	 * @see BOut.PowerUpStrategyDecorator#activate()
	 */
	@Override
	public void activate () {
		super.activate();
		GameState.getGameState().increaseMiniBallCount();
	}

}
