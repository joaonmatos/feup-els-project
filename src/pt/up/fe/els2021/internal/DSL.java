package pt.up.fe.els2021.internal;

import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.combinators.InnerJoinCombinator;
import pt.up.fe.els2021.combinators.MergeCombinator;
import pt.up.fe.els2021.combinators.TableCombinator;
import pt.up.fe.els2021.exporters.CsvExporter;
import pt.up.fe.els2021.functions.*;
import pt.up.fe.els2021.sources.JsonSource;
import pt.up.fe.els2021.sources.XmlSource;

import java.util.ArrayList;
import java.util.List;

public class DSL {
    public static Transformation.Builder transform() {
        return new Transformation.Builder();
    }

    public static Combination.Builder combine(TableCombinator combinator) {
        return new Combination.Builder(combinator);
    }

    public static class Sources {
        private Sources() {
        }

        public static JsonSource.Builder json(String elementPath) {
            return new JsonSource.Builder(elementPath);
        }

        public static XmlSource.Builder xml(String xpath) {
            return new XmlSource.Builder(xpath);
        }
    }

    public static class Combinators {
        private Combinators() {
        }

        public InnerJoinCombinator join(String joinColumn) {
            return new InnerJoinCombinator(joinColumn);
        }

        public MergeCombinator merge() {
            return new MergeCombinator();
        }
    }

    public static class Exporters {
        private Exporters() {
        }

        public static CsvExporter csv(String filename) {
            return new CsvExporter(filename);
        }
    }

    public static class Functions {
        private Functions() {
        }

        public static ExcludeFunction.Builder exclude() {
            return new ExcludeFunction.Builder();
        }

        public static OrderByFunction.Builder orderBy(String key) {
            return new OrderByFunction.Builder(key);
        }

        public static RenameFunction rename(String columnName, String newColumnName) {
            return new RenameFunction(columnName, newColumnName);
        }

        public static SelectFunction.Builder select() {
            return new SelectFunction.Builder();
        }

        public static TrimFunction.Builder trim() {
            return new TrimFunction.Builder();
        }
    }

    public static class Transformation {
        private final Table source;
        private final List<TableFunction> functions;

        private Transformation(Table source, List<TableFunction> functions) {
            this.source = source;
            this.functions = functions;
        }

        public Table getResult() throws Exception {
            var table = source;
            for (var function : functions) {
                table = function.apply(table);
            }
            return table;
        }


        public static class Builder {
            private final List<TableFunction> functions = new ArrayList<>();

            public Builder withFunction(TableFunction function) {
                functions.add(function);
                return this;
            }

            public Builder withFunction(TableFunction.Builder builder) {
                return this.withFunction(builder.build());
            }

            public Table getResult(Table source) throws Exception {
                return build(source).getResult();
            }

            public Transformation build(Table source) {
                return new Transformation(source, functions);
            }
        }
    }

    public static class Combination {
        private final TableCombinator combinator;
        private final List<Table> tables;


        private Combination(TableCombinator combinator, List<Table> tables) {
            this.combinator = combinator;
            this.tables = tables;
        }

        public Table getResult() throws Exception {
            return tables.stream().reduce(combinator::combine).get();
        }

        public static class Builder {
            private final TableCombinator combinator;
            private final List<Table> tables = new ArrayList<>();


            public Builder(TableCombinator combinator) {
                this.combinator = combinator;
            }

            public Combination build() {
                return new Combination(combinator, tables);
            }

            public Table getResult() throws Exception {
                return build().getResult();
            }

            public Builder withTable(Table table) {
                tables.add(table);
                return this;
            }
        }
    }
}
