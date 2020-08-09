package nz.ringfence.pauldron;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;

import nz.ringfence.pauldron.scanner.ScanCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "pauldron",
        description = "Pauldron helps you talk about technical debt.",
        version = "0.0.1",
        subcommands = {ScanCommand.class},
        mixinStandardHelpOptions = true)
public class CliCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Prints additional detail")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(CliCommand.class, args);
    }

    public void run() {
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
        }
    }
}
