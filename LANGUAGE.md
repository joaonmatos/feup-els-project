## Model and Language Description

Our model is based on functions and expressions, with very direct semantics, no control flow, and
simple I/O mechanisms.

As for the input there is **.json config file** where the operations are set. The file should contain an array with the operations (objects) that you wish to apply.

This objects are all commands, which all have a source and a target. We have 3 different types of commands, a import command to read a specific element in a file and create a table, a export command to load our variables to a file and a transform command which applies operations on these variables(tables).

The first type of operation you can do is load a table from a file (import):

> To get table `X`, import element `Y` from XML files `A`, `B`, and `C`

In the config file there must be a JSON object with the following format:


```json
{
  "command": "import",
  "source": {
    "type": "xml",
    "files": ["file_a.xml", "file_b.xml", "file_c"],
    "elementName": "element_y",
    "includeFileName": true
  },
  "target": "table"
}
```

###### *Note: The includeFileName attribute defines if the filename is added to the first column of the table*

The second type of operation you can do is create a new table as a function of existing tables
(transforms and combinators):

> To get table `X`, apply functions `rename(A, B)`, `rename(C, D)`, and `select(B, E, D)` to table
> `Y`

In this transform command we specify the source table and the target, and provide a array of functions which are the operations to be applied to the different tables.

For this example the config file should follow this format:

```json
{
  "command": "transform",
  "source": "table_X",
  "target": "table_Y",
  "functions": [
    {
      "function": "rename",
      "from": "A",
      "to": "B"
    },
    {
      "function": "rename",
      "from": "C",
      "to": "D"
    },
    {
      "function": "select",
      "columns": ["B", "E", "D"]
    }
  ]
}
```

> To get table `X`, join tables `Y` and `Z` using `Y.z_id` and `Z.id`

The third, and final, type of operation is saving a table to a file (export):

> Export table `X` to CSV file `A`

For the config file, this example translates to the following JSON object:

```json
{
  "command": "export",
  "source": "table_X",
  "target": {
    "type": "csv",
    "file": "file_A.csv"
  }
}
```

The language to configure this system is, therefore, a sequence of commands which assign imports or expressions to variables or export variables.

Basically our program has a list of commands which it applies on this variables that are in the form of tables.

At this moment the program supports reading a config file only in JSON format and can only export the tables to a csv.



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
