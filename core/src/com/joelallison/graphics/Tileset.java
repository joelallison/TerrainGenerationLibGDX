package com.joelallison.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class Tileset {
    public String creator;
    private String spriteSheet;
    private Texture spriteSheetTexture;
    public int tileSize;
    private Color baseColor;
    private String baseColorHex;
    public HashMap<String, TileCorners> map = new HashMap<String, TileCorners>(); //Tile name : Tile location

    public Tileset(String creator, String spriteSheet, int tileSize, String baseColorHex, HashMap<String, TileCorners> map) {
        this.creator = creator;
        this.spriteSheet = spriteSheet;
        this.tileSize = tileSize;
        this.map = map;
        this.baseColorHex = baseColorHex;
    }


    public static class TileCorners {
        int cornerX;
        int cornerY;
        public TileCorners(int cornerX, int cornerY) {
            this.cornerX = cornerX;
            this.cornerY = cornerY;
        }
    }

    public static abstract class TileSpec {
        public String name; //universal across all

        public TileSpec(String name) { //for TerrainLayer
            this.name = name;
        }


    }

    //for Terrain
    public static class TerrainTileSpec extends TileSpec {
        public Float lowerBound;
        public TerrainTileSpec(String name, Float lowerBound) {
            super(name);
            this.lowerBound = lowerBound;
        }
    }

    public static class MazeTileSpec extends TileSpec {
        public int orientationID;
        public MazeTileSpec(String name, int orientationID) {
            super(name);
            this.orientationID = orientationID;
        }
    }

    public void initTileset() {
        spriteSheetTexture = new Texture(Gdx.files.internal(this.spriteSheet));
        baseColor = Color.valueOf(this.baseColorHex);
    }

    public Color getColor() {
        return baseColor;
    }

    public void setColor(Color color) {
        this.baseColor = color;
    }

    public TextureRegion getTileTexture(TileCorners tileCorners) {
        return new TextureRegion(this.spriteSheetTexture, getActualTileLocation(tileCorners.cornerX), getActualTileLocation(tileCorners.cornerY), tileSize, tileSize);
    }

    public int getActualTileLocation(int location) { //input will be cornerX or cornerY, as those values are relative and not actual pixel values
        return tileSize * location;
    }

    public String getSpriteSheetName() {
        return spriteSheet;
    }

    public void setSpriteSheetName(String spriteSheet) {
        this.spriteSheet = spriteSheet;
    }

    public Texture getSpriteSheet() {
        return spriteSheetTexture;
    }

    public void setSpriteSheet(String fileLocation) { // used specifically where using with Gdx.files.internal isn't the right option
        spriteSheetTexture = new Texture(fileLocation);
    }



}
