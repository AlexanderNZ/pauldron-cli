package nz.ringfence.pauldron;

import nz.ringfence.pauldron.scanner.ScanCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * [Pauldron]
 * [Short Title] Main Class
 * [Context] Provides a wrapper over the rest of the cli and provides top level information
 * [Impact] Minimal
 * [Absolute Value] 2
 * [Pauldron]
 */
@Command(name = "pauldron",
        description = "Pauldron helps you talk about technical debt.",
        version = "0.0.1",
        subcommands = {ScanCommand.class},
        mixinStandardHelpOptions = true)
public class PauldronCLI implements Callable<Integer> {

    @Option(names = {"-v", "--verbose"}, description = "Prints additional detail")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        int exitCode = new CommandLine(new PauldronCLI()).execute(args);
        System.exit(exitCode);
    }

    public Integer call() throws Exception {
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
        }
        return 0;
    }
}
