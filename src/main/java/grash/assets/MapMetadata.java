package grash.assets;

import java.nio.file.Path;

public final class MapMetadata {
    private final String mapName;
    private final String mapAuthor;
    private final String mapVersion;
    private final String songName;

    private final Path folderPath;
    private final String fileName;

    private final String mapKey;

    public MapMetadata(String mapName, String mapAuthor, String mapVersion,
                       String songName, Path folderPath, String fileName) {
        this.mapName = mapName;
        this.mapAuthor = mapAuthor;
        this.mapVersion = mapVersion;
        this.songName = songName;
        this.folderPath = folderPath;
        this.fileName = fileName;

        this.mapKey = mapAuthor + "::" + mapName + "::" + mapVersion;
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
