package Screens;
 
import java.awt.Color;
 
import Engine.Audio;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import Engine.Screen;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Level.Player;
import Level.PlayerListener;
import Maps.TestMap;
import Maps.TestMap2;
import Maps.TestMap3;
import Maps.TestMap4;
import Maps.TestMap5;
import Maps.TitleScreenMap;
import Players.Cat;
import Players.Dog;
import Players.Ninja;
import Players.Yoshi;
import SpriteFont.SpriteFont;
import Utils.Stopwatch;
 
// This class is for when the platformer game is actually being played
public class PlayLevelScreen extends Screen implements PlayerListener {
    protected ScreenCoordinator screenCoordinator;
    protected Map map;
    protected Player player;
    protected static PlayLevelScreenState playLevelScreenState;
    protected Stopwatch screenTimer = new Stopwatch();
    protected LevelClearedScreen levelClearedScreen;
    protected LevelLoseScreen levelLoseScreen;
    protected AvatarOptionsScreen avatar;
    protected SpriteFont livesLabel;
    private SpriteFont heart1Label, heart2Label, heart3Label;
    
    private boolean level1Completed = false;
    private boolean level2Completed = false;
    private boolean level3Completed = false;
    private boolean level4Completed = false;
    private boolean level5Completed = false;
 
    public PlayLevelScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
    }
 
    public void initialize() {
        // define/setup map
        if (level5Completed) {
            this.map = new TestMap();
            level1Completed = false;
            level2Completed = false;
            level3Completed = false;
            level4Completed = false;
            level5Completed = false;
            map.reset();
        } else if (level4Completed) {
            this.map = new TestMap5();
            map.reset();
        } else if (level3Completed) {
            this.map = new TestMap4();
            map.reset();
        } else if (level2Completed) {
            this.map = new TestMap3();
            map.reset();
        } else if (level1Completed) {
            this.map = new TestMap2();
            map.reset();
        } else {
            this.map = new TestMap();
            map.reset();
        }
        Audio.playMusic("BGM.wav");
 
        // setup player
        if (avatar.chosenavatar == 0) {
            this.player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
            this.player.setMap(map);
            this.player.addListener(this);
            this.player.setLocation(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
            this.playLevelScreenState = PlayLevelScreenState.RUNNING;
        } else if (avatar.chosenavatar == 1) {
            this.player = new Dog(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
            this.player.setMap(map);
            this.player.addListener(this);
            this.player.setLocation(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
            this.playLevelScreenState = PlayLevelScreenState.RUNNING;
        } else if (avatar.chosenavatar == 2) {
            this.player = new Yoshi(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
            this.player.setMap(map);
            this.player.addListener(this);
            this.player.setLocation(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
            this.playLevelScreenState = PlayLevelScreenState.RUNNING;
        } else if (avatar.chosenavatar == 3) {
               this.player = new Ninja(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
            this.player.setMap(map);
            this.player.addListener(this);
            this.player.setLocation(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y);
            this.playLevelScreenState = PlayLevelScreenState.RUNNING;
        }
    }
 
    public void update() {
        // based on screen state, perform specific actions
        switch (playLevelScreenState) {
        // if level is "running" update player and map to keep game logic for the
        // platformer level going
        case RUNNING:
            player.update();
            map.update(player);
            break;
        // if level has been completed, bring up level cleared screen
        case LEVEL_COMPLETED:
            levelClearedScreen = new LevelClearedScreen();
            levelClearedScreen.initialize();
            screenTimer.setWaitTime(2500);
            playLevelScreenState = PlayLevelScreenState.LEVEL_WIN_MESSAGE;
            break;
        // if level cleared screen is up and the timer is up for how long it should stay
        // out, go back to main menu
        case LEVEL_WIN_MESSAGE:
            if (screenTimer.isTimeUp()) {
                levelClearedScreen = null;
                //goBackToMenu();
                if (level1Completed == false) {
                    level1Completed = true;
                } else if (level2Completed == false) {
                    level2Completed = true;
                } else if (level3Completed == false) {
                    level3Completed = true;
                } else if (level4Completed == false) {
                    level4Completed = true;
                } else if (level5Completed == false) {
                    level5Completed = true;
                }
                
                
                resetLevel();
            }
            break;
        // if player died in level, bring up level lost screen
        case PLAYER_DEAD:
            levelLoseScreen = new LevelLoseScreen(this);
            levelLoseScreen.initialize();
            playLevelScreenState = PlayLevelScreenState.LEVEL_LOSE_MESSAGE;
            break;
        // wait on level lose screen to make a decision (either resets level or sends
        // player back to main menu)
        case LEVEL_LOSE_MESSAGE:
            levelLoseScreen.update();
            break;
        }
    }
 
    public void draw(GraphicsHandler graphicsHandler) {
        // based on screen state, draw appropriate graphics
        switch (playLevelScreenState) {
        case RUNNING:
        case LEVEL_COMPLETED:
        case PLAYER_DEAD:
            map.draw(graphicsHandler);
            player.draw(graphicsHandler);
            if (Player.getNumOfLives() >= 3) {
                graphicsHandler.drawImage(ImageLoader.load("FullHeart.png"), 670, 25,55,55);
                graphicsHandler.drawImage(ImageLoader.load("FullHeart.png"), 700, 25,55,55);
                graphicsHandler.drawImage(ImageLoader.load("FullHeart.png"), 730, 25,55,55);
            } else if (Player.getNumOfLives() == 2) {
                graphicsHandler.drawImage(ImageLoader.load("FullHeart.png"), 670, 25,55,55);
                graphicsHandler.drawImage(ImageLoader.load("FullHeart.png"), 700, 25,55,55);
                graphicsHandler.drawImage(ImageLoader.load("EmptyHeart.png"), 730, 25,55,55);
            } else if (Player.getNumOfLives() == 1) {
                graphicsHandler.drawImage(ImageLoader.load("FullHeart.png"), 670, 25,55,55);
                graphicsHandler.drawImage(ImageLoader.load("EmptyHeart.png"), 700, 25,55,55);
                graphicsHandler.drawImage(ImageLoader.load("EmptyHeart.png"), 730, 25,55,55);
            } else if (Player.getNumOfLives() < 1) {
                graphicsHandler.drawImage(ImageLoader.load("EmptyHeart.png"), 670, 25,55,55);
                graphicsHandler.drawImage(ImageLoader.load("EmptyHeart.png"), 700, 25,55,55);
                graphicsHandler.drawImage(ImageLoader.load("EmptyHeart.png"), 730, 25,55,55);
            }
            
            /*livesLabel = new SpriteFont("lives: " + Player.getNumOfLives(), 700, 60, "Comic Sans", 24, Color.white);
            livesLabel.setOutlineColor(Color.black);
            livesLabel.setOutlineThickness(2.0f);
            livesLabel.draw(graphicsHandler);*/
            break;
        case LEVEL_WIN_MESSAGE:
            levelClearedScreen.draw(graphicsHandler);
            break;
        case LEVEL_LOSE_MESSAGE:
            levelLoseScreen.draw(graphicsHandler);
            break;
        }
    }
 
    public PlayLevelScreenState getPlayLevelScreenState() {
        return playLevelScreenState;
    }
 
    public static boolean playLevelScreenRunning() {
        return playLevelScreenState == PlayLevelScreenState.RUNNING;
    }
    
    public void onLevelStarted() {
        Audio.playMusic("BGM.wav");
    }
    
    
 
    @Override
    public void onLevelCompleted() {
        playLevelScreenState = PlayLevelScreenState.LEVEL_COMPLETED;
        Audio.stopMusic();
        Audio.playMusic("src/win.wav");
    }
 
    @Override
     public void onDeath() {
       playLevelScreenState = PlayLevelScreenState.PLAYER_DEAD;
       Audio.stopMusic();
       Audio.playMusic("src/meow.wav");
 
    }
 
 
    public void resetLevel() {
        initialize();
    }
 
    public void goBackToMenu() {
        screenCoordinator.setGameState(GameState.MENU);
    }
 
    public static boolean levelOver() {
        if (playLevelScreenState == PlayLevelScreenState.RUNNING) {
            return false;
        } else
            return true;
    }
 
    // This enum represents the different states this screen can be in
    private enum PlayLevelScreenState {
        RUNNING, LEVEL_COMPLETED, PLAYER_DEAD, LEVEL_WIN_MESSAGE, LEVEL_LOSE_MESSAGE
    }
 
}