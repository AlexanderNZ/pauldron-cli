package nz.ringfence.pauldron.scanner;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * [Pauldron]
 * [Short Title] The Scan Subcommand
 * [Context] This class controls all scanning functionality. It's important!
 * [Impact] Problems here will affect all scanning
 * [Absolute Value] 10
 */
@Command(name = "scan", description = "Scans for notable code. Currently only supports multi-line comments in Java classes",
        mixinStandardHelpOptions = true)
public class ScanCommand implements Runnable {
    @Option(names = {"-d", "--directory"}, description = "Directory to scan. Default: Current Directory.")
    String directory = "";

    @Option(names = {"--no-recursion"}, description = "Disables recursive scan when supplied.")
    boolean recursive;

    @Option(names = {"-s", "--sort"}, description = "Available values: score-desc (descending), score-asc (ascending). Default: score-desc.")
    String sort = "score-desc";

    @Option(names = {"--verbose"}, description = "Print verbose output.")
    boolean verbose;

    /**
     * [Pauldron]
     * [Short Title] Run function
     * [Context] Takes input from the user and returns a list of entries
     * [Impact] This could get very messy in future
     * [Absolute Value] 8
     */
    public void run() {
        if (verbose) {
            System.out.printf(
                    "\nDirectory to scan: %s | Recursive scan disabled: %s | Sort scheme: %s | Verbose: %s\n",
                    directory,
                    recursive,
                    sort,
                    verbose
            );
        } else {
            System.out.printf(
                    "\nDirectory to scan: %s | Sort scheme: %s\n",
                    directory,
                    sort
            );
        }

    }
}
