package grash.editor;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.editor.GrashEvent_ContentModified;
import grash.event.events.editor.GrashEvent_SelectionChanged;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.level.map.LevelMapEffect;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapNote;
import grash.level.map.LevelMapThing;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

public class EditorEditController implements GrashEventListener {

    private final GameController game;
    private final EditorController editorController;

    private GridPane editPane;
    private GridPane defaultEditPane;
    private boolean isEditing;

    private final UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
        String newText = change.getControlNewText();
        return newText.matches("-?\\d*(\\.\\d*)?") ? change : null;
    };


    public EditorEditController(GameController game, EditorController editorController) {
        this.game = game;
        this.editorController = editorController;
        this.isEditing = false;

        game.getEventBus().registerListener(GrashEvent_SelectionChanged.class, this);
        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
        game.getEventBus().registerListener(GrashEvent_ContentModified.class, this);
    }

    public boolean isEditing() {
        return isEditing;
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "SelectionChanged": {
                generateObjectEditFields(((GrashEvent_SelectionChanged) event).getThing());
                break;
            }
            case "KeyDown": {
                onEvent_KeyDown((GrashEvent_KeyDown) event);
                break;
            }
            case "ContentModified": {
                generateObjectEditFields(((GrashEvent_ContentModified) event).getModifiedThing());
                break;
            }
        }
    }

    public void setup() {
        this.editPane = (GridPane) game.getPrimaryStage().getScene().lookup("#editPane");
        this.defaultEditPane = (GridPane) game.getPrimaryStage().getScene().lookup("#defaultEditPane");
        this.isEditing = false;

        generateDefaultEditFields();
    }

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        if(editorController.getEditorState() == EditorState.inactive) return;

        if(event.getKeyCode() == KeyCode.Q) {
            toggleEditMode();
        }
    }

    public void generateObjectEditFields(LevelMapThing targetObject) {
        editPane.getChildren().clear();
        if(targetObject == null) return;

        switch (targetObject.getMapThingType()) {
            case Element -> {
                LevelMapElement element = (LevelMapElement) targetObject;
                createEditPanelHeader(element.getMapElementType().toString());
                createField("thingStartTime", "Time", doubleFilter, Double.toString(element.getTimeStart()), 1);
                createField("elementEndTime", "End-Time", doubleFilter, Double.toString(element.getTimeEnd()), 2);
                createField("elementHeight", "Height", doubleFilter, Double.toString(element.getHeightNormalized()), 3);
                createField("elementIsUp", "Up", null, Boolean.toString(element.getIsUp()), 4);
            }
            case Note -> {
                LevelMapNote note = (LevelMapNote) targetObject;
                createEditPanelHeader(note.getMapNoteType().toString());
                createField("thingStartTime", "Time", doubleFilter, Double.toString(note.getTimeStart()), 1);
                createField("noteEndTime", "End-Time", doubleFilter, Double.toString(note.getTimeEnd()), 2);
                createField("noteYType", "Y-Type", null, Integer.toString(note.getYType()), 3);
            }
            case Effect -> {
                LevelMapEffect effect = (LevelMapEffect) targetObject;
                createEditPanelHeader(effect.getMapEffectType().toString());
                createField("thingStartTime", "Time", doubleFilter, Double.toString(effect.getTimeStart()), 1);
                createField("effectColorR", "R", doubleFilter, Double.toString(effect.getColor().getRed() * 255), 2);
                createField("effectColorG", "G", doubleFilter, Double.toString(effect.getColor().getGreen() * 255), 3);
                createField("effectColorB", "B", doubleFilter, Double.toString(effect.getColor().getBlue() * 255), 4);
                createField("effectValueDouble", "Double Value", doubleFilter, Double.toString(effect.getValueDouble()), 5);
                createField("effectValueInteger", "Integer Value", doubleFilter, Double.toString(effect.getValueInteger()), 6);
            }
        }
    }

    private void generateDefaultEditFields() {
        TextField timeField = (TextField) game.getPrimaryStage().getScene().lookup("#timeField");
        timeField.setOnAction(e -> {handleNewInput("time", timeField.getText());});
        timeField.setTextFormatter(new TextFormatter<>(doubleFilter));
        timeField.setDisable(true);

        TextField swipeField = (TextField) game.getPrimaryStage().getScene().lookup("#swipeField");
        swipeField.setOnAction(e -> {handleNewInput("swipe", swipeField.getText());});
        swipeField.setTextFormatter(new TextFormatter<>(doubleFilter));
        swipeField.setDisable(true);

        TextField moveField = (TextField) game.getPrimaryStage().getScene().lookup("#moveField");
        moveField.setOnAction(e -> {handleNewInput("move", moveField.getText());});
        moveField.setTextFormatter(new TextFormatter<>(doubleFilter));
        moveField.setDisable(true);

        //editPane.requestFocus(); // Deselect any InputField
    }

    private void createEditPanelHeader(String text) {
        Label headerLabel = new Label();
        headerLabel.setText(text);

        editPane.add(headerLabel, 0, 0);
    }

    private void createField(String type, String infoText, UnaryOperator<TextFormatter.Change> inputFilter,
                             String defaultValue, int rowIndex) {
        TextField newField = new TextField();
        Label newLabel = new Label();

        newField.setPromptText("Enter Value");
        newField.setText(defaultValue);
        newField.setTextFormatter(new TextFormatter<>(inputFilter));
        newField.setOnAction(e -> {handleNewInput(type, newField.getText());});
        newField.setDisable(!isEditing);
        newField.setEditable(isEditing);

        newLabel.setText(infoText);

        editPane.add(newLabel, 0, rowIndex);
        editPane.add(newField, 1, rowIndex);
    }

    private void toggleEditMode() {
        this.isEditing = !isEditing;

        setTextFieldsDisable(editPane.getChildren(), !isEditing);
        setTextFieldsDisable(defaultEditPane.getChildren(), !isEditing);

        game.getPrimaryStage().requestFocus();
    }

    private void handleNewInput(String type, String value) {
        System.out.println(type + " - " + value);
    }

    private void setTextFieldsDisable(ObservableList<Node> nodes, boolean disable) {
        for(Node node : nodes) {
            if(node.getClass() == TextField.class) {
                node.setDisable(disable);
                ((TextField) node).setEditable(!disable);
            }
        }
    }
}
