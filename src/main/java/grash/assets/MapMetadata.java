package grash.assets;

import javafx.scene.media.Media;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public final class MapMetadata {
    private final String mapName;
    private final String mapDifficulty;
    private final String mapAuthor;
    private final String mapVersion;
    private final String songName;
    private final String songAuthor;
    private final double songBPM;

    private final Path folderPath;
    private final String fileName;

    private final String mapKey;
    private final String mapGroupKey;

    private final Media songMetadata;

    public MapMetadata(String mapName, String mapDifficulty, String mapAuthor, String mapVersion,
                       String songName, String songAuthor, String songBPM, Path folderPath, String fileName) {
        this.mapName = mapName;
        this.mapDifficulty = mapDifficulty;
        this.mapAuthor = mapAuthor;
        this.mapVersion = mapVersion;
        this.songName = songName;
        this.songAuthor = songAuthor;
        this.songBPM = Double.parseDouble(songBPM);
        this.folderPath = folderPath;
        this.fileName = fileName;

        this.mapKey = mapAuthor + "::" + mapName + "::" + songName + "::" + songAuthor + "::" + mapVersion + "::" + mapDifficulty;
        this.mapGroupKey = mapAuthor + "::" + mapName + "::" + songName + "::" + songAuthor + "::" + mapVersion;

        // Load the Song
        songMetadata = loadSongMetaData(this.folderPath.toString());
    }

    public String getSongName() {
        return this.songName;
    }
    public String getSongAuthor() { return this.songAuthor; }
    public String getMapVersion() {
        return this.mapVersion;
    }
    public String getMapAuthor() {
        return this.mapAuthor;
    }
    public String getMapName() {
        return this.mapName;
    }
    public Path getFolderPath() {
        return folderPath;
    }
    public String getFileName() {
        return this.fileName;
    }

    public String getMapDifficulty() {
        return mapDifficulty;
    }

    public String getMapGroupKey() {
        return mapGroupKey;
    }

    public String getMapKey() { return this.mapKey; }

    public Media getSongMetadata() { return this.songMetadata; }

    /**
     * Returns null if there is no song
     */
    private static Media loadSongMetaData(String targetPath) {
        try {
            File songFile = new File(targetPath + "\\song.mp3");
            if(!songFile.exists())
                throw new IOException("Cant find the songFile inside the Map Folder: \"" + targetPath + "\"");

            return new Media(songFile.toURI().toString());
        } catch (Exception e) {
            return null;
        }
    }

}
