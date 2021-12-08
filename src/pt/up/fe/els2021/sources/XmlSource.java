package pt.up.fe.els2021.sources;

// import java.io.File;
// import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.w3c.dom.NodeList;
import pt.up.fe.els2021.Table;
import pt.up.fe.specs.util.SpecsXml;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class XmlSource extends TableSource {

    private final String xpath;

    @JsonCreator
    public XmlSource(
            @JsonProperty("includes") Map<Include, String> includes,
            @JsonProperty("files") List<String> files,
            @JsonProperty("path") String xpath
    ) {
        super(includes, files);
        this.xpath = xpath;
    }

    @Override
    protected Table getFileTable(String fileContent) throws Exception {

        var columnNames = new LinkedHashMap<String, Integer>();
        var columns = new ArrayList<List<String>>();
        // get wanted element
        var root = SpecsXml.getXmlRoot(fileContent);
        var xpath = XPathFactory.newInstance().newXPath();
        var foundNodes = (NodeList) xpath.compile(this.xpath).evaluate(root, XPathConstants.NODESET);

        for (int nodeIx = 0; nodeIx < foundNodes.getLength(); nodeIx++) {
            var xml = foundNodes.item(nodeIx);
            var xmlColumns = xml.getChildNodes();

            int latestTableColumnIx = 0;
            // Read values from elements' children and put them in the respective column
            for (int elementIx = 0; elementIx < xmlColumns.getLength(); elementIx++) {

                var el = xmlColumns.item(elementIx);
                var elementName = el.getNodeName();
                if (elementName.equals("#text")) continue;
                var content = el.getTextContent();

                if (!columnNames.containsKey(elementName)) {
                    columnNames.put(elementName, latestTableColumnIx++);
                    columns.add(new ArrayList<>());
                    // when new attribute is found populate column for previous nodes
                    for (var ignored = 0; ignored < nodeIx; ignored++)
                        columns.get(latestTableColumnIx).add(null);
                }

                columns.get(columnNames.get(elementName)).add(content);
            }
        }

        return new Table(List.copyOf(columnNames.keySet()), columns);
    }

    public static final class Builder extends TableSource.Builder {
        private final String xpath;

        public Builder(String xpath) {
            this.xpath = xpath;
        }


        @Override
        public TableSource build() {
            return new XmlSource(includes, files, xpath);
        }
    }

}
