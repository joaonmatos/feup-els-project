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
    private final String separator;

    @JsonCreator
    public TextSource(@JsonProperty("includes") Map<Include, String> includes,
                      @JsonProperty("files") List<String> files, @JsonProperty("startToken") String beg,
                      @JsonProperty("endToken") String end, @JsonProperty("width") int width,
                      @JsonProperty("separator") String separator) {

        super(includes, files);
        this.startToken = beg;
        this.endToken = end;
        this.width = width;
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
                    table = getColumns(header, body, width, separator);
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

    private List<List<String>> getColumns(
            ArrayList<char[]> tableHeader,
            ArrayList<char[]> tableBody,
            int width,
            String separator
    ) {


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
        private final String separator;

        public Builder(String beg, String end, int width, String separator) {
            this.startToken = beg;
            this.endToken = end;
            this.width = width;
            this.separator = separator;
        }

        @Override
        public TableSource build() {
            return new TextSource(includes, files, startToken, endToken, width, separator);
        }
    }

    public List<List<String>> flipTable(List<List<String>> list) {

        var newTable = new ArrayList<List<String>>(); for (int i = 0; i <
                list.get(0).size(); i++) { newTable.add(new ArrayList<>()); }

        for (int i = 0; i < list.size(); i++) { for (int j = 0; j <
                list.get(i).size(); j++) { newTable.get(j).add(list.get(i).get(j)); } }

        return newTable; }

}
