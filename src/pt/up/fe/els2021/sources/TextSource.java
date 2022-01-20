package pt.up.fe.els2021.sources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pt.up.fe.els2021.Table;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class TextSource extends TableSource {

    private final String startToken;
    private final String endToken;
    private final int width;
    private final int headersize;
    // private final ArrayList<String> columns; includes
    private final String separator;

    @JsonCreator
    public TextSource(@JsonProperty("includes") Map<Include, String> includes,
                      @JsonProperty("files") List<String> files, @JsonProperty("startToken") String beg,
                      @JsonProperty("endToken") String end, @JsonProperty("width") int width,
                      @JsonProperty("headersize") int headersize, @JsonProperty("separator") String separator) {

        super(includes, files);
        this.startToken = beg;
        this.endToken = end;
        this.width = width;
        this.headersize = headersize;
        this.separator = separator;
    }

    @Override
    protected Table getFileTable(String fileContent) throws Exception {
        return parser(fileContent);
    }

    private Table parser(String filename) throws Exception {
        try (var inputString = new StringReader(filename); var reader = new BufferedReader(inputString)) {
            var iterator = reader.lines().iterator();

            String line;
            ArrayList<char[]> header = new ArrayList<>();
            ArrayList<char[]> body = new ArrayList<>();
            List<List<String>> table = new ArrayList<>();
            // ArrayList<ArrayList<String>> body = new ArrayList<>();
            boolean firstLine = false, headerComplete = false;
            while (iterator.hasNext()) {
                line = iterator.next();
                if (headerComplete && line.isBlank()) {
                    table = getColumns(header, body, headersize, width, separator);
                    break;
                }

                if (!firstLine) {
                    if (line.trim().startsWith(startToken)) {
                        firstLine = true;
                        header.add(line.toCharArray());
                    }

                } else {
                    if (!headerComplete) {
                        header.add(line.toCharArray());
                        headerComplete = line.replaceAll(" +", " ").trim().endsWith(endToken);

                    } else {
                        body.add(line.toCharArray());
                    }
                }
            }
            reader.close();
            var fliped = flipTable(table.subList(1, table.size()));
            return new Table(table.get(0), fliped);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("TextSource: did not find end of table");
        }
    }

    public static boolean checkTable(ArrayList<List<String>> table, int height, int widht, int header) {
        if (table.size() < header + 1 || table.size() > height + header) {
            return false;
        }

        for (List<String> line : table) {
            if (line.size() > widht) {
                return false;
            }
        }
        return true;
    }

    private List<List<String>> getColumns(ArrayList<char[]> tableHeader, ArrayList<char[]> tableBody, int headerSize,
                                          int width, String separator) {


        var newTable = getTable(tableHeader, tableBody, width, headerSize, separator);

        for (var header : newTable) {
            // System.out.println("header - " + header.toString());
        }

        return newTable;

    }

    private List<List<String>> getTable(ArrayList<char[]> tableHeader, ArrayList<char[]> tableBody, int width,
                                        int headerSize, String separator) {
        List<List<String>> table = new ArrayList<>();
        ArrayList<ArrayList<int[]>> indexes = new ArrayList<>();

        ArrayList<String> header = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            header.add("");
        }

        for (char[] h : tableHeader) {
            indexes.add(getIndexesOfHeader(h));
        }
        ArrayList<int[]> limits = arrangeLimits(indexes, width);
        boolean word = false;

        for (char[] c : tableHeader) {
            int ind = -1;
            for (int i = 0; i < c.length; i++) {
                if (word == false) {
                    if (c[i] != ' ') {
                        ind = getIndexOfLine(i, limits);
                        if (ind >= 0) {
                            String colWord = getWordFromIndex(c, i);
                            String l = "";
                            l = header.get(ind);
                            header.set(ind, (l + " " + colWord).trim());
                            word = true;
                        }
                    }
                } else {
                    if (c[i] == ' ' || i == c.length - 1) {
                        word = false;
                    }
                }
            }
        }
        // System.out.println(header);
        table.add(header);

        for (char[] c : tableBody) {
            ArrayList<String> line = new ArrayList<>(
                    Arrays.asList(String.valueOf(c).trim().replaceAll(" +", " ").split(separator)));
            if (line.size() == width) {
                table.add(line);
            } else {
                // System.out.println();
                line = new ArrayList<>();
                for (int i = 0; i < width; i++) {
                    line.add("");
                }
                int lineIndex = -1;
                for (int i = 0; i < c.length; i++) {
                    if (word == false) {
                        if (c[i] != ' ') {
                            lineIndex = getIndexOfLine(i, limits);
                            if (lineIndex >= 0) {
                                String colWord = getWordFromIndex(c, i);
                                String l = "";
                                line.set(lineIndex,(" " + colWord).trim());
                                word = true;
                            }
                        }
                    } else {
                        if (c[i] == ' ' || i == c.length - 1) {
                            word = false;
                        }
                    }
                }
                table.add(line);
            }
        }

        return table;
    }

    private int getIndexOfLine(int index, ArrayList<int[]> limits) {
        for (int[] limit : limits) {
            if (index >= limit[0] && index <= limit[1]) {
                return limits.indexOf(limit);
            }
        }
        return -1;
    }

    private String getWordFromIndex(char[] h, int index) {
        int i = index;
        String word = "";

        while (index < h.length && h[i] != ' ') {
            word += h[index];
            i++;
            index++;
            // System.out.print("w " + word);
        }
        return word;
    }

    private ArrayList<int[]> getIndexesOfHeader(char[] h1) {
        int[] limits = new int[2];
        ArrayList<int[]> indexes = new ArrayList<>();
        boolean word = false;
        for (int i = 0; i < h1.length; i++) {
            if (word == false) {
                if (h1[i] != ' ') {
                    limits[0] = i;
                    word = true;
                }
            } else {
                if (h1[i] == ' ' || i == h1.length - 1) {
                    limits[1] = i - 1;
                    indexes.add(limits);
                    // System.out.println(limits[0] + "-" + limits[1]);
                    limits = new int[2];
                    word = false;
                }
            }
        }

        return indexes;
    }

    private ArrayList<String> getLineChars(char[] c) {
        ArrayList<String> x = new ArrayList<>();
        String word = "";
        boolean inWord = false;

        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                if (inWord) {
                    x.add(word);
                    word = "";
                    inWord = false;
                }
                x.add(String.valueOf(c[i]));
            } else {
                word += String.valueOf(c[i]);
                // System.out.println(word);
                inWord = true;
            }
        }
        if (word != "")
            x.add(word);
        return x;
    }

    private ArrayList<int[]> arrangeLimits(ArrayList<ArrayList<int[]>> limits, int width) {
        // System.out.println("final");

        ArrayList<int[]> finalLimits = new ArrayList<>();
        ArrayList<int[]> base = new ArrayList<>();
        for (ArrayList<int[]> l : limits) {
            if (l.size() == width)
                base = l;
        }
        int min, max;
        if (!base.isEmpty()) {
            // significa que h� uma base
            for (int[] finalLimit : base) {
                min = finalLimit[0];
                max = finalLimit[1];
                for (ArrayList<int[]> lineLimits : limits)
                    for (int[] limit : lineLimits) {
                        if (Math.abs(min - limit[0]) <= 2 || Math.abs(max - limit[1]) <= 2) {
                            // falta por opc��o de se esta contido entre os da base
                            min = Math.min(min, limit[0]);
                            max = Math.max(max, limit[1]);
                        }
                    }
                // System.out.println(min + "-" + max);
                finalLimits.add(new int[]{min, max});
            }
        }

        return finalLimits;
    }

    public static final class Builder extends TableSource.Builder {
        private final String startToken;
        private final String endToken;
        private final int width;
        private final int headersize;
        // private final ArrayList<String> columns;
        private final String separator;

        public Builder(String beg, String end, int width, int headersize, String separator) {
            this.startToken = beg;
            this.endToken = end;
            this.width = width;
            this.headersize = headersize;
            // this.columns = columns;
            this.separator = separator;
        }

        @Override
        public TableSource build() {
            return new TextSource(includes, files, startToken, endToken, width, headersize, separator);
        }
    }

    public List<List<String>> flipTable(List<List<String>> list) {

        var newTable = new ArrayList<List<String>>(); for (int i = 0; i <
                list.get(0).size(); i++) { newTable.add(new ArrayList<>()); }

        for (int i = 0; i < list.size(); i++) { for (int j = 0; j <
                list.get(i).size(); j++) { newTable.get(j).add(list.get(i).get(j)); } }

        return newTable; }

}

/*
 * private Table parser(String fileContent) throws Exception { try (var
 * inputString = new StringReader(fileContent); var reader = new
 * BufferedReader(inputString)) { var iterator = reader.lines().iterator(); var
 * file = new ArrayList<List<String>>(); var firstLine = false; var ending =
 * false;
 *
 * while (iterator.hasNext()) { var line = iterator.next();
 *
 * if (!firstLine) { if (line.trim().startsWith(this.startToken)) { firstLine =
 * true;
 *
 * List<String> trimedLine = Arrays.asList(line.trim().replaceAll(" +",
 * " ").split(separator)); if(trimedLine.size() == width){ file.add(trimedLine);
 * }else{ fixFileLine(line); } } } else { ending = line.endsWith(this.endToken);
 * List<String> trimedLine = Arrays.asList(line.trim().replaceAll(" +",
 * " ").split(separator)); if(trimedLine.size() == width){ file.add(trimedLine);
 * } else { fixFileLine(line); }
 *
 * if (ending) { if (checkTable(file, height, width, headersize)) { return
 * getColumns(file, headersize); } else { firstLine = false; } } } } throw new
 * Exception("TextSource: did not find end of table"); } }
 *
 * private void fixFileLine(String line) { for (: ) {
 *
 * } }
 *
 * public boolean checkTable(ArrayList<List<String>> table, int height, int
 * width, int header) { if (table.size() < header + 1 || table.size() > height +
 * header) { return false; }
 *
 * for (var line : table) { if (line.size() > width) { return false; } } return
 * true; }
 *
 * public Table getColumns(ArrayList<List<String>> table, int headerSize) {
 *
 * var fileHeaders = table.subList(0, headerSize); var header = new
 * ArrayList<>(fileHeaders.get(0));
 *
 * // merge header for (int i = 1; i < headerSize; i++) { int tempHeaderSize =
 * header.size(); for (int j = 0; j < fileHeaders.get(i).size(); j++) { if
 * (tempHeaderSize > j) { String temp = header.get(j); header.set(j, temp + " "
 * + fileHeaders.get(i).get(j)); } else { header.add(fileHeaders.get(i).get(j));
 * } } }
 *
 * ArrayList<List<String>> newTable = new ArrayList<>() { { add(header); } };
 *
 * newTable.addAll(table.subList(2, table.size()));
 *
 * var flipped = flipTable(newTable.subList(1, newTable.size()));
 *
 * return new Table(newTable.get(0), flipped);
 *
 * }
 */


