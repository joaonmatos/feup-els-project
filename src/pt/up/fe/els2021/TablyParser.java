package pt.up.fe.els2021;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.parser.IParser;
import pt.up.fe.els2021.combinators.InnerJoinCombinator;
import pt.up.fe.els2021.commands.*;
import pt.up.fe.els2021.commands.Command;
import pt.up.fe.els2021.exporters.CsvExporter;
import pt.up.fe.els2021.functions.TableFunction;
import pt.up.fe.els2021.sources.JsonSource;
import pt.up.fe.els2021.sources.TableSource;
import pt.up.fe.els2021.sources.TextSource;
import pt.up.fe.els2021.sources.XmlSource;
import pt.up.fe.els2021.tably.TablyStandaloneSetup;
import pt.up.fe.els2021.tably.tably.*;
import pt.up.fe.els2021.tably.tably.impl.ProgramImpl;

import javax.inject.Inject;
import java.io.Reader;
import java.util.*;

public class TablyParser {
    @Inject
    private IParser parser;

    private final HashSet<String> existingVariables = new HashSet<>();

    public TablyParser() {
        var injector = new TablyStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);
    }

    public void setExistingVariables(Collection<String> variables) {
        existingVariables.clear();
        existingVariables.addAll(variables);
    }

    public Program parse(Reader reader) throws Exception {
        var parseResult = parser.parse(reader);

        if (parseResult.hasSyntaxErrors()) {
            var builder = new StringBuilder("Syntax errors:\n");
            for (var error: parseResult.getSyntaxErrors()) {
                builder.append('\t').append(error.getTextRegionWithLineInformation()).append('\t').append(error.getSyntaxErrorMessage()).append('\n');
            }
            throw new Exception(builder.toString());
        }

        return parseProgram((ProgramImpl) parseResult.getRootASTElement());
    }

    private Program parseProgram(pt.up.fe.els2021.tably.tably.Program parserProgram) throws Exception {
        var commands = new ArrayList<Command>();
        for (var parserCommand : parserProgram.getCommands()) {
            commands.add(parseCommand(parserCommand));
        }
        return new Program(commands);
    }

    private Command parseCommand(pt.up.fe.els2021.tably.tably.Command parserCommand) throws Exception {
        if (parserCommand instanceof Assignment parserAssignment) {
            existingVariables.add(parserAssignment.getTarget());
            var source = parserAssignment.getSource();
            return parseAssignment(parserAssignment);
        } else if (parserCommand instanceof Export parserExport) {
            if (!existingVariables.contains(parserExport.getSource())) {
                throw new Exception("Illegal export: table " + parserExport.getSource() + " does not exist at this point");
            }
            return parseExport(parserExport);
        }
        else throw new Exception("Unknown parse element " + parserCommand);
    }

    private Command parseExport(Export parserExport) {
        return new TableExport(
                parserExport.getSource(),
                new CsvExporter(parserExport.getOutfile())
        );
    }

    private Command parseAssignment(Assignment parserAssignment) throws Exception {
        var source = parserAssignment.getSource();
        var target = parserAssignment.getTarget();
        if (source instanceof Import parserImport) {
            return parseImport(target, parserImport);
        } else if (source instanceof Transformation parserTransformation) {
            return parseTransformation(target, parserTransformation);
        } else if (source instanceof Combination parserCombination) {
            return parseCombination(target, parserCombination);
        } else throw new Exception("Unknown parse element " + parserAssignment);
    }

    private Command parseCombination(String target, Combination parserCombination) throws Exception {
        var sources = parserCombination.getTables();
        var parserCombinator = parserCombination.getCombinator();
        if (parserCombinator instanceof MergeCombinator) {
            return new TableCombination(
                    sources,
                    target,
                    new pt.up.fe.els2021.combinators.MergeCombinator()
            );
        } else if (parserCombinator instanceof JoinCombinator join) {
            return new TableCombination(
                    sources,
                    target,
                    new InnerJoinCombinator(join.getKey())
            );
        } else throw new Exception("Unknown parse element " + parserCombinator);
    }

    private Command parseTransformation(String target, Transformation parserTransformation) throws Exception {
        if (!existingVariables.contains(parserTransformation.getSource())) {
            throw new Exception("Parse error: source \"" + parserTransformation.getSource() + "\"for transformation does not exist at this point");
        }
        var functions = new ArrayList<TableFunction>();
        for (var function : parserTransformation.getFunctions()) {
            functions.add(parseFunction(function));
        }
        return new TableTransformation(
                parserTransformation.getSource(),
                target,
                functions
        );
    }

    private TableFunction parseFunction(Function function) throws Exception {
        if (function instanceof AddColumnFunction f) {
            return new pt.up.fe.els2021.functions.AddColumnFunction(f.getColName(), f.getColValue());
        } else if (function instanceof  AggregatesFunction f) {
            return new pt.up.fe.els2021.functions.AggregatesFunction(switch (f.getType()) {
                case COUNT -> pt.up.fe.els2021.functions.AggregatesFunction.Type.COUNT;
                case MAX -> pt.up.fe.els2021.functions.AggregatesFunction.Type.MAX;
                case MIN -> pt.up.fe.els2021.functions.AggregatesFunction.Type.MIN;
                case SUM -> pt.up.fe.els2021.functions.AggregatesFunction.Type.SUM;
                case AVERAGE -> pt.up.fe.els2021.functions.AggregatesFunction.Type.AVERAGE;
            });
        } else if (function instanceof GroupByFunction f) {
            return parseGroupByFunction(f);
        } else if (function instanceof MoveColumnFunction f) {
            return new pt.up.fe.els2021.functions.MoveColumnFunction(
                    f.getColName(),
                    f.getIndex()
            );

        } else if (function instanceof RenameFunction f) {
            return new pt.up.fe.els2021.functions.RenameFunction(
                    f.getColName(),
                    f.getNewColName()
            );
        } else if (function instanceof SelectFunction f) {
            if (new HashSet<>(f.getColumns()).size() < f.getColumns().size()) {
                throw new Exception("Language error: select function is selecting duplicate columns");
            }
            return new pt.up.fe.els2021.functions.SelectFunction(f.getColumns());
        } else if (function instanceof TrimFunction f) {
            return new pt.up.fe.els2021.functions.TrimFunction(f.getOffset(), f.getLimit());
        } else if (function instanceof ExcludeFunction f) {
            return new pt.up.fe.els2021.functions.ExcludeFunction(new ArrayList<>(new HashSet<>(f.getColumns())));
        } throw new Exception("Unknown parse element " + function);
    }

    private TableFunction parseGroupByFunction(GroupByFunction parserFunction) throws Exception {
        var innerParser = new TablyParser();
        innerParser.existingVariables.add(parserFunction.getInputTable());
        var commands = new ArrayList<Command>();
        for (var assignment : parserFunction.getCommands()) {
            commands.add(innerParser.parseCommand(assignment));
        }
        if (!innerParser.existingVariables.contains(parserFunction.getOutputTable())) {
            throw new Exception("Language error: group by transformation specifies output table '" + parserFunction.getOutputTable() + "' but does not produce it in lambda");
        }
        return new pt.up.fe.els2021.functions.GroupByFunction(
                parserFunction.getColName(),
                parserFunction.getInputTable(),
                parserFunction.getOutputTable(),
                new Program(commands)
        );
    }

    private Command parseImport(String target, Import parserImport) throws Exception {
        var sources = parserImport.getSources();
        Map<TableSource.Include, String> includes = parseIncludes(parserImport.getIncludes());
        var adapter = parseAdapter(sources, includes, parserImport.getAdapter());
        return new TableImport(adapter, target);
    }

    private TableSource parseAdapter(EList<String> sources, Map<TableSource.Include, String> includes, ImportAdapter adapter) {
        if (adapter instanceof XmlAdapter xml) {
            return new XmlSource(includes, sources, xml.getPath());
        } else if (adapter instanceof JsonAdapter json) {
            return new JsonSource(includes, sources, json.getPath());
        } else if (adapter instanceof TextAdapter text) {
            return new TextSource(
                    includes,
                    sources,
                    text.getStartToken(),
                    text.getEndToken(),
                    text.getWidth(),
                    text.getSeparator()
            );
        } else throw new Error("Unknown parse element " + adapter);
    }

    private Map<TableSource.Include, String> parseIncludes(EList<Include> includes) throws Exception {
        var map = new HashMap<TableSource.Include, String>();
        for (var include: includes) {
            var internalInclude = switch (include.getType()) {
                case PATH -> TableSource.Include.PATH;
                case FILENAME -> TableSource.Include.FILENAME;
                case FOLDER -> TableSource.Include.FOLDER;
            };
            if (map.containsKey(internalInclude)) {
                throw new Exception("Repeated include of type " + internalInclude);
            }
            map.put(internalInclude, include.getColName());
        }
        return map;
    }


}
