package com.joelallison.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.joelallison.main.GameScreen;
import com.joelallison.main.Init;

public class Player extends Entity {

    float stateTime;

    public Player(int xPosition, int yPosition, Texture spriteSheet, int FRAME_COLS, int FRAME_ROWS){
        super(xPosition, yPosition, spriteSheet, FRAME_COLS, FRAME_ROWS);
    }

    public void initAnimations() {
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / FRAME_COLS, spriteSheet.getHeight() / FRAME_ROWS);

        animations.put("walk", new Animation<TextureRegion>(1f/1.5f,
                new TextureRegion[]{tmp[0][0], tmp[0][1], tmp[0][2]}
        ));
    }

    
    public void handleInput() {

        //movement
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            setyPosition(yPosition + 1);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            setyPosition(yPosition - 1);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            setxPosition(xPosition - 1);
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            setxPosition(xPosition + 1);
        }

        //zoom
        if(Gdx.input.isKeyPressed(Input.Keys.P)){
            Init.camera.zoom += 0.02;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.O)){
            Init.camera.zoom -= 0.02;
        }
    }

    public static TextureRegion getFrame(float stateTime, boolean looping) {

        return animations.get("walk").getKeyFrame(stateTime, looping);
    }

    public void zoom() {
        float zoomMod = 0;


    }

}
