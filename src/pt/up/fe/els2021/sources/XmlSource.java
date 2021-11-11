package pt.up.fe.els2021.sources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pt.up.fe.els2021.Table;
import pt.up.fe.specs.util.SpecsXml;

public record XmlSource(List<String> files, String elementName, boolean includeFileName) implements TableSource {

    @Override
    public Table getTable() throws Exception {
        // initiating tables and first columnName
        List<String> columnNames = new ArrayList<>();
        List<List<String>> columns = new ArrayList<>();

        int startIndex = 0;
        if (includeFileName) {
            columnNames.add("File");
            startIndex = 1;
        }

        for (String file : files) {
            // get wanted element
            File f = new File(file);
            Document root = SpecsXml.getXmlRoot(f);
            Element xml = SpecsXml.getElement(root.getDocumentElement(), elementName);
            List<Element> xmlCollumns = SpecsXml.getElementChildren(xml);

            // Initializing the columns List according to the number of elements
            // in the wanted element
            if (columns.isEmpty()) {
                for (int i = 0; i < xmlCollumns.size() + 1; i++) {
                    columns.add(new ArrayList<>());
                }
            }

            // Add the first column -> filename
            if (includeFileName) {
                columns.get(0).add(file);
            }

            // looping throw the elements and adding into column names and the columns.
            // If it starts at one we are including a file column
            for (int i = 0; i < xmlCollumns.size(); i++) {

                Element el = xmlCollumns.get(i);

                // Add the collumn name only once
                if (!columnNames.contains(el.getNodeName()))
                    columnNames.add(el.getNodeName());

                columns.get(i + startIndex).add(el.getTextContent());
            }
        }
        return new Table(columnNames, columns);
    }

}
