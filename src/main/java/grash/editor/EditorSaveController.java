package grash.editor;

import grash.core.GameController;
import grash.core.Main;
import grash.level.map.LevelMapEffect;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapNote;
import grash.level.map.LevelMapThing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class EditorSaveController {

    private final GameController game;
    private final EditorController editorController;

    public EditorSaveController(GameController game, EditorController editorController) {
        this.game = game;
        this.editorController = editorController;
    }

    public boolean saveEditorMapData(final EditorMapData editorMapData) {
        if(editorMapData == null) return false;

        return saveAtLocation(editorMapData,
                editorMapData.mapMetadata.getFolderPath().toString(),
                editorMapData.mapMetadata.getMapName());
    }

    public boolean saveAtLocation(final EditorMapData editorMapData, final String path, final String mapName) {
        if(editorMapData == null || path == null || mapName == null) return false;

        // Generate the ML
        String generatedML = "";
        try {
            generatedML = generateMapML(editorMapData);
            createOrOverwriteFile(path + "/" + mapName + ".grashMap", generatedML);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void createOrOverwriteFile(String filePath, String mlContent) throws IOException {
        File file = new File(filePath);

        FileWriter writer = new FileWriter(file, false);
        writer.append(mlContent);
        writer.close();
    }

    private static String generateMapML(final EditorMapData editorMapData) {
        StringBuilder builder = new StringBuilder();

        // Create all Lines
        // Metadata
        builder.append(generateOneParamMLLine("loader", Main.MAP_LOADER_VERSION)).append("\n");
        builder.append(generateOneParamMLLine("mapname", editorMapData.mapMetadata.getMapName())).append("\n");
        builder.append(generateOneParamMLLine("difficulty", editorMapData.mapMetadata.getMapDifficulty())).append("\n");
        builder.append(generateOneParamMLLine("mapauthor", editorMapData.mapMetadata.getMapAuthor())).append("\n");
        builder.append(generateOneParamMLLine("songname", editorMapData.mapMetadata.getSongName())).append("\n");
        builder.append(generateOneParamMLLine("songauthor", editorMapData.mapMetadata.getSongAuthor())).append("\n");
        builder.append(generateOneParamMLLine("mapversion", editorMapData.mapMetadata.getMapVersion())).append("\n");
        builder.append(generateOneParamMLLine("songbpm", Double.toString(editorMapData.mapMetadata.getSongBPM()))).append("\n");
        builder.append(generateOneParamMLLine("speed", Double.toString(editorMapData.speed))).append("\n");
        builder.append(generateOneParamMLLine("growspeed", Double.toString(editorMapData.growspeed))).append("\n");
        builder.append(generateOneParamMLLine("startcolor",
                Double.toString(to255Range(editorMapData.startColor.getRed())) + ", " +
                Double.toString(to255Range(editorMapData.startColor.getGreen())) + ", " +
                Double.toString(to255Range(editorMapData.startColor.getBlue()))
        )).append("\n");
        builder.append(generateOneParamMLLine("startfovscale", Double.toString(editorMapData.startFOVScale))).append("\n");
        builder.append(generateOneParamMLLine("startrotation", Double.toString(editorMapData.startRotation))).append("\n");

        // Obstacles
        builder.append(generateMLLine("spike", editorMapData.spikes,
                EditorSaveController::getElementIsUp,
                EditorSaveController::getThingStartTime
        )).append("\n");
        builder.append(generateMLLine("slide", editorMapData.slides,
                EditorSaveController::getElementIsUp,
                EditorSaveController::getThingStartTime,
                EditorSaveController::getElementEndTime
        )).append("\n");
        builder.append(generateMLLine("wall", editorMapData.walls,
                EditorSaveController::getElementIsUp,
                EditorSaveController::getThingStartTime
        )).append("\n");
        builder.append(generateMLLine("doublejump", editorMapData.doubleJumps,
                EditorSaveController::getElementHeightY,
                EditorSaveController::getThingStartTime
        )).append("\n");
        builder.append(generateMLLine("rope", editorMapData.ropes,
                EditorSaveController::getElementHeightY,
                EditorSaveController::getThingStartTime,
                EditorSaveController::getElementEndTime
        )).append("\n");

        // Notes
        builder.append(generateMLLine("tapnote", editorMapData.tapNotes,
                EditorSaveController::getNoteYType,
                EditorSaveController::getThingStartTime
        )).append("\n");
        builder.append(generateMLLine("grownote", editorMapData.growNotes,
                EditorSaveController::getNoteIsLeft,
                EditorSaveController::getThingStartTime
        )).append("\n");
        builder.append(generateMLLine("slidenote", editorMapData.slideNotes,
                EditorSaveController::getNoteYType,
                EditorSaveController::getThingStartTime,
                EditorSaveController::getNoteEndTime
        )).append("\n");

        // Effects
        builder.append(generateMLLine("color", editorMapData.colors,
                EditorSaveController::getEffectRed,
                EditorSaveController::getEffectGreen,
                EditorSaveController::getEffectBlue,
                EditorSaveController::getThingStartTime
        )).append("\n");
        builder.append(generateMLLine("fovscale", editorMapData.fovScales,
                EditorSaveController::getEffectDouble,
                EditorSaveController::getThingStartTime
        )).append("\n");
        builder.append(generateMLLine("rotate", editorMapData.rotates,
                EditorSaveController::getEffectDouble,
                EditorSaveController::getThingStartTime
        )).append("\n");
        builder.append(generateMLLine("lasershow", editorMapData.lasershows,
                EditorSaveController::getEffectDouble,
                EditorSaveController::getThingStartTime
        )).append("\n");

        return builder.toString();
    }

    private static String getThingStartTime(LevelMapThing t) { return Double.toString(t.getTimeStart()); }

    private static String getElementEndTime(LevelMapThing t) { return Double.toString(((LevelMapElement) t).getTimeEnd()); }
    private static String getElementIsUp(LevelMapThing t) { return Boolean.toString(((LevelMapElement) t).getIsUp()); }
    private static String getElementHeightY(LevelMapThing t) { return Double.toString(((LevelMapElement) t).getHeightNormalized()); }

    private static String getNoteYType(LevelMapThing t) { return Byte.toString(((LevelMapNote) t).getYType()); }
    private static String getNoteIsLeft(LevelMapThing t) { return Boolean.toString(((LevelMapNote) t).getIsLeft()); }
    private static String getNoteEndTime(LevelMapThing t) { return Double.toString(((LevelMapNote) t).getTimeEnd()); }

    private static String getEffectRed(LevelMapThing t) { return Double.toString(to255Range(((LevelMapEffect) t).getColor().getRed())); }
    private static String getEffectGreen(LevelMapThing t) { return Double.toString(to255Range(((LevelMapEffect) t).getColor().getGreen())); }
    private static String getEffectBlue(LevelMapThing t) { return Double.toString(to255Range(((LevelMapEffect) t).getColor().getBlue())); }
    private static double to255Range(double d) { return Math.round(d * 255.0 * 100.0) / 100.0; }

    private static String getEffectDouble(LevelMapThing t) { return Double.toString(((LevelMapEffect) t).getValueDouble()); }
    private static String getEffectInteger(LevelMapThing t) { return Integer.toString(((LevelMapEffect) t).getValueInteger()); }

    public static String generateOneParamMLLine(final String prefix, final String value) {
        return "[" + prefix + "]{(" + value + ")}";
    }

    @SafeVarargs
    private static String generateMLLine(final String prefix, final List<? extends LevelMapThing> list,
                                  final Function<LevelMapThing, String>... getter) {
        // Check if you can work the List
        if(list == null || list.isEmpty()) {
            return "[" + prefix + "]{}";
        }

        // Generate the Line
        StringBuilder builder = new StringBuilder("[" + prefix + "]{");

        // Add every Element in the List with the Following format: "(getterVal1, getterVal2, ...)[,]"
        for(LevelMapThing thing : list) {
            builder.append("(");
            // Process all Getter Functions
            for(Function<LevelMapThing, String> f : getter) {
                builder.append(f.apply(thing)).append(",");
            }

            builder.setLength(builder.length() - 1); // Remove the last ','
            builder.append(")");
        }

        return builder.toString() + "}";
    }
}
