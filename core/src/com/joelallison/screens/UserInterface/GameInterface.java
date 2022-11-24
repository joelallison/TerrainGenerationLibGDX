package com.joelallison.screens.UserInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.text.DecimalFormat;

public class GameInterface extends UserInterface {

    Label scaleLabel = new Label("Scale:", chosenSkin);
    Label octavesLabel = new Label("Octaves:", chosenSkin);
    Label lacunarityLabel = new Label("Lacunarity:", chosenSkin);
    Label wrapFactorLabel = new Label("Wrap factor:", chosenSkin);
    Label invertLabel = new Label("Invert:", chosenSkin);
    CheckBox invertCheck = new CheckBox("", chosenSkin);
    DecimalFormat floatFormat = new DecimalFormat("##0.00");
    DecimalFormat intFormat = new DecimalFormat("00000");

    public void genUI(){
        //toolbar
        final TextButton file = new TextButton("File", chosenSkin, "default");
        file.setSize(55f, 25f);
        file.setPosition(0, Gdx.graphics.getHeight() - file.getHeight());

        file.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                file.setText("Bingus!");
            }
        });

        stage.addActor(file);

        final TextButton edit = new TextButton("Edit", chosenSkin, "default");
        edit.setSize(55f, 25f);
        edit.setPosition(0 + file.getWidth(), Gdx.graphics.getHeight() - edit.getHeight());

        edit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                edit.setText("Bingus?");
            }
        });

        stage.addActor(edit);

        //box to edit leftPanel of generation
        final Window leftPanel = new Window("Generation parameters:", chosenSkin);
        leftPanel.setSize(250f, 500f);
        leftPanel.setPosition(5f ,200f);
        leftPanel.setMovable(false);

        scaleLabel.setPosition(leftPanel.getX(), leftPanel.getY() + leftPanel.getHeight() / 2);
        leftPanel.addActor(scaleLabel);

        final Slider scaleSlider = new Slider(0.005f, 256f, 0.001f, false, chosenSkin);
        scaleSlider.setPosition(scaleLabel.getX() + 44f, scaleLabel.getY()-2);
        scaleSlider.setValue(Float.parseFloat(values[0]));

        scaleSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                values[0] = Float.toString(scaleSlider.getValue());
            }
        });

        leftPanel.addActor(scaleSlider);

        octavesLabel.setPosition(leftPanel.getX(), (leftPanel.getY() + leftPanel.getHeight() / 2) - 24);
        leftPanel.addActor(octavesLabel);

        final Slider octavesSlider = new Slider(1, 3, 1, false, chosenSkin);
        octavesSlider.setPosition(octavesLabel.getX() + 64f, octavesLabel.getY()-2);
        octavesSlider.setWidth(120f);
        octavesSlider.setValue(Float.parseFloat(values[1]));

        octavesSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                values[1] = Integer.toString((int) octavesSlider.getValue());
            }
        });

        leftPanel.addActor(octavesSlider);

        lacunarityLabel.setPosition(leftPanel.getX(), (leftPanel.getY() + leftPanel.getHeight() / 2) - 48);
        leftPanel.addActor(lacunarityLabel);

        final Slider lacunaritySlider = new Slider(0.01f, 10f, 0.01f, false, chosenSkin);
        lacunaritySlider.setPosition(lacunarityLabel.getX() + 80f, lacunarityLabel.getY()-2);
        lacunaritySlider.setWidth(108f);
        lacunaritySlider.setValue(Float.parseFloat(values[2]));

        lacunaritySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                values[2] = Float.toString(lacunaritySlider.getValue());
            }
        });

        leftPanel.addActor(lacunaritySlider);

        wrapFactorLabel.setPosition(leftPanel.getX(), (leftPanel.getY() + leftPanel.getHeight() / 2) - 72);
        leftPanel.addActor(wrapFactorLabel);

        final Slider wrapFactorSlider = new Slider(1, 20, 1, false, chosenSkin);
        wrapFactorSlider.setPosition(wrapFactorLabel.getX() + 92f, wrapFactorLabel.getY()-2);
        wrapFactorSlider.setWidth(96f);
        wrapFactorSlider.setValue(Float.parseFloat(values[3]));

        wrapFactorSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                values[3] = Integer.toString((int) wrapFactorSlider.getValue());
            }
        });

        leftPanel.addActor(wrapFactorSlider);


        invertLabel.setPosition(leftPanel.getX(), (leftPanel.getY() + leftPanel.getHeight() / 2) - 96);
        leftPanel.addActor(invertLabel);
        invertCheck.setPosition(leftPanel.getX() + 48, (leftPanel.getY()-2 + leftPanel.getHeight() / 2) - 96);
        invertCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                values[4] = Boolean.toString(invertCheck.isChecked());
            }
        });

        leftPanel.addActor(invertCheck);

        stage.addActor(leftPanel);
    }

    public void update() {
        scaleLabel.setText("Scale:                                      " + floatFormat.format(Float.parseFloat(values[0])));
        octavesLabel.setText("Octaves:                                 " + intFormat.format(Integer.parseInt(values[1])));
        lacunarityLabel.setText("Lacunarity:                             " + floatFormat.format(Float.parseFloat(values[2])));
        wrapFactorLabel.setText("Wrap Factor:                            " + floatFormat.format(Integer.parseInt(values[3])));

        //if showLeft then show left panel etc.

    }

    @Override
    public void valuesDeclaration(){
        values[0] = "20f"; //scale
        values[1] = "2"; //octaves
        values[2] = "2f"; //lacunarity
        values[3] = "1"; //wrap factor
        values[4] = "false"; //invert?
    }
}
