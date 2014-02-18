package BOut;

public class PowerUpStrategyDecoratorIncr extends PowerUpStrategyDecorator {

	public PowerUpStrategyDecoratorIncr(PowerUpStrategy pu) {
		super(pu);
	}

	@Override
	public void activate() {
		super.activate();
		GameState.getGameState().incrementMultiplier();
	}

	@Override
	public void lose() {
		super.lose();
	}


}
