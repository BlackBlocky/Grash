package grash.assets;

public class MapMetadata {
    private String loaderVersion;
    private String mapName;
    private String mapAuthor;
    private String mapVersion;
    private String songName;

    public MapMetadata(String loaderVersion, String mapName, String mapAuthor, String mapVersion, String songName) {
        this.loaderVersion = loaderVersion;
        this.mapName = mapName;
        this.mapAuthor = mapAuthor;
        this.mapVersion = mapVersion;
        this.songName = songName;
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
    public String getLoaderVersion() {
        return this.loaderVersion;
    }
}
