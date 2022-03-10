# ELS Project - Tably data pre-processing language

This project was elaborated during the Software Language Engineering course at FEUP, a course about
the use and development of Domain-Specific Languages.

Tably is a language that allows users to quickly bulk process data files and produce an output for further use.

The authors are:

- João N. Matos (@joaonmatos)
- João Renato Pinto (@joaorenatopinto)
- Murilo Couceiro (@MuriloCouceiro)

It can read data from structured files (XML and JSON), using familiar XPath and Json Pointer syntax to
extract specific nodes, or parse tabular data from text files. Then you are able to perform
relational-like operations on those virtual tables to produce a csv output that is more suitable for
data processing tasks.

**Copyright 2021-2022 João Nuno Matos, João Renato Pinto, and Murilo Couceiro**

**This work is licenced to you under the Apache license, version 2.0.**
**You may consult the terms of the license at the LICENSE file in the root of this repository**

For running this project, you need to have a working Java installation, version 17+.

## Compile and Running

To compile and install the program, run `./gradlew installDist`. This will compile your classes and create a launcher script in the folder `build/install/els2021-g3/bin`. For convenience, there are two script files, one for Windows (`els2021-g3.bat`) and another for Unix (`els2021-g3`), in the root of the repository, that call these scripts.

After compilation, tests will be automatically executed, if any test fails, the build stops. If you want to ignore the tests and build the program even if some tests fail, execute Gradle with flags "-x test".

When creating a Java executable, it is necessary to specify which class that contains a `main()` method should be entry point of the application. This can be configured in the Gradle script with the property `mainClassName`, which by default has the value `pt.up.fe.els2021.Main`.

## Test

To test the program, run `gradle test`. This will execute the build, and run the JUnit tests in the `test` folder. If you want to see output printed during the tests, use the flag `-i` (i.e., `./gradlew test -i`).
You can also see a test report by opening `build/reports/tests/test/index.html`.
