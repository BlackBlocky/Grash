package grash.ui;

import grash.core.GameController;
import grash.core.Main;
import grash.core.WindowState;
import grash.editor.EditorSaveController;
import grash.event.events.core.GrashEvent_ReloadMaps;
import grash.event.events.level.GrashEvent_LoadLevel;
import grash.event.events.scene.GrashEvent_SwitchScene;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.converter.DoubleStringConverter;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class EditorSelectorScreenController extends ScreenController{
    private GameController game;

    private ListView<Text> mapsList;
    private VBox newMapPanel;

    private Button goBackButton;
    private Button loadSelectedMapButton;
    private Button openMapCreatorButton;
    private Button createNewMapButton;

    private TextField mapNameField;
    private TextField difficultyField;
    private TextField mapAuthorField;
    private TextField songNameField;
    private TextField songAuthorField;
    private TextField songBpmField;
    private TextField mapSpeedField;
    private TextField growSpeedField;

    public EditorSelectorScreenController(GameController gameController) {
        this.game = gameController;
    }

    @Override
    public void init() {
        setupReferences();
        setupLevelsList();
        setupButtons();
        setupTextFields();

        newMapPanel.setVisible(false);
    }

    @Override
    public void close() {

    }

    private void setupReferences() {
        Scene scene = game.getPrimaryStage().getScene();

        mapsList = lookup(scene, "#mapsList", ListView.class);
        newMapPanel = lookup(scene, "#newMapPanel", VBox.class);

        goBackButton = lookup(scene, "#goBackButton", Button.class);
        loadSelectedMapButton = lookup(scene, "#loadSelectedMapButton", Button.class);
        openMapCreatorButton = lookup(scene, "#openMapCreatorButton", Button.class);
        createNewMapButton = lookup(scene, "#createNewMapButton", Button.class);

        mapNameField = lookup(scene, "#mapNameInput", TextField.class);
        difficultyField = lookup(scene, "#difficultyInput", TextField.class);
        mapAuthorField = lookup(scene, "#mapAuthorInput", TextField.class);
        songNameField = lookup(scene, "#songNameInput", TextField.class);
        songAuthorField = lookup(scene, "#songAuthorInput", TextField.class);
        songBpmField = lookup(scene, "#songBpmInput", TextField.class);
        mapSpeedField = lookup(scene, "#mapSpeedInput", TextField.class);
        growSpeedField = lookup(scene, "#growSpeedInput", TextField.class);
    }
    private <T> T lookup(Scene scene, String selector, Class<T> type) {
        Node node = scene.lookup(selector);
        return type.isInstance(node) ? type.cast(node) : null;
    }

    private void setupLevelsList() {
        // Clear the List
        mapsList.getItems().clear();

        // Generate the new Elements
        String[] allMapKeysClone = game.getResourceLoader().getAllMapKeys().clone();
        Arrays.sort(allMapKeysClone);
        for(String key : allMapKeysClone) {
            Text newListElement = new Text();
            newListElement.setText(key);
            newListElement.getStyleClass().add("map-list-text");
            mapsList.getItems().add(newListElement);
        }
    }

    private void setupButtons() {
        goBackButton.setOnMousePressed(mouseEvent -> {
            game.getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.WelcomeScreen));
        });
        openMapCreatorButton.setOnMousePressed(mouseEvent -> {
            newMapPanel.setVisible(true);
        });

        createNewMapButton.setOnMousePressed(mouseEvent -> {
            button_createNewMap();
        });
        loadSelectedMapButton.setOnMousePressed(mouseEvent -> {
            button_loadSelectedMap();
        });
    }

    private void setupTextFields() {
        UnaryOperator<TextFormatter.Change> spaceFilter = change -> {
            change.setText(change.getText().replace(" ", "_"));
            if(change.getText().matches(".*[\\{\\[\\(\\)\\]},].*")) return null;
            return change;
        };
        mapNameField.setTextFormatter(new TextFormatter<>(spaceFilter));
        difficultyField.setTextFormatter(new TextFormatter<>(spaceFilter));
        mapAuthorField.setTextFormatter(new TextFormatter<>(spaceFilter));
        songNameField.setTextFormatter(new TextFormatter<>(spaceFilter));
        songAuthorField.setTextFormatter(new TextFormatter<>(spaceFilter));

        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("-?\\d*(\\.\\d*)?") ? change : null;
        };
        songBpmField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter));
        mapSpeedField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter));
        growSpeedField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), 0.0, doubleFilter));
    }

    private void button_createNewMap() {
        if(!checkIfEverythingInFormulaIsFilled()) return;

        String mapName = mapNameField.getText();
        String difficulty = difficultyField.getText();
        String mapAuthor = mapAuthorField.getText();
        String songName = songNameField.getText();
        String songAuthor = songAuthorField.getText();

        double songBpm = Double.parseDouble(songBpmField.getText());
        double mapSpeed = Double.parseDouble(mapSpeedField.getText());
        double growSpeed = Double.parseDouble(growSpeedField.getText());

        StringBuilder builder = new StringBuilder();
        builder.append(EditorSaveController.generateOneParamMLLine("loader", Main.MAP_LOADER_VERSION)).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("mapname", mapName)).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("difficulty", difficulty)).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("mapauthor", mapAuthor)).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("songname", songName)).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("songauthor", songAuthor)).append("\n");

        builder.append(EditorSaveController.generateOneParamMLLine("mapversion", "1.0")).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("songbpm", Double.toString(songBpm))).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("speed", Double.toString(mapSpeed))).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("growspeed", Double.toString(growSpeed))).append("\n");

        builder.append(EditorSaveController.generateOneParamMLLine("startcolor", "0, 0, 0")).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("startfovscale", "1.0")).append("\n");
        builder.append(EditorSaveController.generateOneParamMLLine("startrotation", "0.0"));

        // Create the new Map-File
        String folderName = mapAuthor + "-" + mapName;
        String fileName = mapName + ".grashMap";

        File newMapFile = new File(Main.WORKING_DIRECTORY + "/assets/maps/" + folderName + "/" + fileName);

        if(newMapFile.exists()) {
            System.out.println("The Map: \"" + newMapFile.getAbsolutePath() + "\" already exists!");
            return;
        }

        try {
            new File(Main.WORKING_DIRECTORY + "/assets/maps/" + folderName).mkdir();
            newMapFile.createNewFile();
            FileWriter writer = new FileWriter(newMapFile, false);
            writer.write(builder.toString());
            writer.close();

            System.out.println("Successfully created: " + newMapFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("An Error happened creating the new Map!");
            e.printStackTrace();
        }

        game.getEventBus().triggerEvent(new GrashEvent_ReloadMaps());
        game.getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.EditorSelector));
    }
    private void button_loadSelectedMap() {
        if(mapsList.getSelectionModel().isEmpty()) return;

        String selectedMapKey = mapsList.getSelectionModel().getSelectedItem().getText();

        game.getEventBus().triggerEvent(new GrashEvent_LoadLevel(selectedMapKey));
    }

    private boolean checkIfEverythingInFormulaIsFilled() {
        return Stream.of (
                mapNameField, difficultyField, mapAuthorField, songNameField,
                songAuthorField, songBpmField, mapSpeedField, growSpeedField
        ).noneMatch(field -> field.getText().isEmpty());
    }
}
