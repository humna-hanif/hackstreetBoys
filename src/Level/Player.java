package Level;
 
// *** CROUCH S BREAKS GAME
import Engine.Key;
import Engine.KeyLocker;
import Engine.Keyboard;
import GameObject.GameObject;
import GameObject.SpriteSheet;
import Utils.AirGroundState;
import Utils.Direction;
 
import java.util.ArrayList;
 
public abstract class Player extends GameObject {
    // values that affect player movement
    // these should be set in a subclass
    protected float walkSpeed = 0;
    protected float gravity = 0;
    protected float jumpHeight = 0;
    protected float jumpDegrade = 0;
    protected float terminalVelocityY = 0;
    protected float momentumYIncrease = 0;
 
    // values used to handle player movement
    protected float jumpForce = 0;
    protected float momentumY = 0;
    protected float moveAmountX, moveAmountY;
 
    // values used to keep track of player's current state
    protected PlayerState playerState;
    protected PlayerState previousPlayerState;
    protected Direction facingDirection;
    protected AirGroundState airGroundState;
    protected AirGroundState previousAirGroundState;
    protected static LevelState levelState;
 
    // classes that listen to player events can be added to this list
    protected ArrayList<PlayerListener> listeners = new ArrayList<>();
 
    // define keys
    protected KeyLocker keyLocker = new KeyLocker();
    protected Key JUMP_KEY = Key.UP;
    protected Key MOVE_LEFT_KEY = Key.LEFT;
    protected Key MOVE_RIGHT_KEY = Key.RIGHT;
    protected Key CROUCH_KEY = Key.DOWN;
    protected Key W_KEY = Key.W;
    protected Key A_KEY = Key.A;
    protected Key S_KEY = Key.S;
    protected Key D_KEY = Key.D;
 
    // if true, player cannot be hurt by enemies (good for testing)
    protected static boolean isInvincible = false;
    protected static boolean playerDead = false;
    
    protected static int numOfCollisions = 0;
    protected static int numOfLives;
    
 
    protected boolean firstCollision;
    protected long collisionStartTime, collisionTime, secondsPassed, millisPassed;
 
 
    public Player(SpriteSheet spriteSheet, float x, float y, String startingAnimationName) {
        super(spriteSheet, x, y, startingAnimationName);
        facingDirection = Direction.RIGHT;
        airGroundState = AirGroundState.AIR;
        previousAirGroundState = airGroundState;
        playerState = PlayerState.STANDING;
        previousPlayerState = playerState;
        levelState = LevelState.RUNNING;
        firstCollision = true;
        numOfLives = 3;
        numOfCollisions = 0;
 
    }
 
    public void update() {
        moveAmountX = 0;
        moveAmountY = 0;
 
        // if player is currently playing through level (has not won or lost)
        if (levelState == LevelState.RUNNING) {
            applyGravity();
 
            // update player's state and current actions, which includes things like
            // determining how much it should move each frame and if its walking or jumping
            do {
                previousPlayerState = playerState;
                handlePlayerState();
            } while (previousPlayerState != playerState);
 
            previousAirGroundState = airGroundState;
 
            // update player's animation
            super.update();
 
            // move player with respect to map collisions based on how much player needs to
            // move this frame
            super.moveYHandleCollision(moveAmountY);
            super.moveXHandleCollision(moveAmountX);
 
            updateLockedKeys();
        }
 
        // if player has beaten level
        else if (levelState == LevelState.LEVEL_COMPLETED) {
            updateLevelCompleted();
        }
 
        // if player has lost level
        else if (levelState == LevelState.PLAYER_DEAD) {
            updatePlayerDead();
        }
    }
 
    // add gravity to player, which is a downward force
    protected void applyGravity() {
        moveAmountY += gravity + momentumY;
    }
 
    // based on player's current state, call appropriate player state handling
    // method
    protected void handlePlayerState() {
        switch (playerState) {
        case STANDING:
            playerStanding();
            break;
        case WALKING:
            playerWalking();
            break;
        case CROUCHING:
            playerCrouching();
            break;
        case JUMPING:
            playerJumping();
            break;
        }
    }
 
    // player STANDING state logic
    protected void playerStanding() {
        // sets animation to a STAND animation based on which way player is facing
        currentAnimationName = facingDirection == Direction.RIGHT ? "STAND_RIGHT" : "STAND_LEFT";
 
        // if walk left or walk right key is pressed, player enters WALKING state
        if ((Keyboard.isKeyDown(MOVE_LEFT_KEY) || Keyboard.isKeyDown(MOVE_RIGHT_KEY))
                || (Keyboard.isKeyDown(A_KEY) || Keyboard.isKeyDown(D_KEY))) {
            playerState = PlayerState.WALKING;
        }
 
        // if jump key is pressed, player enters JUMPING state
        else if ((Keyboard.isKeyDown(JUMP_KEY) && !keyLocker.isKeyLocked(JUMP_KEY))
                || (Keyboard.isKeyDown(W_KEY) && !keyLocker.isKeyLocked(W_KEY))) {
            if (Keyboard.isKeyDown(JUMP_KEY)) {
                keyLocker.lockKey(JUMP_KEY);
            } else
                keyLocker.lockKey(W_KEY);
 
            playerState = PlayerState.JUMPING;
        }
 
        // if crouch key is pressed, player enters CROUCHING state
        else if (Keyboard.isKeyDown(CROUCH_KEY) || Keyboard.isKeyDown(S_KEY)) {
            playerState = PlayerState.CROUCHING;
        }
    }
 
    // player WALKING state logic
    protected void playerWalking() {
        // sets animation to a WALK animation based on which way player is facing
        currentAnimationName = facingDirection == Direction.RIGHT ? "WALK_RIGHT" : "WALK_LEFT";
 
        // if left key is pressed it moves left and allows for override
        if (Keyboard.isKeyDown(MOVE_LEFT_KEY) || Keyboard.isKeyDown(A_KEY)) {
            if (Keyboard.isKeyUp(MOVE_RIGHT_KEY) && Keyboard.isKeyUp(D_KEY)) {
                moveAmountX -= walkSpeed;
                facingDirection = Direction.LEFT;
            }
            if ((Keyboard.isKeyDown(MOVE_RIGHT_KEY) || Keyboard.isKeyDown(D_KEY))
                    && facingDirection == Direction.RIGHT) {
                moveAmountX -= walkSpeed;
            }
            if ((Keyboard.isKeyDown(MOVE_RIGHT_KEY) || Keyboard.isKeyDown(D_KEY))
                    && facingDirection == Direction.LEFT) {
                moveAmountX += walkSpeed;
            }
 
        }
 
        // if right is pressed it moves right
        else if (Keyboard.isKeyDown(MOVE_RIGHT_KEY) || Keyboard.isKeyDown(D_KEY)) {
            moveAmountX += walkSpeed;
            facingDirection = Direction.RIGHT;
 
        }
 
        else if ((Keyboard.isKeyUp(MOVE_LEFT_KEY) && Keyboard.isKeyUp(A_KEY))
                && (Keyboard.isKeyUp(MOVE_RIGHT_KEY) && Keyboard.isKeyUp(D_KEY))) {
            playerState = PlayerState.STANDING;
        }
 
        // if jump key is pressed, player enters JUMPING state
        if ((Keyboard.isKeyDown(JUMP_KEY) && !keyLocker.isKeyLocked(JUMP_KEY))
                || (Keyboard.isKeyDown(W_KEY) && !keyLocker.isKeyLocked(W_KEY))) {
            if (Keyboard.isKeyDown(JUMP_KEY)) {
                keyLocker.lockKey(JUMP_KEY);
            } else
                keyLocker.lockKey(W_KEY);
 
            playerState = PlayerState.JUMPING;
        }
 
        // if crouch key is pressed,
        else if (Keyboard.isKeyDown(CROUCH_KEY) || Keyboard.isKeyDown(S_KEY)) {
            playerState = PlayerState.CROUCHING;
        }
    }
 
    // player CROUCHING state logic
    protected void playerCrouching() {
        // sets animation to a CROUCH animation based on which way player is facing
        currentAnimationName = facingDirection == Direction.RIGHT ? "CROUCH_RIGHT" : "CROUCH_LEFT";
 
        // if crouch key is released, player enters STANDING state
        if (Keyboard.isKeyUp(CROUCH_KEY) && Keyboard.isKeyUp(S_KEY)) {
            playerState = PlayerState.STANDING;
        }
 
        // if jump key is pressed, player enters JUMPING state
        if ((Keyboard.isKeyDown(JUMP_KEY) && !keyLocker.isKeyLocked(JUMP_KEY))
                || (Keyboard.isKeyDown(W_KEY) && !keyLocker.isKeyLocked(W_KEY))) {
            if (Keyboard.isKeyDown(JUMP_KEY)) {
                keyLocker.lockKey(JUMP_KEY);
            } else {
                keyLocker.lockKey(W_KEY);
 
            }
            playerState = PlayerState.JUMPING;
        }
 
        // ** NEW ADDITION , 10/1/20 **
        // allows you to move left and right while crouching
 
        if (Keyboard.isKeyDown(MOVE_LEFT_KEY) || Keyboard.isKeyDown(A_KEY)) {
            moveAmountX -= walkSpeed;
        } else if (Keyboard.isKeyDown(MOVE_RIGHT_KEY) || Keyboard.isKeyDown(D_KEY)) {
            moveAmountX += walkSpeed;
        }
 
    }
 
    // player JUMPING state logic
    protected void playerJumping() {
        // if last frame player was on ground and this frame player is still on ground,
        // the jump needs to be setup
        if (previousAirGroundState == AirGroundState.GROUND && airGroundState == AirGroundState.GROUND) {
 
            // sets animation to a JUMP animation based on which way player is facing
            currentAnimationName = facingDirection == Direction.RIGHT ? "JUMP_RIGHT" : "JUMP_LEFT";
 
            // player is set to be in air and then player is sent into the air
            airGroundState = AirGroundState.AIR;
            jumpForce = jumpHeight;
            if (jumpForce > 0) {
                moveAmountY -= jumpForce;
                jumpForce -= jumpDegrade;
                if (jumpForce < 0) {
                    jumpForce = 0;
                }
            }
 
        }
 
        // if player is in air (currently in a jump) and has more jumpForce, continue
        // sending player upwards
        else if (airGroundState == AirGroundState.AIR) {
            if (jumpForce > 0) {
                moveAmountY -= jumpForce;
                jumpForce -= jumpDegrade;
                if (jumpForce < 0) {
                    jumpForce = 0;
                }
            }
 
            // if player is moving upwards, set player's animation to jump. if player moving
            // downwards, set player's animation to fall
            if (previousY > Math.round(y)) {
                currentAnimationName = facingDirection == Direction.RIGHT ? "JUMP_RIGHT" : "JUMP_LEFT";
            } else {
                currentAnimationName = facingDirection == Direction.RIGHT ? "FALL_RIGHT" : "FALL_LEFT";
            }
 
            // allows you to move left and right while in the air
            if (Keyboard.isKeyDown(MOVE_LEFT_KEY) || Keyboard.isKeyDown(A_KEY)) {
                moveAmountX -= walkSpeed;
            } else if (Keyboard.isKeyDown(MOVE_RIGHT_KEY) || Keyboard.isKeyDown(D_KEY)) {
                moveAmountX += walkSpeed;
            }
 
            // if player is falling, increases momentum as player falls so it falls faster
            // over time
            if (moveAmountY > 0) {
                increaseMomentum();
            }
        }
 
        // if player last frame was in air and this frame is now on ground, player
        // enters STANDING state
        else if (previousAirGroundState == AirGroundState.AIR && airGroundState == AirGroundState.GROUND) {
            playerState = PlayerState.STANDING;
        }
    }
 
    // while player is in air, this is called, and will increase momentumY by a set
    // amount until player reaches terminal velocity
    protected void increaseMomentum() {
        momentumY += momentumYIncrease;
        if (momentumY > terminalVelocityY) {
            momentumY = terminalVelocityY;
        }
    }
 
    protected void updateLockedKeys() {
        if (Keyboard.isKeyUp(JUMP_KEY)) {
            keyLocker.unlockKey(JUMP_KEY);
        }
        if (Keyboard.isKeyUp(W_KEY)) {
            keyLocker.unlockKey(W_KEY);
        }
 
    }
 
    @Override
    // prevents the player from falling off the edge map tile in the x direction
    public void onEndCollisionCheckX(boolean hasCollided, Direction direction) {
        // prevents player from falling off the left side of the map tile
        if (getX() < -17) {
            setX(-16);
        }
        // prevents player from falling off the right side of the map tile
        else if (levelState != LevelState.LEVEL_COMPLETED && getX() > (getCurrentMap().endBoundX - 53.109985F)) { 
            setX(getCurrentMap().endBoundX - 53.108032F); // made it so the end bounds is exactly at the end of the screen
        }
    }
 
    @Override
    public void onEndCollisionCheckY(boolean hasCollided, Direction direction) {
        // if player collides with a map tile below it, it is now on the ground
        // if player does not collide with a map tile below, it is in air
        if (direction == Direction.DOWN) {
            if (hasCollided) {
                momentumY = 0;
                airGroundState = AirGroundState.GROUND;
            } else {
                playerState = PlayerState.JUMPING;
                airGroundState = AirGroundState.AIR;
            }
        }
 
        // if player collides with map tile upwards, it means it was jumping and then
        // hit into a ceiling -- immediately stop upwards jump velocity
        else if (direction == Direction.UP) {
            if (hasCollided) {
                jumpForce = 0;
            }
        }
    }
 
    // other entities can call this method to hurt the player
    public void hurtPlayer(MapEntity mapEntity) {
        if (!isInvincible && levelState != LevelState.PLAYER_DEAD) {
            if (firstCollision) {
                collisionStartTime = System.currentTimeMillis();
                firstCollision = false;
                numOfCollisions = numOfCollisions + 1;
                numOfLives = numOfLives - 1;
            }
            
            collisionTime = System.currentTimeMillis() - collisionStartTime;
            secondsPassed = (collisionTime / 1000);
            if (secondsPassed > 0.25) {
                numOfCollisions = numOfCollisions + 1;
                numOfLives = numOfLives - 1;
                collisionStartTime = System.currentTimeMillis();
            }
            // if map entity is an enemy, kill player on touch
            if (mapEntity instanceof Enemy && numOfCollisions >= 3) {
                
                levelState = LevelState.PLAYER_DEAD;
            }
        }
    }
    
    // instantly kill the player
    public static void killPlayer() {
        if (!isInvincible && levelState != LevelState.PLAYER_DEAD) {
            numOfCollisions = 5;
            numOfLives = 0;
            levelState = LevelState.PLAYER_DEAD;
        }
    }
 
    // other entities can call this to tell the player they beat a level
    public void completeLevel() {
        levelState = LevelState.LEVEL_COMPLETED;
    }
 
    // if player has beaten level, this will be the update cycle
    public void updateLevelCompleted() {
        // if player is not on ground, player should fall until it touches the ground
        if (airGroundState != AirGroundState.GROUND && map.getCamera().containsDraw(this)) {
            currentAnimationName = "FALL_RIGHT";
            applyGravity();
            increaseMomentum();
            super.update();
            moveYHandleCollision(moveAmountY);
        }
        // move player to the right until it walks off screen
        else if (map.getCamera().containsDraw(this)) {
            currentAnimationName = "WALK_RIGHT";
            super.update();
            moveXHandleCollision(walkSpeed);
        } else {
            // tell all player listeners that the player has finished the level
            for (PlayerListener listener : listeners) {
                listener.onLevelCompleted();
            }
        }
    }
 
    // if player has died, this will be the update cycle
    public void updatePlayerDead() {
        // change player animation to DEATH
        if (!currentAnimationName.startsWith("DEATH")) {
            if (facingDirection == Direction.RIGHT) {
                currentAnimationName = "DEATH_RIGHT";
            } else {
                currentAnimationName = "DEATH_LEFT";
            }
            super.update();
        }
        // if death animation not on last frame yet, continue to play out death
        // animation
        else if (currentFrameIndex != getCurrentAnimation().length - 1) {
            super.update();
        }
        // if death animation on last frame (it is set up not to loop back to start),
        // player should continually fall until it goes off screen
        else if (currentFrameIndex == getCurrentAnimation().length - 1) {
            if (map.getCamera().containsDraw(this)) {
                moveY(3);
            } else {
                // tell all player listeners that the player has died in the level
                for (PlayerListener listener : listeners) {
                    listener.onDeath();
                }
            }
        }
    }
 
    public PlayerState getPlayerState() {
        return playerState;
    }
 
    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }
 
    public AirGroundState getAirGroundState() {
        return airGroundState;
    }
 
    public Direction getFacingDirection() {
        return facingDirection;
    }
 
    public void setFacingDirection(Direction facingDirection) {
        this.facingDirection = facingDirection;
    }
 
    public void setLevelState(LevelState levelState) {
        this.levelState = levelState;
    }
 
    public void addListener(PlayerListener listener) {
        listeners.add(listener);
    }
    public static int getNumOfLives() {
        return numOfLives;
    }
}