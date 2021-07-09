# Pauldron

A CLI and comment tool that helps us talk about code.

Use Pauldron to:
* Reduce the time it takes to onboard newbies into large codebases
* Call out idiomatic sections of code in your codebase
* Highlight technical debt, code smells or other issues

## Developing

Pauldron is a CLI tool written in Java 11 with Micronaut and PicoCLI, backed by ~~ANTLR4 lexers and parsers~~ regex lol.

To get started:

1. Ensure you have Java 16 or higher installed. If not, I recommend you use [sdkman](https://sdkman.io)
2. Clone the codebase
3. Import the project into your IDE of choice.
5. Use this handy one liner to build and run the CLI application (command and options are just an example):

> `./gradlew assemble && java -jar build/libs/cli-0.1-all.jar scan -d src -s score-asc --verbose`

Alternatively, run the tests [here](src/test/java/nz/ringfence/pauldron/scanner/ScanCommandTest.java)

### Native Image Compilation

This tool will be packaged and released as a native image with GraalVM. For more info on GraalVM click [here](https://graalvm.org).

Install Graal and Native Image with:

1. Install the Java 16 GraalVM SDK - sdkman makes this easy as well i.e. `sdk install java 21.1.0.r16-grl`
2. Install GraalVM's native image tooling. `gu install native-image`. Confirm it's working with `native-image --version`.

Compile with:

3. Assemble the CLI package with `./gradlew --no-daemon assemble`
4. Compile the jar to native code with `native-image --no-server -cp build/libs/cli-*-all.jar`

> Compiling to machine code is a computationally expensive process. The performance and program startup times are worth
> it for a CLI tool use case. This process will take some time and will consume a large amount of CPU time.

You should end up with an executable file named 'cli'. To use it you may need to make it executable:

5. `chmod a+x cli`

Finally, give it a scan command as you would the jar - as an example, `./cli scan -s score-asc`