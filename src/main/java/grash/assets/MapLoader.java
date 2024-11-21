package grash.assets;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public MapLoader() {

    }

    public MapMetadata loadMapMetadata(Path folderPath, String grashMapFileName) {
        String mapMl = generateMapML(Paths.get(folderPath.toAbsolutePath() + "\\" + grashMapFileName));

        String[][] mapName = extractParamsFromMapML(mapMl, "mapname");
        String[][] mapAuthor = extractParamsFromMapML(mapMl, "mapauthor");
        String[][] songName = extractParamsFromMapML(mapMl, "songname");
        String[][] mapVersion = extractParamsFromMapML(mapMl, "mapversion");

        return new MapMetadata(mapName[0][0], mapAuthor[0][0], mapVersion[0][0], songName[0][0], folderPath, grashMapFileName);
    }

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
        int paramsStartIndex = mapML.indexOf('{', keywordIndex);
        int paramsEndIndex = mapML.indexOf('}', paramsStartIndex);

        return mapML.substring(paramsStartIndex + 1, paramsEndIndex); // elementsFromKeyword
    }

    private String[][] extractMapMLParamsFromElements(String mlElementList) {
        ArrayList<String[]> paramList = new ArrayList<>();

        String[] elements = mlElementList.replace(")", "").split("\\(");
        for(String activeElement : elements) {
            if(activeElement.isEmpty()) continue;
            paramList.add(activeElement.split(","));
        }

        return paramList.toArray(new String[0][0]);
    }

}
