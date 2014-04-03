/**
 * 
 */
package BOut;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;

import static org.easymock.classextension.EasyMock.*;

import java.awt.Dimension;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * class for testing the GameMenu class
 * 
 * @author Eliot Moss
 */
public class TestGameMenu_initResources {

  /**
   * the BOEngine in whose context the GameMenu will run
   */
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

  @Before
  public void setUp () throws Exception {
  }

  @After
  public void tearDown () throws Exception {
  }

  /**
   * Test method for {@link BOut.GameMenu#initResources()}.
   */
  @Test
  public void testInitResources () {

    PlayField mockField = createMock(PlayField.class);
    ImageBackground mockBack = createMock(ImageBackground.class);
    
    expect(mockBack.getHeight()).andStubReturn(600);
    expect(mockBack.getWidth()).andStubReturn(800);

    mockField.setBackground(mockBack);
    mockField.add(isA(Sprite.class));
    expectLastCall().times(4);
    
    replay(mockField, mockBack);

    GameMenu menu = new GameMenuWithMocking(engine, mockField, mockBack, testLevelsDir, testArchivesDir);
    menu.initResources();

    verify(mockField, mockBack);
    List<String> apparentLevels = new ArrayList<String>();
    for (File file : menu.levelsFiles())
    {
      apparentLevels.add(file.getName());
    }
    String [] expectedLevels = new String[] {"Level1.xml", "Level2.xml", "Level3.xml"};
    assertEquals(apparentLevels.size(), 3);
    for (String aName : expectedLevels)
    {
      assertTrue(apparentLevels.contains(aName));
    }
  }

}
