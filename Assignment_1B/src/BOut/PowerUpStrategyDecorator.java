package BOut;

public abstract class PowerUpStrategyDecorator implements PowerUpStrategy {

	private final PowerUpStrategy decorated;

	public PowerUpStrategyDecorator(PowerUpStrategy pu){
		decorated = pu;
	}
	
	public void activate(){
		decorated.activate();
	}
	
	public void lose(){
		decorated.lose();
	}

}
