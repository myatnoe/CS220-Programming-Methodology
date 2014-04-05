package BOut;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.golden.gamedev.*;
import com.golden.gamedev.engine.*;
import com.golden.gamedev.object.*;

import static org.easymock.classextension.EasyMock.*;
import org.easymock.classextension.EasyMock;
import static org.easymock.EasyMock.*;
import org.easymock.Capture;
import org.easymock.IAnswer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Class for testing the GameMenu.update method
 * 
 * @author Eliot Moss
 */
public class TestGameMenu_update_render1 {

	private static BreakOutEngine engine;

	/**
	 * Directory name for testing levels
	 */
	private static final String testLevelsDir = "testLevels";

	/**
	 * Directory name for testing archives
	 */
	private static final String testArchivesDir = "testArchives";

	@BeforeClass
	public static void setUpBeforeClass () throws Exception {
		engine = BreakOutEngine.getEngine();
		GameLoader game = new GameLoader();
		game.setup(engine, new Dimension(800, 600), false);
		engine.initMe();

		File levelsDir = new File(testLevelsDir);
		levelsDir.mkdir();
		new File(testLevelsDir+File.separator+"Level1.xml").createNewFile();
		new File(testLevelsDir+File.separator+"Level2.xml").createNewFile();
		new File(testLevelsDir+File.separator+"Level3.xml").createNewFile();

		File archivesDir = new File(testArchivesDir);
		archivesDir.mkdir();
		new File(testArchivesDir+File.separator+"archive1.sav").createNewFile();
		new File(testArchivesDir+File.separator+"archive2.sav").createNewFile();
		new File(testArchivesDir+File.separator+"archive3.sav").createNewFile();
	}

	@AfterClass
	public static void tearDownAfterClass () throws Exception {
		// clean up test directory for levels
		File levelsDir = new File(testLevelsDir);
		if (levelsDir.canRead()) {
			for (File member : levelsDir.listFiles()) {
				member.delete();
			}
			levelsDir.delete();
		}
		// ... and for archives
		File archivesDir = new File(testArchivesDir);
		if (archivesDir.canRead()) {
			for (File member : archivesDir.listFiles()) {
				member.delete();
			}
			archivesDir.delete();
		}
	}

	private GameMenuWithMocking menu;
	private BaseInput mockInput;
	private Sprite mockPlay;
	private Sprite play;
	private Sprite mockQuit;
	private Sprite quit;
	private Sprite mockForward;
	private Sprite forward;
	private Sprite mockBackward;
	private Sprite backward;

	private GameFont mockTitle;
	private GameFont titleFont;
	private GameFont mockChoice;
	private GameFont choiceFont;


	@Before
	public void setUp () throws Exception {
		mockInput = createStrictMock(BaseInput.class);

		menu = new GameMenuWithMocking(engine, mockInput, testLevelsDir, testArchivesDir);
		menu.initResources();

		mockPlay = createMock(Sprite.class);
		play = menu.swapPlay(mockPlay);
		mockQuit = createMock(Sprite.class);
		quit = menu.swapQuit(mockQuit);
		mockForward = createMock(Sprite.class);
		forward = menu.swapForward(mockForward);
		mockBackward = createMock(Sprite.class);
		backward = menu.swapBackward(mockBackward);

		expect(mockPlay.getHeight()).andStubReturn(play.getHeight());
		expect(mockPlay.getWidth()).andStubReturn(play.getWidth());
		mockPlay.update(anyLong());
		expectLastCall().anyTimes();

		expect(mockQuit.getHeight()).andStubReturn(quit.getHeight());
		expect(mockQuit.getWidth()).andStubReturn(quit.getWidth());
		mockQuit.update(anyLong());
		expectLastCall().asStub();

		expect(mockForward.getHeight()).andStubReturn(forward.getHeight());
		expect(mockForward.getWidth()).andStubReturn(forward.getWidth());
		mockForward.update(anyLong());
		expectLastCall().asStub();

		expect(mockBackward.getHeight()).andStubReturn(backward.getHeight());
		expect(mockBackward.getWidth()).andStubReturn(backward.getWidth());
		mockBackward.update(anyLong());
		expectLastCall().anyTimes();

		mockTitle = createStrictMock(GameFont.class);
		titleFont = menu.swapTitleFont(mockTitle);
		mockChoice = createStrictMock(GameFont.class);
		choiceFont = menu.swapChoiceFont(mockChoice);

		// default setup for fonts
		expect(mockTitle.getWidth(isA(String.class))).andStubAnswer(new IAnswer<Integer>() {
			public Integer answer () {
				String in = (String)(EasyMock.getCurrentArguments()[0]);
				return titleFont.getWidth(in);
			}
		});
		expect(mockTitle.getHeight()).andStubReturn(titleFont.getHeight());

		expect(mockChoice.getWidth(isA(String.class))).andStubAnswer(new IAnswer<Integer>() {
			public Integer answer () {
				String in = (String)(EasyMock.getCurrentArguments()[0]);
				return choiceFont.getWidth(in);
			}
		});
		expect(mockChoice.getHeight()).andStubReturn(choiceFont.getHeight());

	}

	@After
	public void tearDown () throws Exception {
		// insure that possibly-deleted file is restored for later tests
		new File(testArchivesDir+File.separator+"archive1.sav").createNewFile();
	}

	private void fakeKey (int key) {
		final int keyWanted = key;
		expect(mockInput.isMousePressed(anyInt())).andReturn(false);
		expect(mockInput.isKeyPressed(anyInt())).andAnswer(new IAnswer<Boolean>() {
			public Boolean answer () {
				int keyAsked = (Integer)EasyMock.getCurrentArguments()[0];
				return keyAsked == keyWanted;
			}
		});
		expectLastCall().atLeastOnce();
	}

	private void fakeMouse () {
		expect(mockInput.isMousePressed(anyInt())).andReturn(true);
		expect(mockInput.isKeyPressed(anyInt())).andReturn(false).anyTimes();
	}

	@Test
	public void testUpdate_QuitYes () {
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey (KeyEvent.VK_Q);
		fakeKey (KeyEvent.VK_Y);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).startsWith("Quit"));
		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).contains("Level"));
		assertTrue(menu.sawFinish);
		assertNull(engine.nextGame);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}

	@Test
	public void testUpdate_QuitQuit () {
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey (KeyEvent.VK_Q);
		fakeKey (KeyEvent.VK_Q);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).startsWith("Quit"));
		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).startsWith("Quit"));
		assertFalse(menu.sawFinish);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}

	@Test
	public void testUpdate_QuitNo () {
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey (KeyEvent.VK_Q);
		fakeKey (KeyEvent.VK_N);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).startsWith("Quit"));
		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).contains("Level"));
		assertFalse(menu.sawFinish);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}

	@Test
	public void testUpdate_LevelBackwardForward () {
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey (KeyEvent.VK_ASTERISK); // a key we do not look for, just to allow update
		fakeKey (KeyEvent.VK_LEFT);
		fakeKey (KeyEvent.VK_RIGHT);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "Level1");
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "Level3");
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "Level1");
		assertFalse(menu.sawFinish);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}

	@Test
	public void testUpdate_LevelBackwardForwardMouse () {
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_ASTERISK); // a key we do not look for, just to allow update
		fakeMouse();
		fakeMouse();

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "Level1");
		menu.setMouseSprite(mockBackward);
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "Level3");
		menu.setMouseSprite(mockForward);
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "Level1");
		assertFalse(menu.sawFinish);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}

	@Test
	public void testRender_QuitYes () {

		fakeKey (KeyEvent.VK_Q);
		expect(mockTitle.drawString(isA(Graphics2D.class), startsWith("UMASS"), anyInt(), anyInt())).andReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), eq("Level1"), anyInt(), anyInt())).andReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), startsWith("Quit"), anyInt(), anyInt())).andReturn(0);
		fakeKey (KeyEvent.VK_Y);
		expect(mockTitle.drawString(isA(Graphics2D.class), startsWith("UMASS"), anyInt(), anyInt())).andReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), eq("Level1"), anyInt(), anyInt())).andReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), contains("Level"), anyInt(), anyInt())).andReturn(0);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput, mockTitle, mockChoice);

		Graphics2D g = menu.bsGraphics.getBackBuffer();
		menu.update(50);
		menu.render(g);
		menu.update(50);
		menu.render(g);
		assertTrue(menu.sawFinish);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput, mockTitle, mockChoice);
	}
	
	@Test
	public void testUpdate_Play(){

		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_RIGHT);
		fakeKey(KeyEvent.VK_ENTER);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "Level2");
		menu.update(50);
		assertEquals(engine.nextGameID, BreakOutEngine.BreakOutPlayGame);
		assertTrue(engine.nextLevel.endsWith("testLevels"+File.separator+"Level2.xml"));
		assertTrue(menu.sawFinish);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}
	
	@Test
	public void testUpdate_RestoreRestore(){
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_R);
		fakeKey(KeyEvent.VK_R);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertTrue(menu.getState().getChoiceString(menu).contains("archiv"));
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "Level1"); // found error here
		
		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}
	
	@Test
	public void testUpdate_RestoreBackwardForward() {
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_R);
		fakeKey(KeyEvent.VK_LEFT);
		fakeKey(KeyEvent.VK_RIGHT);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive3");
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		assertFalse(menu.sawFinish);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}
	
	@Test
	public void testUpdate_RestoreDeleteYes() {
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_R);
		fakeKey(KeyEvent.VK_D);
		fakeKey(KeyEvent.VK_Y);
		
		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
		
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).startsWith("Delete"));
		menu.update(50);
		assertFalse(new File(testArchivesDir+File.separator+"archive1.sav").canRead());
		assertEquals(menu.getState().getChoiceString(menu), "archive2");
		
		
		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}
	
	@Test
	public void testUpdate_RestoreDeleteNo() {
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_R);
		fakeKey(KeyEvent.VK_D);
		fakeKey(KeyEvent.VK_N);
		
		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
		
		
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).startsWith("Delete"));
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		assertTrue(new File(testArchivesDir+File.separator+"archive1.sav").exists());
		
		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}
	
	@Test
	public void testUpdate_RestorePlay(){
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_R);
		fakeKey(KeyEvent.VK_ENTER);
		
		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
		
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		menu.update(50);
		assertEquals(engine.nextGameID, BreakOutEngine.BreakOutRestore);
		assertTrue(engine.restoreFilename.endsWith("archive1.sav"));
		assertTrue(menu.sawFinish);
		
		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}
	
	@Test
	public void testUpdate_RestoreQuitYes(){
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_R);
		fakeKey(KeyEvent.VK_Q);
		fakeKey(KeyEvent.VK_Y);
		
		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
		
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).startsWith("Quit"));
		menu.update(50);
		assertTrue(menu.sawFinish);
		assertNull(engine.nextGame);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}
	
	@Test
	public void testUpdate_RestoreQuitNo(){
		expect(mockTitle.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class), isA(String.class), anyInt(), anyInt())).andStubReturn(0);

		fakeKey(KeyEvent.VK_R);
		fakeKey(KeyEvent.VK_Q);
		fakeKey(KeyEvent.VK_N);
		
		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput);

		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		menu.update(50);
		assertTrue(menu.getState().getMenuString(menu).startsWith("Quit"));
		menu.update(50);
		assertEquals(menu.getState().getChoiceString(menu), "archive1");
		assertFalse(menu.sawFinish);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput);
	}

	private boolean badOverlap (double x1, double y1, double w1, double h1,
			double x2, double y2, double w2, double h2) {
		return (x1+w1 >= x2) && (x2+w2 >= x1) && (y1+h1 >= y2) && (y2+h2 >= y1);
	}

	private boolean badOverlap (Sprite s, double x, double y, double w, double h) {
		double sx = s.getX();
		double sy = s.getY();
		double sw = s.getWidth();
		double sh = s.getHeight();
		return badOverlap (x, y, w, h, sx, sy, sw, sh);
	}

	private boolean badOverlap (Sprite s1, Sprite s2) {
		double sx = s1.getX();
		double sy = s1.getY();
		double sw = s1.getWidth();
		double sh = s1.getHeight();
		return badOverlap (s2, sx, sy, sw, sh);
	}

	private boolean badOverlap (double x, double y, double width, double height) {
		return
		badOverlap(play, x, y, width, height) ||
		badOverlap(quit, x, y, width, height) ||
		badOverlap(forward, x, y, width, height) ||
		badOverlap(backward, x, y, width, height);
	}

	@Test
	public void testRender_noOverlap () {
		Capture<String> s1 = new Capture<String>();
		Capture<String> s2 = new Capture<String>();
		Capture<String> s3 = new Capture<String>();
		Capture<Integer> x1 = new Capture<Integer>();
		Capture<Integer> x2 = new Capture<Integer>();
		Capture<Integer> x3 = new Capture<Integer>();
		Capture<Integer> y1 = new Capture<Integer>();
		Capture<Integer> y2 = new Capture<Integer>();
		Capture<Integer> y3 = new Capture<Integer>();

		fakeKey (KeyEvent.VK_ASTERISK);
		expect(mockTitle.drawString(isA(Graphics2D.class),
				and(startsWith("UMASS"), capture(s1)),
				and(anyInt(), capture(x1)),
				and(anyInt(), capture(y1)))).andReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class),
				and(eq("Level1"), capture(s2)),
				and(anyInt(), capture(x2)),
				and(anyInt(), capture(y2)))).andReturn(0);
		expect(mockChoice.drawString(isA(Graphics2D.class),
				and(contains("Level"), capture(s3)),
				and(anyInt(), capture(x3)),
				and(anyInt(), capture(y3)))).andReturn(0);

		replay(mockPlay, mockQuit, mockForward, mockBackward, mockInput, mockTitle, mockChoice);

		Graphics2D g = menu.bsGraphics.getBackBuffer();
		menu.update(50);
		menu.render(g);

		verify(mockPlay, mockQuit, mockForward, mockBackward, mockInput, mockTitle, mockChoice);

		double h1 = titleFont.getHeight();
		double h2 = choiceFont.getHeight();
		double h3 = h2;
		double w1 = titleFont.getWidth(s1.getValue());
		double w2 = choiceFont.getWidth(s2.getValue());
		double w3 = choiceFont.getWidth(s3.getValue());
		double dx1 = x1.getValue();
		double dx2 = x2.getValue();
		double dx3 = x3.getValue();
		double dy1 = y1.getValue();
		double dy2 = y2.getValue();
		double dy3 = y3.getValue();
		assertFalse(badOverlap(dx1, dy1, w1, h1));
		assertFalse(badOverlap(dx2, dy2, w2, h2));
		assertFalse(badOverlap(dx3, dy3, w3, h3));
		assertFalse(badOverlap(dx1, dy1, w1, h1, dx2, dy2, w2, h2));
		assertFalse(badOverlap(dx1, dy1, w1, h1, dx3, dy3, w3, h3));
		assertFalse(badOverlap(dx2, dy2, w2, h2, dx3, dy3, w3, h3));
		assertFalse(badOverlap(play, quit));
		assertFalse(badOverlap(play, forward));
		assertFalse(badOverlap(play, backward));
		assertFalse(badOverlap(quit, forward));
		assertFalse(badOverlap(quit, backward));
		assertFalse(badOverlap(backward, forward));
	}
}
