package BOut;


import com.golden.gamedev.engine.*;
import com.golden.gamedev.object.*;
import static BOut.BreakOutEngine.*;

public class MiniBall extends Sprite implements Cloneable
{

	/**
	 * audio channel for the mini ball's sounds to play
	 */
	private transient BaseAudio audio;
	/**
	 * the sound to make when mini ball hit the block
	 */
	private static String HitBlockSound = SoundsDirectory + "ringout.wav";
	
	/**
	 * the number of time it already hit the block
	 */
	private int hitBlockCount;

	/**
	 * maximum count it can mark to destroy the block
	 */
	public int maxHitBlockCount;
	
	/**
	 * event source for hearing about changes in status of Mini Balls
	 */
	private static BreakoutEventSource<ActiveChangedEvent<MiniBall>> activitySource =
		new BreakoutEventSource<ActiveChangedEvent<MiniBall>>();

	/**
	 * getter of event source for changes in activity status of Mini Ball
	 * @return the current BreakoutEventSource<ActiveChangedEvent<MiniBall>>
	 */
	public static BreakoutEventSource<ActiveChangedEvent<MiniBall>> getActiveChangedSource ()
	{
		return activitySource;
	}

	/**
	 * should be called as we start a new game, so we can get ready
	 */
	public static void newGame () {
		activitySource.reset();
	}

	/**
	 * Takes in the position of the mini Ball, without an image provided. with default maximum hit block count 3.
	 * @param x
	 * @param y
	 */
	public MiniBall (double x, double y)
	{
		super(x, y);
		hitBlockCount = 0;
		this.maxHitBlockCount = 3;
		notifyActivityChanged();
	}
	/**
	 * 
	 * @param x
	 * @param y
	 * @param maximum block that can hit.
	 */
	public MiniBall(double x, double y, int maxHitCount){
		super(x, y);
		hitBlockCount = 0;
		this.maxHitBlockCount = maxHitCount;
		notifyActivityChanged();
	}
	
	/**
	 * increase how many block the miniball already hit
	 */
	public void increaseHitBlockCount(){
		hitBlockCount++;
	}
	
	/**
	 * getter for whether miniball still can hit the block.
	 */
	public boolean canHitBlock(){
		return hitBlockCount < maxHitBlockCount;
	}

	/**
	 * Set the velocity (speed and direction) of the Ball
	 * @param magnitude a double giving the magnitude of the speed;
	 * units are pixels per millisecond
	 * @param angle a double giving the angle in radians; take note
	 * that the convention is the 0 is straight up, and positive
	 * is counter-clockwise
	 */
	public void setVelocityPolar (double magnitude, double angle) {
		this.setHorizontalSpeed(-(Math.sin(angle)*magnitude));
		this.setVerticalSpeed  (-(Math.cos(angle)*magnitude));
	}

	/**
	 * Must be set for any sounds to play.  If null, no sounds will be made when the ball
	 * collides with something.
	 * @param ba
	 */
	public void setAudio (BaseAudio ba)
	{
		this.audio = ba;
	}

	/**
	 * This method is called if there is a collision with the boundary,
	 * and is given 4 booleans depending on the side that
	 * this ball hits.  If it hits a corner, it would be given two trues.
	 * @param onTop a boolean, true if the collision is on the top of the Ball
	 * @param onBottom a boolean, true if the collision is on the bottom of the Ball
	 * @param onLeft a boolean, true if the collision is on the left of the Ball
	 * @param onRight a boolean, true if the collision is on the right of the Ball
	 */
	public void collisionWithBounds ()
	{
		setActive(false);
	}

	/**
	 * For collisions with Blocks and other objects, takes in parameters to tell if the ball
	 * collides with something on its top, bottom,, left, or right with something, and
	 * the speed of that something (pixels per second; walls and blocks have speed 0,
	 * but the paddle may have a different speed).
	 *
	 * @param onTop a boolean, true if the collision is with the top of the ball
	 * @param onBottom a boolean, true if the collision is with the bottom of the ball
	 * @param onLeft a boolean, true if the collision is with the left of the ball
	 * @param onRight a boolean, true if the collision is with the right of the ball
	 */
	public void collisionWithBlock ()
	{
		// play the sound on collision with block
		if (HitBlockSound != null && audio != null)
	    {
	      audio.play(HitBlockSound);
	    }
		
	}

	/**
	 * wraps activity changes so that we can notify listeners
	 * @param newValue a boolean giving the new value for whether the Ball is active
	 */
	public void setActive (boolean newValue)
	{
		boolean changed = (this.isActive() ^ newValue);
		super.setActive(newValue);
		if (changed)
		{
			notifyActivityChanged();
		}
	}

	/**
	 * use to notify activity change listeners (if any)
	 */
	private void notifyActivityChanged ()
	{
		if (activitySource != null && activitySource.anyListeners())
		{
			activitySource.notify(new ActiveChangedEvent<MiniBall>(this));
		}
	}

	/**
	 * @return a memento (copy) of this MiniBall
	 */
	public MiniBall memento ()
	{
		try
		{
			return (MiniBall)this.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			System.err.printf("Problem cloning a MiniBall:%n%s", exc);
			return null;
		}
	}

}
