## Model and Language Description

The Tably language encodes a very simple model, that yet is dual with a much more powerful one.
Tably is implemented as a simple expression-based language, with no control flow and linear syntax.
The core data primitive of the Tably language is a simple table, with no row indexing and named columns.
All transformation primitives immutably transform these tables, producing new tables in the process.
Aliasing is allowed.

We support four kind of operations, those being:

 - Imports: import commands allow reading and parsing the content of one or more files into a table. To do so, the user must only provide a set of paths and globs to be read, as well as the indication of which adapter to use. Adapters implement parsing for different types of files. We support reading XML and JSON files, as well as extracting whitespace-structured tables from text files.
 - Transformations: transformations allow the user to compose the application of one or more functions to a certain table, yielding a new, transformed table.
 - Combinations: combinations allow the user to combine one or more tables to produce a new one. To do so, it configures a combinator, such as a join on a key or a simple merge, and provides a set of tables to be combined.
 - Exports: export commands allow writing the data of a table to a file. At the moment, it supports only csv output.

This simple linear model is in fact isomorphic with a more interesting one, which is that of a data-flow
DAG. In short, we can understand the described primitives in the following terms:

 - Imports are sources in the graph.
 - Exports are sinks in the graph.
 - Transformations as nodes have one inbound edge, whereas combinations can have multiple inbound edges
 - The use of a certain variable (which denotes a certain table), consitutes an edge from the node that assigns the table
 to the node that uses it.


## Reasoning

We chose to model our system this way because it provides several benefits:

- Immutability - by not mutating tables in place, but only providing functions that take existing
  tables and produce new tables (whether these tables are inside the system or are to be read from
  or written to files), we reduce the ammount of confusion about the shape of the tables the user
  is working with.
- Partial evaluation - by dividing each directive in the configuration language into a
  self-contained commands that can be incrementaly evaluated, we lay the groundwork to easily
  implement several features, such as REPLs or debuggers.
- Strictly declarative - by separating the configuration from the execution of the system, we open
  a lot of ground for adapting the implementation without breaking the user's abstraction. Not only
  that, our model allows for a measure of 'query planning' to potentially be implemented, such as:
  - Not performing operations that are not dependencies of exported tables (lazy evaluation);
  - Factoring common expressions to deduplicate work;
  - Reordering operations when the result is not affected;
  - Combining operations when that is advantageous;
  - Paralelizing unrelated operations;
  - Propagating validation requirements to the importers (e.g. validating that a column that will
    be used in an average only contains numbers as soon as it is imported from the source file).
