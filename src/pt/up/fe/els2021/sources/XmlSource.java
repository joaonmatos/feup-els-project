package pt.up.fe.els2021.sources;

import java.util.List;

import pt.up.fe.els2021.Table;

public record XmlSource(List<String> files, String elementName, boolean includeFileName) implements TableSource {

    @Override
    public Table getTable() {
        // TODO Auto-generated method stub
        return null;
    }

}
