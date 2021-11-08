## Model and Language Description

Our model is based on functions and expressions, with very direct semantics, no control flow, and
simple I/O mechanisms.

The first type of operation you can do is load a table from a file (import):

> To get table `X`, import element `Y` from XML files `A`, `B`, and `C`

The second type of operation you can do is create a new table as a function of existing tables
(transforms and combinators):

> To get table `X`, apply functions `rename(A, B)`, `rename(C, D)`, and `select(B, E, D)` to table
> `Y`

> To get table `X`, join tables `Y` and `Z` using `Y.z_id` and `Z.id`

The third, and final, type of operation is saving a table to a file (export):

> Export table `X` to CSV file `A`

The language to configure this system is, therefore, a sequence of commands which assign imports or
expressions to variables or export variables.

## Reasoning

We chose to model our system this way because it provides several benefits:

 * Immutability - by not mutating tables in place, but only providing functions that take existing
   tables and produce new tables (whether these tables are inside the system or are to be read from
   or written to files), we reduce the ammount of confusion about the shape of the tables the user
   is working with.
 * Partial evaluation - by dividing each directive in the configuration language into a
   self-contained commands that can be incrementaly evaluated, we lay the groundwork to easily
   implement several features, such as REPLs or debuggers.
 * Strictly declarative - by separating the configuration from the execution of the system, we open
   a lot of ground for adapting the implementation without breaking the user's abstraction. Not only
   that, our model allows for a measure of 'query planning' to potentially be implemented, such as:
    - Not performing operations that are not dependencies of exported tables (lazy evaluation);
    - Factoring common expressions to deduplicate work;
    - Reordering operations when the result is not affected;
    - Combining operations when that is advantageous;
    - Paralelizing unrelated operations;
    - Propagating validation requirements to the importers (e.g. validating that a column that will
      be used in an average only contains numbers as soon as it is imported from the source file).
