package pt.up.fe.els2021.sources;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import pt.up.fe.els2021.Table;

public final class TxtSource extends TableSource {

    private final String startToken;
    private final String endToken;
    private final int height;
    private final int width;
    private final int headersize;
    // private final ArrayList<String> columns; includes
    private final String separator;

    @JsonCreator
    public TxtSource(
            @JsonProperty("includes") Map<Include, String> includes,
            @JsonProperty("files") List<String> files,
            @JsonProperty("startToken") String beg,
            @JsonProperty("endToken") String end,
            @JsonProperty("height") int height,
            @JsonProperty("width") int width,
            @JsonProperty("headersize") int headersize,
            @JsonProperty("separator") String separator) {

        super(includes, files);

        this.startToken = beg;
        this.endToken = end;
        this.height = height;
        this.width = width;
        this.headersize = headersize;
        this.separator = separator;
    }

    @Override
    protected Table getFileTable(String fileContent) throws Exception {
        return parser(fileContent);
    }

    public Table parser(String fileContent) {
        try {
            Table result = null;
            Reader inputString = new StringReader(fileContent);
            BufferedReader reader = new BufferedReader(inputString);
            String line;
            ArrayList<List<String>> ficheiro = new ArrayList<>();
            boolean firstLine = false, ending = false;
            while ((line = reader.readLine()) != null) {

                if (!firstLine) {
                    if (line.trim().startsWith(this.startToken)) {
                        firstLine = true;
                        ficheiro.add(Arrays.asList(line.trim().replaceAll(" +", " ").split(separator)));
                    }
                } else {
                    ending = line.endsWith(this.endToken);
                    ficheiro.add(Arrays.asList(line.trim().replaceAll(" +", " ").split(separator)));

                    if (ending) {
                        if (checkTable(ficheiro, height, width, headersize)) {
                            result = getColumns(ficheiro, headersize);
                        } else {
                            firstLine = false;
                            ending = false;
                        }
                    }
                }
            }

            reader.close();

            return result;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public boolean checkTable(ArrayList<List<String>> table, int height, int widht, int header) {
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

    public Table getColumns(ArrayList<List<String>> table, int headerSize) {

        List<List<String>> fileHeaders = table.subList(0, headerSize);
        List<String> header = new ArrayList<String>();
        fileHeaders.get(0).forEach(r -> header.add(r));

        // merge header
        for (int i = 1; i < headerSize; i++) {
            int tempHeaderSize = header.size();
            for (int j = 0; j < fileHeaders.get(i).size(); j++) {
                if (tempHeaderSize > j) {
                    String temp = header.get(j);
                    header.set(j, temp + " " + fileHeaders.get(i).get(j));
                } else {
                    header.add(fileHeaders.get(i).get(j));
                }
            }
        }

        ArrayList<List<String>> newTable = new ArrayList<List<String>>() {
            {
                add(header);
            }
        };

        for (List<String> a : table.subList(2, table.size())) {
            newTable.add(a);
        }

        List<List<String>> fliped = flipTable(newTable.subList(1, newTable.size()));

        return new Table(newTable.get(0), fliped);

    }

    public List<List<String>> flipTable(List<List<String>> list) {

        ArrayList<List<String>> newTable = new ArrayList<List<String>>();
        for (int i = 0; i < list.get(0).size(); i++) {
            newTable.add(new ArrayList<String>());
        }
        System.out.println(newTable.toString());

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                System.out.println(i + " " + j);
                newTable.get(j).add(list.get(i).get(j));
            }
        }

        System.out.println(newTable.toString());

        return newTable;
    }

    public static final class Builder extends TableSource.Builder {
        private final String startToken;
        private final String endToken;
        private final int height;
        private final int width;
        private final int headersize;
        // private final ArrayList<String> columns;
        private final String separator;

        public Builder(String beg, String end, int height, int width, int headersize, String separator) {
            this.startToken = beg;
            this.endToken = end;
            this.height = height;
            this.width = width;
            this.headersize = headersize;
            // this.columns = columns;
            this.separator = separator;
        }

        @Override
        public TableSource build() {
            return new TxtSource(includes, files, startToken, endToken, height, width, headersize, separator);
        }
    }

}
