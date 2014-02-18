package BOut;

public class PowerUpStrategyDecoratorDecr extends PowerUpStrategyDecorator {

	public PowerUpStrategyDecoratorDecr(PowerUpStrategy pu) {
		super(pu);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void activate() {
		super.activate();
	}

	@Override
	public void lose() {
		super.lose();
		GameState.getGameState().decrementMultiplier();
	}


}
