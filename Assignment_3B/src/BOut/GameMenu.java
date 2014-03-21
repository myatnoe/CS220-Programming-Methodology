package BOut;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import static BOut.BreakOutEngine.*;

/**
 * The menu screen. Currently just lets you select the level, but you could add high score tables, options, etc.
 * @author Paul Barba
 */
public class GameMenu extends GameObject {

	/**
	 * PlayField for the game choice menu screen;
	 */
	private PlayField field;

	/**
	 * Font used for the title of the game
	 */
	private GameFont titleFont;

	/**
	 * Font used for the name of the currently chosen level of the game 
	 */
	private GameFont choiceFont;

	// Remove File[] levelFiles, int levelIndex, File[] archiveFiles, archiveIndex
	// Instead use 2 FileSet objects
	FileSet levelFiles;
	FileSet archiveFiles;
	/**
	 * Sprite (roughly, "image") for moving forward in the menu list 
	 */
	private Sprite forward;

	/**
	 * Sprite (roughly, "image") for moving backward in the menu list 
	 */
	private Sprite backward;

	/**
	 * Sprite for playing the chosen level
	 */
	private Sprite play;

	/**
	 * Sprite for quitting entirely
	 */
	private Sprite quit;

	/**
	 * horiz position on which to center the level name
	 */
	private int nameCenter;

	/**
	 * vert position for the name
	 */
	private int nameYpos;

	/**
	 * vert position for the title
	 */
	private int titleYpos;

	/**
	 * Default constructor -- nothing special
	 * @param engine
	 */
	public GameMenu (GameEngine engine)
	{
		super(engine);
	}

	/**
	 * Prepares the "playing field" for rendering and receiving input actions 
	 */
	public void initResources () 
	{
		// Found in a search for 800x600 public domain images.
		Background background = createBackground();
		field = createPlayField(background);
		initializeFonts();
		// init level and archive
		levelFiles = new FileSet("levels", "Level\\d+\\.xml");
		archiveFiles = new FileSet(".", ".+\\.sav");
		

		int maxWidth = determineMaxNameWidth();
		int halfMaxWidth = (maxWidth + 1) / 2; // round up
		createAndPositionSprites(background, halfMaxWidth);
	}

	/**
	 * @return a suitable Background
	 */
	private Background createBackground () {
		Background bground = new ImageBackground(
				super.getImage(GraphicsDirectory + "MenuBackground.jpg"));
		return bground;
	}

	/**
	 * @param background the desired Background
	 * @return a new PlayField with that BackGround
	 */
	private PlayField createPlayField (Background background) {
		PlayField field = new PlayField();
		field.setBackground(background);
		return field;
	}

	/**
	 * sets up our chosen Font objects 
	 */
	private void initializeFonts() {
		// Fonts are used to write to the screen
		GameFontManager fontMgr = new GameFontManager();
		// This color is "half-strength" pure red
		titleFont = fontMgr.getFont(new Font("sansserif", Font.BOLD, 48), new Color(128, 0, 0));
		choiceFont = fontMgr.getFont(new Font("serif", Font.BOLD, 24), Color.BLACK);
	}

	/**
	 * @return an int giving the maximum width of a level name,
	 * in pixels, in our choice font
	 */
	private int determineMaxNameWidth () {
		int maxWidth = 0;
		for (File f : levelFiles)
		{
			String name = getDisplayFor(f.getName());
			int width = choiceFont.getWidth(name);
			if (maxWidth < width)
			{
				maxWidth = width;
			}
		}

		archiveFiles.refresh();
		for (File f : archiveFiles)
		{
			String name = getDisplayFor(f.getName());
			int width = choiceFont.getWidth(name);
			if (maxWidth < width)
			{
				maxWidth = width;
			}
		}

		return maxWidth;
	}


	/**
	 * creates Sprites for the buttons, positioning them using information
	 * from the arguments
	 * @param background the Background object (for determine width and height)
	 * @param halfMaxWidth an int giving 1/2 the max width of a level name when displayed
	 */
	private void createAndPositionSprites (Background background, int halfMaxWidth) {
		// approximate layout:
		//  <    level-name    >
		//    PLAY        QUIT

		titleYpos = background.getHeight() / 3;

		nameCenter = background.getWidth() / 2;
		int gap = 10; // to separate forward/backward from name

		play = new Sprite(getImage(GraphicsDirectory + "PlayButton.png"));
		field.add(play);
		int startYpos = background.getHeight() - 2*gap - play.getHeight();
		play.setLocation(nameCenter - play.getWidth() - gap, startYpos);

		quit = new Sprite(getImage(GraphicsDirectory + "QuitButton.png"));
		field.add(quit);
		quit.setLocation(nameCenter + gap, startYpos);

		forward = new Sprite(getImage(GraphicsDirectory + "RightButton.png"));
		field.add(forward);
		nameYpos = startYpos - gap - forward.getHeight();
		forward.setLocation(nameCenter+halfMaxWidth+gap, nameYpos);

		backward = new Sprite(getImage(GraphicsDirectory + "LeftButton.png"));
		field.add(backward);
		backward.setLocation(nameCenter-halfMaxWidth-gap-backward.getWidth(), nameYpos);
	}

	/**
	 * are we displaying levels, or archives?
	 */
	private enum Mode {
		Levels {
			State getChoosingState () { return State.ChoosingLevel; }
			State getQuittingState () { return State.QuittingLevel; }
			State getOppositeState () { return State.ChoosingArchive; }
			String getChoiceString (GameMenu menu) {
				if (menu.levelFiles.size() <= 0) { return ""; }
				return menu.levelFiles.currentDisplay();
			} 
			String getMenuString (GameMenu menu) { return "(Level)"; }
		},
		Archives {
			State getChoosingState () { return State.ChoosingArchive; }
			State getQuittingState () { return State.QuittingArchive; }
			State getOppositeState () { return State.ChoosingLevel; }
			String getChoiceString (GameMenu menu) {
				if (menu.archiveFiles.size() <= 0) { return ""; }
				return menu.archiveFiles.currentDisplay();
			}
			String getMenuString (GameMenu menu) { return "(Archive)"; }
		};

		// obtain the proper state for ordinary choosing in this mode
		abstract State getChoosingState ();

		// obtain the proper state for quitting in this mode
		abstract State getQuittingState ();

		// obtain the state for choosing in the opposite mode
		abstract State getOppositeState ();

		// obtain the appropriate String that might be displayed
		abstract String getChoiceString (GameMenu menu);

		// obtain the appropriate String that might be displayed above a menu item
		abstract String getMenuString (GameMenu menu);
	}

	/**
	 * the different possible input processing states
	 */
	private enum State
	{
		// presenting choices of levels to play
		ChoosingLevel(Mode.Levels) {
			State gotForward (GameMenu menu) {
				menu.levelFiles.advance(1);
				return this;
			}
			State gotBackward (GameMenu menu) {
				menu.levelFiles.advance(-1);
				
				return this;
			}
			State gotPlay (GameMenu menu) {
				if (menu.levelFiles.size() <= 0)
				{
					return this;  // no game to play
				}
				menu.parent.nextGameID = BreakOutEngine.BreakOutPlayGame;
				((BreakOutEngine)menu.parent).nextLevel = menu.levelFiles.current().getAbsolutePath();
				return Finish;
			}
		},

		// presenting choices of archives to restore
		ChoosingArchive(Mode.Archives) {
			State gotForward (GameMenu menu) {
				menu.archiveFiles.advance(1);
				return this;
			}
			State gotBackward (GameMenu menu) {
				menu.archiveFiles.advance(-1);
				return this;
			}
			State gotPlay (GameMenu menu) {
				if (menu.archiveFiles.size() <= 0)
				{
					return this;  // no archive to restore: ignore key
				}
				menu.parent.nextGameID = BreakOutEngine.BreakOutRestore;
				((BreakOutEngine)menu.parent).restoreFilename = menu.archiveFiles.current().getAbsolutePath();
				return Finish;
			}
			State gotDelete (GameMenu menu){
				if (menu.archiveFiles.size() <= 0) return this;
				return DeletingArchive;
			}
		},
		
		//Deleting Archive File
		DeletingArchive(Mode.Archives){
			State gotYes (GameMenu menu){
				menu.archiveFiles.current().getAbsoluteFile().delete();
				menu.archiveFiles.refresh();
				return ChoosingArchive;
			}
			State gotNo (GameMenu menu){ return ChoosingArchive; }
			String getMenuString (GameMenu menu) { return "Delete Archive? Y or N";}
		},

		// quit requested; awaiting confirmation
		QuittingLevel(Mode.Levels) {
			State gotYes (GameMenu menu) { 
				menu.parent.nextGame = null;
				return Finish;
			}
			String getMenuString (GameMenu menu) { return "Quit entirely?  Y or N"; }
		},

		// quit requested; awaiting confirmation
		QuittingArchive(Mode.Archives) {
			State gotYes (GameMenu menu) {
				menu.parent.nextGame = null;
				return Finish;
			}
			String getMenuString (GameMenu menu) { return "Quit entirely?  Y or N"; }
		},

		// quit has been confirmed; level vs archive does not matter
		Finish(Mode.Levels) {
			boolean shouldFinish (GameMenu menu) { return true; }
		};

		// is this a state that is handling levels, or archives?
		private final Mode mode;

		// constructor, which distinguishes level states versus archive states
		State (Mode mode) { this.mode = mode; }

		// are we in a state that should exit this "game"?
		boolean shouldFinish (GameMenu menu) { return false; }

		// respond to a Yes confirmation
		State gotYes (GameMenu menu) { return this; }

		// respond to a No confirmation
		State gotNo (GameMenu menu) { return mode.getChoosingState(); }

		// respond to a move-forward-in-the-list request
		State gotForward  (GameMenu menu) { return this; }

		// respond to a move-backward-in-the-list request
		State gotBackward (GameMenu menu) { return this; }

		// respond to a request to play the current choice
		State gotPlay (GameMenu menu) { return this; }

		// respond to a request to quit
		State gotQuit (GameMenu menu) { return mode.getQuittingState(); }

		// respond to a request to toggle between choosing a level or an archive
		State gotRestore (GameMenu menu) {
			State newState = mode.getOppositeState();
			return newState;
		}

		// get the current choice string to display
		String getChoiceString (GameMenu menu) { return mode.getChoiceString(menu); }

		// get the current string to display above the menu choice
		String getMenuString (GameMenu menu) { return mode.getMenuString(menu); }

		// get suitable starting state
		static State initial (Mode mode) { return mode.getChoosingState(); }

		// respond to a delete archive file
		State gotDelete(GameMenu gameMenu) { return this; }
	}

	/**
	 * the current input processing state
	 */
	private State state = State.initial(Mode.Levels);

	/**
	 * Called to update the screen.
	 * Here just checks for input events and updates information for
	 * rendering (or starts the chosen game)
	 * @param elaspedTime time since last update (not very relevant here)
	 */
	@Override
	public void update (long elapsedTime) 
	{
		field.update(elapsedTime);
		processInput();
		if (state.shouldFinish(this))
		{
			finish();
		}
	}

	private enum MyEvent {
		None,
		MoveForward,
		MoveBackward,
		No,
		Play,
		Quit,
		Restore,
		Yes,
		Delete;
	}

	/**
	 * Check for and process any input event 
	 */
	private void processInput () {
		switch (getEvent()) {
		case MoveForward:   state = state.gotForward (this);  break;
		case MoveBackward:  state = state.gotBackward(this);  break;
		case No:            state = state.gotNo      (this);  break;
		case Play:          state = state.gotPlay    (this);  break;
		case Quit:          state = state.gotQuit    (this);  break;
		case Restore:       state = state.gotRestore (this);  break;
		case Delete:        state = state.gotDelete  (this);  break;
		case Yes:           state = state.gotYes     (this);  break;
		case None:                                            break;
		}
	}

	/**
	 * Looks for keyboard or mouse input, converting to the
	 * common type MyEvent
	 * @return a MyEvent (None if nothing happened)
	 */
	private MyEvent getEvent () {
		MyEvent event = MyEvent.None;
		if (bsInput.isMousePressed(MouseEvent.BUTTON1))
			event = getMouseEvent();
		else
			event = getKeyEvent();
		return event;
	}

	/**
	 * Checks mouse position and determines appropriate event, if any
	 * @return the MyEvent for the current mouse position 
	 */
	private MyEvent getMouseEvent ()
	{
		if (mouseOver(forward))
			return MyEvent.MoveForward;
		else if (mouseOver(backward))
			return MyEvent.MoveBackward;
		else if (mouseOver(play))
			return MyEvent.Play;
		else if (mouseOver(quit))
			return MyEvent.Quit;
		else
			return MyEvent.None;
	}

	/**
	 * Return the MyEvent for the current key pressed, if any
	 * @return the MyEvent for the current key pressed (None if none)
	 */
	private MyEvent getKeyEvent ()
	{
		if (bsInput.isKeyPressed(KeyEvent.VK_RIGHT))
			return MyEvent.MoveForward;
		else if (bsInput.isKeyPressed(KeyEvent.VK_LEFT))
			return MyEvent.MoveBackward;
		else if (bsInput.isKeyPressed(KeyEvent.VK_ENTER))
			return MyEvent.Play;
		else if (bsInput.isKeyPressed(KeyEvent.VK_N))
			return MyEvent.No;
		else if (bsInput.isKeyPressed(KeyEvent.VK_Q))
			return MyEvent.Quit;
		else if (bsInput.isKeyPressed(KeyEvent.VK_R))
			return MyEvent.Restore;
		else if (bsInput.isKeyPressed(KeyEvent.VK_D))
			return MyEvent.Delete;
		else if (bsInput.isKeyPressed(KeyEvent.VK_Y))
			return MyEvent.Yes;
		else
			return MyEvent.None;
	}

	/**
	 * paint the menu screen
	 */
	public void render (Graphics2D g)
	{
		field.render(g);

		String title = "UMASS BREAKOUT";
		int titleWidth = titleFont.getWidth(title);
		titleFont.drawString(g, title, nameCenter-(titleWidth/2), titleYpos);

		String choice = state.getChoiceString(this);
		int choiceWidth = choiceFont.getWidth(choice);
		choiceFont.drawString(g, choice, nameCenter-(choiceWidth/2), nameYpos);

		String kind = state.getMenuString(this);
		int kindWidth = choiceFont.getWidth(kind);
		choiceFont.drawString(g, kind, nameCenter-(kindWidth/2), nameYpos - 10 - choiceFont.getHeight());
	}

	/**
	 * Tests whether the mouse is currently over a given sprite.
	 * Used for telling what to do on mouse presses.
	 * @param s the sprite
	 * @return true if the mouse is over the sprite
	 */
	private boolean mouseOver (Sprite s)
	{
		return (getMouseX() >= s.getX() && getMouseX() < (s.getX() + s.getWidth ()) &&
				getMouseY() >= s.getY() && getMouseY() < (s.getY() + s.getHeight()));
	}

	/**
	 * Converts from a filename to what we want on the screen
	 * @param filename a String giving the file name
	 * @return another String giving the display form of the file name;
	 * presently, we just trim off the dot and the file type
	 */
	private static String getDisplayFor (String filename)
	{
		// we trim off the file type
		return filename.substring(0, filename.indexOf('.'));
	}

}