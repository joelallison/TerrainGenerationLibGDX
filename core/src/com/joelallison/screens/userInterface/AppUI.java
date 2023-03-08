package com.joelallison.screens.userInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.joelallison.generation.Layer;
import com.joelallison.generation.MazeLayer;
import com.joelallison.generation.TerrainLayer;
import com.joelallison.graphics.Tileset;
import com.joelallison.user.Database;

import java.text.DecimalFormat;
import java.util.*;

import static com.joelallison.screens.AppScreen.*;

public class AppUI extends UI {

    //many elements of the ui are used in multiple methods, so it's best that they're all declared globally [to the class]

    //for all layer type gen settings
    protected TextButton layerSeedButton = new TextButton("Seed: ", skin);
    protected CheckBox inheritSeedCheck = new CheckBox("", skin);

    //for terrain gen settings
    protected Label scaleLabel = new Label("Scale:", skin);
    protected Label octavesLabel = new Label("Octaves:", skin);
    protected Label lacunarityLabel = new Label("Lacunarity:", skin);
    protected Label wrapFactorLabel = new Label("Wrap factor:", skin);
    protected Label invertLabel = new Label("Invert:", skin);
    protected CheckBox invertCheck = new CheckBox("", skin);
    protected Label centerPointLabel = new Label("x: , y:", skin); // for variable names, I'm reluctantly using the American spelling of 'centre' as it feels like convention :-(
    protected TextButton centerPointButton = new TextButton("Centre coords:", skin);
    String integerFilter = "[0-9]+";
    final Slider scaleSlider = new Slider(TerrainLayer.SCALE_MIN, TerrainLayer.SCALE_MAX, 0.001f, false, skin);
    final Slider octavesSlider = new Slider(TerrainLayer.OCTAVES_MIN, TerrainLayer.OCTAVES_MAX, 1, false, skin);
    final Slider lacunaritySlider = new Slider(TerrainLayer.LACUNARITY_MIN, TerrainLayer.LACUNARITY_MAX, 0.01f, false, skin);
    final Slider wrapFactorSlider = new Slider(TerrainLayer.WRAP_MIN, TerrainLayer.WRAP_MAX, 1, false, skin);
    protected static DecimalFormat floatFormat = new DecimalFormat("##0.00");
    protected static DecimalFormat intFormat = new DecimalFormat("00000");
    protected Window generationSettingsPanel = new Window("Generation parameters:", skin);
    protected Window layerPanel = new Window("Layers:", skin);
    protected Label selectedLayerLabel = new Label("The currently selected layer is '[].", skin);

    //for tile panel
    protected Window tilePanel = new Window("Tiles and aesthetics: ", skin);
    Label selectedLayerTilesetLabel = new Label("", skin);
    static VerticalGroup tiles = new VerticalGroup();
    //layer panel
    protected VerticalGroup layerGroup = new VerticalGroup();
    boolean layersChanged = false;
    boolean tileDataChanged = false;
    //misc ui
    String helpMsg = "Press TAB to toggle UI.\nUse '<' and '>' to zoom in and out. All window-box things are draggable and movable!\nThe * layer button is used to select that layer. The ! layer button is a shortcut to exporting the layer individually.";
    protected Label controlsTips = new Label(helpMsg, skin);
    protected Label topLabel = new Label("Name: , Seed: \nx: , y: ", skin);
    //global vars
    static Stage stage;
    public static int selectedLayerIndex;
    public static String saveProgress = ""; // for updating the user on progress of save, public static so it can be changed in other classes
    //misc ui buttons at top of screen
    TextButton saveWorldButton = new TextButton("Save", skin);
    Label saveDialogText = new Label(saveProgress, skin);
    TextButton exportWorldButton = new TextButton("Export", skin);

    public void genUI(final Stage stage) { //stage is made final so that it can be accessed within inner classes
        this.stage = stage;

        //box to edit generation settings for selected layer
        doGenerationSettingsPanel();
        stage.addActor(generationSettingsPanel);

        //box for managing the different layers of generation
        layerPanel.add(layerGroup);
        doLayerPanel();
        stage.addActor(layerPanel);

        //box to edit tiles and visuals
        doTilePanel();
        stage.addActor(tilePanel);

        topLabel.setPosition(48, Gdx.graphics.getHeight() - (2 * topLabel.getPrefHeight()));
        stage.addActor(topLabel);

        saveWorldButton.setPosition(Gdx.graphics.getWidth() - saveWorldButton.getPrefWidth() * 1.5f, Gdx.graphics.getHeight() - saveWorldButton.getPrefHeight() * 1.5f);
        saveWorldButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Dialog saveDialog = new Dialog("Saving...", skin) {
                    public void result(Object obj) {
                        if (obj.equals(true)) {
                            if (!saveProgress.equals("Done!")) {
                                //if saving isn't finished, the 'OK' button does nothing
                                cancel();
                            }
                        }
                    }
                };
                saveDialog.add(saveDialogText);
                saveDialog.row();
                saveDialog.button("OK", true);
                saveDialog.setSize(200, 200);
                saveDialog.show(stage);

                Database.saveWorld(username, world);
                return true;
            }
        });

        stage.addActor(saveWorldButton);

        exportWorldButton.setPosition(saveWorldButton.getX(), saveWorldButton.getY() - exportWorldButton.getPrefHeight() - 8);
        stage.addActor(exportWorldButton);

        // TextFields don't lose focus by default when you click out, so...
        stage.getRoot().addCaptureListener(new InputListener() {
            // this code was gratefully found on a GitHub forum --> https://github.com/libgdx/libgdx/issues/2173
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) stage.setKeyboardFocus(null);
                return false;
            }
        });

        // text to tell users of otherwise hidden keybinds
        controlsTips.setPosition(8, 8);

        stage.addActor(controlsTips);
    }

    public void update(float delta) {
        //updating the ui
        updateGenerationSettingsPanel();
        updateLayerPanel();
        updateTilePanel();

        topLabel.setText("Name: " + world.name + ", Seed: " + world.seed + "\nx: " + userInput.getxPosition() + " y: " + userInput.getyPosition());

        saveDialogText.setText(saveProgress);

        //hide/show ui elements, just using one of their 'isVisible' booleans as a measure of if they're hidden or not
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (generationSettingsPanel.isVisible()) {
                generationSettingsPanel.setVisible(false);
                topLabel.setVisible(false);
                layerPanel.setVisible(false);
                tilePanel.setVisible(false);
                controlsTips.setText("Press TAB to toggle UI.");
            } else {
                generationSettingsPanel.setVisible(true);
                topLabel.setVisible(true);
                layerPanel.setVisible(true);
                tilePanel.setVisible(true);
                controlsTips.setText(helpMsg);
            }
        }
    }

    protected void doGenerationSettingsPanel() {
        String selectedLayerType = world.layers.get(selectedLayerIndex).getClass().getName().replace("com.joelallison.generation.", "").replace("Layer", "");

        Label inheritLabel = new Label("Inherit global seed: ", skin);
        generationSettingsPanel.add(inheritLabel);
        inheritSeedCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                world.layers.get(selectedLayerIndex).setInheritSeed(inheritSeedCheck.isChecked());
            }
        });
        generationSettingsPanel.add(inheritSeedCheck);
        generationSettingsPanel.row();

        layerSeedButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                final TextField inputField = new TextField("", skin);
                inputField.setTextFieldFilter(new TextField.TextFieldFilter() {
                    // Accepts all Alphanumeric Characters except
                    public boolean acceptChar(TextField textField, char c) {
                        if (Character.toString(c).matches(integerFilter)) {
                            return true;
                        }
                        return false;
                    }
                });
                Dialog dialog = new Dialog("Edit seed", skin) {
                    public void result(Object obj) {
                        if (obj.equals(true)) {
                            if (!inputField.getText().equals("")) {
                                world.layers.get(selectedLayerIndex).setSeed(Long.parseLong(inputField.getText()));
                            } else {
                                //if left blank, do nothing, just close window
                            }
                        } else {
                            cancel();
                        }
                    }
                };
                dialog.text("Current seed is " + world.layers.get(selectedLayerIndex).getSeed() + "\nEnter new seed:");
                dialog.add(inputField);
                dialog.button("OK", true);
                dialog.button("Cancel", false);
                dialog.show(stage);
                return true;
            }
        });

        generationSettingsPanel.add(layerSeedButton).colspan(2).align(Align.center);
        generationSettingsPanel.row();

        switch (selectedLayerType) {
            case "Terrain":
                loadTerrainSettings();

                break;
            default:
                // 0_o
        }

        generationSettingsPanel.setPosition(5f, Gdx.graphics.getHeight() - 400f);
        generationSettingsPanel.padLeft(4f);
        generationSettingsPanel.padRight(4f);
        generationSettingsPanel.setSize(generationSettingsPanel.getPrefWidth() * 1.2f, generationSettingsPanel.getPrefHeight());
    }

    protected void loadTerrainSettings() {
        generationSettingsPanel.add(scaleLabel);

        scaleSlider.setValue(((TerrainLayer) world.layers.get(selectedLayerIndex)).getScale());
        scaleSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((TerrainLayer) world.layers.get(selectedLayerIndex)).setScale(scaleSlider.getValue());
            }
        });

        generationSettingsPanel.add(scaleSlider);
        generationSettingsPanel.row();

        generationSettingsPanel.add(octavesLabel);

        octavesSlider.setValue(((TerrainLayer) world.layers.get(selectedLayerIndex)).getOctaves());

        octavesSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((TerrainLayer) world.layers.get(selectedLayerIndex)).setOctaves((int) octavesSlider.getValue());
            }
        });

        generationSettingsPanel.add(octavesSlider);

        generationSettingsPanel.row();
        generationSettingsPanel.add(lacunarityLabel);

        lacunaritySlider.setValue(((TerrainLayer) world.layers.get(selectedLayerIndex)).getLacunarity());

        lacunaritySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((TerrainLayer) world.layers.get(selectedLayerIndex)).setLacunarity(lacunaritySlider.getValue());
            }
        });

        generationSettingsPanel.add(lacunaritySlider);

        generationSettingsPanel.row();
        generationSettingsPanel.add(wrapFactorLabel);

        wrapFactorSlider.setValue(((TerrainLayer) world.layers.get(selectedLayerIndex)).getWrap());

        wrapFactorSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((TerrainLayer) world.layers.get(selectedLayerIndex)).setWrap((int) wrapFactorSlider.getValue());
            }
        });

        generationSettingsPanel.add(wrapFactorSlider);

        generationSettingsPanel.row();
        generationSettingsPanel.add(invertLabel);
        invertCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ((TerrainLayer) world.layers.get(selectedLayerIndex)).setInvert(invertCheck.isChecked());
            }
        });

        generationSettingsPanel.add(invertCheck);

        generationSettingsPanel.row();
        generationSettingsPanel.add(centerPointButton);
        centerPointButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Dialog dialog = new Dialog("Editing centre coords", skin, "dialog") {
                    //AAAAAAA
                };
                dialog.show(stage);
                return true;
            }
        });


        generationSettingsPanel.add(centerPointLabel);
    }

    protected void updateGenerationSettingsPanel() {
        String layerShown = "(hidden)";
        if (world.layers.get(selectedLayerIndex).layerShown()) {
            layerShown = "(shown)";
        }

        generationSettingsPanel.getTitleLabel().setText("Generation Settings: " + world.layers.get(selectedLayerIndex).getName() + " " + layerShown);
        inheritSeedCheck.setChecked(world.layers.get(selectedLayerIndex).inheritSeed());
        layerSeedButton.setText("Seed: " + world.layers.get(selectedLayerIndex).getSeed());

        updateGenerationSettingsTerrain();
    }

    protected void updateGenerationSettingsTerrain() {
        switch (getLayerTypeChar(world.layers.get(selectedLayerIndex))) {
            case 'T':
                if (((TerrainLayer) world.layers.get(selectedLayerIndex)).getOctaves() < 2) { // lacunarity has no effect if octaves is less than 2, this visual update attempts to indicate that to the user
                    lacunarityLabel.setColor(0.45f, 0.45f, 0.45f, 1);
                } else {
                    lacunarityLabel.setColor(1, 1, 1, 1);
                }

                scaleLabel.setText("Scale: " + floatFormat.format(((TerrainLayer) world.layers.get(selectedLayerIndex)).getScale()));
                octavesLabel.setText("Octaves: " + intFormat.format(((TerrainLayer) world.layers.get(selectedLayerIndex)).getOctaves()));
                lacunarityLabel.setText("Lacunarity: " + floatFormat.format(((TerrainLayer) world.layers.get(selectedLayerIndex)).getLacunarity()));
                wrapFactorLabel.setText("Wrap Factor: " + floatFormat.format(((TerrainLayer) world.layers.get(selectedLayerIndex)).getWrap()));

                scaleSlider.setValue(((TerrainLayer) world.layers.get(selectedLayerIndex)).getScale());
                octavesSlider.setValue(((TerrainLayer) world.layers.get(selectedLayerIndex)).getOctaves());
                lacunaritySlider.setValue(((TerrainLayer) world.layers.get(selectedLayerIndex)).getLacunarity());
                wrapFactorSlider.setValue(((TerrainLayer) world.layers.get(selectedLayerIndex)).getWrap());
                invertCheck.setChecked(((TerrainLayer) world.layers.get(selectedLayerIndex)).isInverted());
                break;
            case 'M':

                break;
        }

    }

    protected void doLayerPanel() {
        for (int i = world.layers.size() - 1; i >= 0; i--) {
            layerGroup.addActor(createLayerWidget((world.layers.get(i))));
        }

        HorizontalGroup layerFunctions = new HorizontalGroup();
        layerFunctions.space(2);
        layerFunctions.pad(4);
        layerFunctions.align(Align.center);

        SelectBox tilesetSelect = new SelectBox(skin);
        tilesetSelect.setItems(tilesets.keySet().toArray());

        // make it so that dialog calls adding of the new thingamabob. cool. goodnight.

        TextButton addTerrainLayer = new TextButton("+T", skin);
        addTerrainLayer.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                addLayer('T');
                return true;
            }
        });

        TextButton addMazeLayer = new TextButton("+M", skin);
        addMazeLayer.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                addLayer('M');
                return true;
            }
        });

        TextButton removeLayer = new TextButton("-", skin);
        removeLayer.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (world.layers.size() > 1) {
                    world.layers.remove(selectedLayerIndex);
                    layersChanged = true;
                    if (selectedLayerIndex != 0) {
                        selectedLayerIndex = selectedLayerIndex - 1;
                    } else {
                        selectedLayerIndex = 0;
                    }
                }

                return true;
            }
        });

        layerFunctions.addActor(addTerrainLayer);
        layerFunctions.addActor(addMazeLayer);
        layerFunctions.addActor(removeLayer);

        layerGroup.addActor(layerFunctions);

        layerGroup.addActor(selectedLayerLabel);

        layerPanel.setPosition(Gdx.graphics.getWidth() - layerPanel.getPrefWidth() - 5, Gdx.graphics.getHeight() - 400f);
        layerPanel.setSize(layerPanel.getPrefWidth(), layerPanel.getPrefHeight());
    }

    void addLayer(char type) {
        Dialog addLayerPopup;
        final SelectBox tilesetSelect = new SelectBox(skin);
        tilesetSelect.setItems(tilesets.keySet().toArray());
        final CheckBox useDefaults = new CheckBox("", skin);
        switch (type) {
            case 'T':
                addLayerPopup = new Dialog("New terrain layer", skin) {
                    public void result(Object obj) {
                        if (obj.equals(true)) {
                            TerrainLayer terrainLayer = new TerrainLayer(world.seed);
                            if (!useDefaults.isChecked()) {
                                terrainLayer.tilesetName = (String) tilesetSelect.getSelected();
                                terrainLayer.tileSpecs = new ArrayList<>();
                                terrainLayer.tileSpecs.add(new Tileset.TerrainTileSpec(tilesets.get(terrainLayer.tilesetName).defaultTile));
                            }
                            world.layers.add(terrainLayer);
                            selectedLayerIndex = selectedLayerIndex + 1;
                            layersChanged = true;
                        }
                    }
                };
                addLayerPopup.button("OK", true);
                addLayerPopup.button("Cancel", false);
                addLayerPopup.add(tilesetSelect);
                addLayerPopup.text("Use default settings? (this will override tileset too)");
                addLayerPopup.add(useDefaults);

                addLayerPopup.show(stage);
                break;
            case 'M':
                addLayerPopup = new Dialog("New maze layer", skin) {
                    public void result(Object obj) {
                        if (obj.equals(true)) {
                            MazeLayer mazeLayer = new MazeLayer(world.seed);
                            if (!useDefaults.isChecked()) {
                                mazeLayer.tilesetName = (String) tilesetSelect.getSelected();
                                mazeLayer.tileSpecs = new ArrayList<>();
                                mazeLayer.tileSpecs.add(new Tileset.MazeTileSpec(tilesets.get(mazeLayer.tilesetName).defaultTile));
                            }
                            world.layers.add(mazeLayer);
                            selectedLayerIndex = selectedLayerIndex + 1;
                            layersChanged = true;
                        }
                    }
                };
                addLayerPopup.button("OK", true);
                addLayerPopup.button("Cancel", false);
                addLayerPopup.add(tilesetSelect);
                addLayerPopup.text("Use default settings? (this will override tileset too)");
                addLayerPopup.add(useDefaults);

                addLayerPopup.show(stage);
                break;
        }


    }

    protected void updateLayerPanel() {

        // only make updates to the layers if anything has been edited, otherwise it's unnecessary as there's no change
        if (layersChanged) {
            tileDataChanged = true;
            layerGroup.clear();
            doLayerPanel();

            layersChanged = false;
        }

        selectedLayerLabel.setText("The currently selected layer is '" + world.layers.get(selectedLayerIndex).getName() + "'.");
    }

    protected HorizontalGroup createLayerWidget(final Layer layer) {
        HorizontalGroup layerGroup = new HorizontalGroup();
        layerGroup.space(4);
        layerGroup.pad(8);

        TextButton select = new TextButton("*", skin);
        TextButton moveUp = new TextButton("^", skin);
        TextButton moveDown = new TextButton("v", skin);
        TextButton showOrHide = new TextButton("[show/hide]", skin);

        showOrHide.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                layer.showLayer(!layer.layerShown());
                return true;

            }
        });

        select.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                for (int i = 0; i < world.layers.size(); i++) {
                    if (Objects.equals(world.layers.get(i), layer)) {
                        AppUI.selectedLayerIndex = i;
                    }
                }
                return true;
            }
        });

        moveUp.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                int layerIndex = world.layers.indexOf(layer);
                if (layerIndex < world.layers.size() - 1) {
                    layersChanged = true;
                    world.swapLayers(layerIndex, layerIndex + 1);
                }
                return true;
            }
        });

        moveDown.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                int layerIndex = world.layers.indexOf(layer);
                if (layerIndex > 0) {
                    layersChanged = true;
                    world.swapLayers(layerIndex - 1, layerIndex);
                }
                return true;
            }
        });

        layerGroup.addActor(moveUp);
        layerGroup.addActor(moveDown);

        final TextField nameField = new TextField(layer.getName(), skin);
        nameField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField field, char c) {
                layer.setName(nameField.getText());
            }
        });

        layerGroup.addActor(nameField);

        layerGroup.addActor(showOrHide);
        layerGroup.addActor(select);

        return layerGroup;
    }

    protected void doTilePanel() {
        tilePanel.add(selectedLayerTilesetLabel);
        tilePanel.row();

        /*hueSlider.setValue((world.layers.get(selectedLayerIndex)).hueShift);
        hueSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                (world.layers.get(selectedLayerIndex)).hueShift = (hueSlider.getValue());
            }
        });

        tilePanel.add(hueSlider);*/

        genTileList();
        tilePanel.add(tiles);
        tilePanel.row();

        TextButton sortTilesButton = new TextButton("[update]", skin);
        sortTilesButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                tiles.clear();
                genTileList();
                return false;
            }
        });
        tilePanel.add(sortTilesButton);

        tilePanel.setSize(tilePanel.getPrefWidth() * 1.2f, tilePanel.getPrefHeight());
    }

    public static void genTileList() {
        //sort tiles
        world.layers.get(selectedLayerIndex).sortTileSpecs();

        // in the list of tilechildren, create a widget for each tile
        switch (getLayerTypeChar(world.layers.get(selectedLayerIndex))) {
            case 'T':
                for (int i = 0; i < ((TerrainLayer) world.layers.get(selectedLayerIndex)).tileSpecs.size(); i++) {
                    tiles.addActor(createTileLine(tilesets.get(world.layers.get(selectedLayerIndex).tilesetName), i));
                }
                break;
            case 'M':
                for (int i = 0; i < ((MazeLayer) world.layers.get(selectedLayerIndex)).tileSpecs.size(); i++) {
                    tiles.addActor(createTileLine(tilesets.get(world.layers.get(selectedLayerIndex).tilesetName), i));
                }
                break;
        }
    }

    protected void updateTilePanel() {
        selectedLayerTilesetLabel.setText("Using: " + world.layers.get(selectedLayerIndex).tilesetName);
        // only make updates to the tiles if anything has been edited, otherwise it's unnecessary as there's no change

        if (layersChanged) {
            System.out.println("Yeah.");


            layersChanged = false;
        }

        if (tileDataChanged) {
            tiles.clear();
            genTileList();
            for (int i = 0; i < tiles.getChildren().size; i++) {
                switch (getLayerTypeChar(world.layers.get(selectedLayerIndex))) {
                    case 'T':
                        Tileset.TerrainTileSpec terrainTile = ((TerrainLayer) world.layers.get(selectedLayerIndex)).tileSpecs.get(i);
                        ((Label) ((HorizontalGroup) tiles.getChild(i)).getChild(1)).setText("name: " + terrainTile.name + ", thresh: " + floatFormat.format(terrainTile.lowerBound));
                        break;
                    case 'M':
                        Tileset.MazeTileSpec mazeTile = ((MazeLayer) world.layers.get(selectedLayerIndex)).tileSpecs.get(i);
                        ((Label) ((HorizontalGroup) tiles.getChild(i)).getChild(1)).setText("name: " + mazeTile.name + ", orientation: " + floatFormat.format(mazeTile.orientationID));
                        break;
                }


            }

            /*if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) || updateTileList) {
                tilePanel.clear();
                genTileList();
                updateTileList = false;
            } else {
                updateTileList = true;
            }*/

            tileDataChanged = false;
        }
        //hueSlider.setValue((world.layers.get(selectedLayerIndex)).hueShift);
    }

    private static HorizontalGroup createTileLine(Tileset tileset, final int selectedTile) {
        HorizontalGroup tileDataGroup = new HorizontalGroup();
        tileDataGroup.space(4);
        tileDataGroup.pad(2);

        switch (getLayerTypeChar(world.layers.get(selectedLayerIndex))) {
            case 'T':
                final Tileset.TerrainTileSpec terrainTile = ((TerrainLayer) world.layers.get(selectedLayerIndex)).tileSpecs.get(selectedTile);

                TextureRegionDrawable terrainTileImgDrawable = new TextureRegionDrawable(tileset.getTileTextureFromName(terrainTile.name));
                terrainTileImgDrawable.setMinSize(terrainTileImgDrawable.getMinWidth() * 2, terrainTileImgDrawable.getMinHeight() * 2);
                Image terrainTileImg = new Image(terrainTileImgDrawable);
                tileDataGroup.addActor(terrainTileImg);

                Label terrainTileName = new Label("name: " + terrainTile.name + ", thresh: " + floatFormat.format(terrainTile.lowerBound), skin);
                tileDataGroup.addActor(terrainTileName);

                final Slider lowerBoundSlider = new Slider(0, 1, 0.01f, false, skin);
                lowerBoundSlider.setValue(terrainTile.lowerBound);
                lowerBoundSlider.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        ((TerrainLayer) world.layers.get(selectedLayerIndex)).tileSpecs.set(selectedTile, new Tileset.TerrainTileSpec(terrainTile.name, lowerBoundSlider.getValue()));
                    }
                });

                tileDataGroup.addActor(lowerBoundSlider);
                break;
            case 'M':
                final Tileset.MazeTileSpec mazeTile = ((MazeLayer) world.layers.get(selectedLayerIndex)).tileSpecs.get(selectedTile);

                TextureRegionDrawable mazeTileImgDrawable = new TextureRegionDrawable(tileset.getTileTextureFromName(mazeTile.name));
                mazeTileImgDrawable.setMinSize(mazeTileImgDrawable.getMinWidth() * 2, mazeTileImgDrawable.getMinHeight() * 2);
                Image mazeTileImg = new Image(mazeTileImgDrawable);
                tileDataGroup.addActor(mazeTileImg);

                Label mazeTileName = new Label("name: " + mazeTile.name + ", orientation: " + intFormat.format(mazeTile.orientationID), skin);
                tileDataGroup.addActor(mazeTileName);
                break;
        }


        return tileDataGroup;
    }
}