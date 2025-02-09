package grash.assets;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

final class MapLoader {

    public MapLoader() {

    }

    /**
     * This Method the MapMetadata on the specific Path.
     */
    public MapMetadata loadMapMetadata(Path folderPath, String grashMapFileName) {
        String mapMl = generateMapML(Paths.get(folderPath.toAbsolutePath() + "\\" + grashMapFileName));

        String[][] mapName = extractParamsFromMapML(mapMl, "mapname");
        String[][] mapDifficulty = extractParamsFromMapML(mapMl, "difficulty");
        String[][] mapAuthor = extractParamsFromMapML(mapMl, "mapauthor");
        String[][] songName = extractParamsFromMapML(mapMl, "songname");
        String[][] songAuthor = extractParamsFromMapML(mapMl, "songauthor");
        String[][] mapVersion = extractParamsFromMapML(mapMl, "mapversion");

        return new MapMetadata(mapName[0][0], mapDifficulty[0][0], mapAuthor[0][0], mapVersion[0][0], songName[0][0],
                songAuthor[0][0], folderPath, grashMapFileName);
    }

    /**
     * This Method loads all the MapData that still need to be loaded.
     * It does not need a Path because the Path if already stored in the MapMetaData
     */
    public MapData loadMap(MapMetadata mapMetadata) {
        String mapMl = generateMapML(Paths.get(mapMetadata.getFolderPath() + "\\" + mapMetadata.getFileName()));

        String[][] mapSpeed = extractParamsFromMapML(mapMl, "speed");
        String[][] mapGrowspeed = extractParamsFromMapML(mapMl, "growspeed");
        String[][] mapStartcolor = extractParamsFromMapML(mapMl, "startcolor");
        String[][] mapStartfovscale = extractParamsFromMapML(mapMl, "startfovscale");
        String[][] mapStartrotation = extractParamsFromMapML(mapMl, "startrotation");

        String[][] mapSpike = extractParamsFromMapML(mapMl, "spike");
        String[][] mapSlide = extractParamsFromMapML(mapMl, "slide");
        String[][] mapWall = extractParamsFromMapML(mapMl, "wall");
        String[][] mapDoublejump = extractParamsFromMapML(mapMl, "doublejump");
        String[][] mapRope = extractParamsFromMapML(mapMl, "rope");

        String[][] mapTapnote = extractParamsFromMapML(mapMl, "tapnote");
        String[][] mapGrownote = extractParamsFromMapML(mapMl, "grownote");
        String[][] mapSlidenote = extractParamsFromMapML(mapMl, "slidenote");

        String[][] mapColor = extractParamsFromMapML(mapMl, "color");
        String[][] mapFovscale = extractParamsFromMapML(mapMl, "fovscale");
        String[][] mapRotate = extractParamsFromMapML(mapMl, "rotate");

        String[][] mapBimage = extractParamsFromMapML(mapMl, "bimage");
        String[][] mapLasershow = extractParamsFromMapML(mapMl, "lasershow");

        return new MapData(mapMetadata,
                mapSpeed,
                mapGrowspeed,
                mapStartcolor,
                mapStartfovscale,
                mapStartrotation,

                mapSpike,
                mapSlide,
                mapWall,
                mapDoublejump,
                mapRope,

                mapTapnote,
                mapGrownote,
                mapSlidenote,

                mapColor,
                mapFovscale,
                mapRotate,

                mapBimage,
                mapLasershow);
    }

    // TODO: All of this should be in another class on day,
    //  if I do not forget (I will xD) The File-Extension should also be .grashML
    private String[][] extractParamsFromMapML(String mapML, String keyword) {
        return extractMapMLParamsFromElements(extractMapMLKeywordElements(mapML, keyword));
    }

    private String generateMapML(Path mapPath) {
        String s = "";
        try {
            s = String.join("", Files.readAllLines(mapPath)).replace(" ", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }

    private String extractMapMLKeywordElements(String mapML, String keyword) {
        String searchedKeyword = "[" + keyword + "]";

        int keywordIndex = mapML.indexOf(searchedKeyword);
        if(keywordIndex == -1) return null;
        int paramsStartIndex = mapML.indexOf('{', keywordIndex);
        int paramsEndIndex = mapML.indexOf('}', paramsStartIndex);

        return mapML.substring(paramsStartIndex + 1, paramsEndIndex); // elementsFromKeyword
    }

    private String[][] extractMapMLParamsFromElements(String mlElementList) {
        if(mlElementList == null) return null;

        ArrayList<String[]> paramList = new ArrayList<>();

        String[] elements = mlElementList.replace(")", "").split("\\(");
        for(String activeElement : elements) {
            if(activeElement.isEmpty()) continue;
            paramList.add(activeElement.split(","));
        }

        return paramList.toArray(new String[0][0]);
    }

}
