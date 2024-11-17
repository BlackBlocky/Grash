package grash.assets;

import java.nio.file.Path;

public class MapMetadata {
    private String mapName;
    private String mapAuthor;
    private String mapVersion;
    private String songName;

    private Path folderPath;
    private String fileName;

    public MapMetadata(String mapName, String mapAuthor, String mapVersion, String songName, Path folderPath, String fileName) {
        this.mapName = mapName;
        this.mapAuthor = mapAuthor;
        this.mapVersion = mapVersion;
        this.songName = songName;
        this.folderPath = folderPath;
        this.fileName = fileName;
    }

    public String getSongName() {
        return this.songName;
    }
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
}
