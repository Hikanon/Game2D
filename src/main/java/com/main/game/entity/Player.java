package com.main.game.entity;

import com.main.engine.KeyHandler;
import com.main.engine.Sprite;
import com.main.game.GamePanel;
import com.main.engine.enums.Direction;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@Getter
@Setter
public class Player extends Entity{

    private final KeyHandler keyHandler;
    private final Point[] startAnimPoints;
    private int animCount = 0;
    private int spriteCount = 0;
    private final int runningSpeed;
    private final int defaultSpeed;
    private final GamePanel gamePanel;
    private BufferedImage drawingSprite;
    private Direction oldDirection = Direction.DOWN;

    public Player(Point position, int speed, KeyHandler keyHandler, GamePanel gamePanel) throws IOException {
        super(position, speed);
        this.keyHandler = keyHandler;
        BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/playerSprites/player.png")));
        Dimension playerSize = new Dimension(48, 48);
        this.sprites =  new Sprite(image, playerSize.width, playerSize.height);
        this.gamePanel = gamePanel;
        runningSpeed = speed *2;
        defaultSpeed = speed;
        startAnimPoints = new Point[]{new Point(2, 20), new Point(2, 2), new Point(2, 38), new Point(2, 56)};
        hitBox = new Rectangle(position, new Dimension(playerSize.width, playerSize.height));
    }

    /**
     * offsets[0] = -1, если идем вверх
     * offsets[1] = 1, если идем вниз
     * offsets[2] = -1, если идем влево
     * offsets[3] = 1, если идем вправо
     * offsets[4] = 1, SHIFT
     * */
    @Override
    public void move(byte[] offsets){

        int newPositionX = this.position.x + (offsets[2] + offsets[3]) * this.speed;
        int newPositionY = this.position.y + (offsets[1] + offsets[0]) * this.speed;

        if(offsets[0] == -1) {
            direction = Direction.UP;
        }if(offsets[1] == 1) {
            direction = Direction.DOWN;
        }if(offsets[2] == -1){
            direction = Direction.LEFT;
        }if(offsets[3] == 1){
            direction = Direction.RIGHT;
        }

        if(newPositionX + this.hitBox.width < GamePanel.SCREEN_WIDTH && newPositionX > 0){
            if(collisionDirection == Direction.RIGHT){
                this.position.x += offsets[2] * this.getSpeed();
            }else  if(collisionDirection == Direction.LEFT){
                this.position.x += offsets[3] * this.getSpeed();
            }else{
                this.position.x += (offsets[2] + offsets[3]) * this.getSpeed();
            }
        }
       if(newPositionY + this.hitBox.height < GamePanel.SCREEN_HEIGHT && newPositionY > 0){
           if(collisionDirection == Direction.UP){
               this.position.y += offsets[1] * this.getSpeed();
           }else  if(collisionDirection == Direction.DOWN){
               this.position.y += offsets[0] * this.getSpeed();
           }else{
               this.position.y += (offsets[0] + offsets[1]) * this.getSpeed();
           }
       }
       this.hitBox.setLocation(this.position.x, this.position.y);
       oldDirection = direction;
    }

    void chooseSprite(){
        BufferedImage image = null;
        switch (direction) {
            case UP -> {
                if(oldDirection == direction && keyHandler.getPlayerOffset()[0] == -1){
                    image = sprites.getSprites().getSubimage((startAnimPoints[0].x + (16* animCount)) + (2 * animCount), startAnimPoints[0].y, 16, 16);
                }else image = sprites.getSprites().getSubimage(startAnimPoints[0].x, startAnimPoints[0].y, 16, 16);
            }
            case DOWN -> {
                if(oldDirection == direction && keyHandler.getPlayerOffset()[1] == 1){
                    image = sprites.getSprites().getSubimage((startAnimPoints[1].x + (16 * animCount)) + (2 *animCount), startAnimPoints[1].y, 16, 16);
                }else image = sprites.getSprites().getSubimage(startAnimPoints[1].x, startAnimPoints[1].y, 16, 16);
            }
            case LEFT -> {
                if (oldDirection == direction && keyHandler.getPlayerOffset()[2] == 1) {
                    image = sprites.getSprites().getSubimage((startAnimPoints[2].x + (16 * animCount)) + (2 * animCount), startAnimPoints[2].y, 16, 16);
                } else image = sprites.getSprites().getSubimage(startAnimPoints[2].x, startAnimPoints[2].y, 16, 16);
            }
            case RIGHT ->{
                if(oldDirection == direction && keyHandler.getPlayerOffset()[3] == 1) {
                    image = sprites.getSprites().getSubimage((startAnimPoints[3].x + (16 * animCount)) + (2 * animCount), startAnimPoints[3].y, 16, 16);
                }else image = sprites.getSprites().getSubimage(startAnimPoints[3].x, startAnimPoints[3].y, 16, 16);
            }
        }
        spriteCount++;
        if(spriteCount == 12){
            animCount++;
            spriteCount = 0;
        }
        if (animCount > 3) animCount = 2;
        oldDirection = direction;
        drawingSprite = image;
    }

    public void update(){
        this.move(keyHandler.getPlayerOffset());
        this.chooseSprite();
        collisionOn = false;
        gamePanel.getCollisionManager().checkCollision(this);
    }

    public void draw(Graphics2D graphics2D){

        graphics2D.drawImage(drawingSprite, position.x, position.y, sprites.getSpriteW(),sprites.getSpriteH(), null);
    }
}
