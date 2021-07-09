# Pauldron

A CLI and comment tool that helps us talk about code.

Use Pauldron to:
* Reduce the time it takes to onboard newbies into large codebases
* Call out idiomatic sections of code in your codebase
* Highlight technical debt, code smells or other issues

## Developing

Pauldron is a CLI tool written in Java 11 with Micronaut and PicoCLI, backed by ~~ANTLR4 lexers and parsers~~ regex lol.

To get started:

1. Ensure you have the GraalVM Java 11 SDK installed (version `20.1.0.r11` can be easily installed via SDKMAN)
2. Clone the codebase
3. Import the project into your IDE of choice.
5. Use this handy one liner to build and run the CLI application (command and options are just an example):

> `./gradlew assemble && java -jar build/libs/cli-0.1-all.jar scan -d src -s score-asc --no-recursion --verbose`

Alternatively, run the tests [here](src/test/java/nz/ringfence/pauldron/scanner/ScanCommandTest.java)