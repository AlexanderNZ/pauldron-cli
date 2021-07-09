package nz.ringfence.pauldron.scanner;

import nz.ringfence.pauldron.model.PauldronComment;
import nz.ringfence.pauldron.model.PauldronFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Help.Ansi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * [Pauldron]
 * [Short Title] The Scan Subcommand
 * [Context] This class controls all scanning functionality. It's important!
 * [Impact] Problems here will affect all scanning
 * [Absolute Value] 10
 * [Pauldron]
 */
@Command(name = "scan", description = "Scans for comments about notable code. For an example comment block, see X. 0 returns indicate that no valid comments were found.",
        mixinStandardHelpOptions = true)
public class ScanCommand implements Callable<Integer> {
    @Option(names = {"-d", "--directory"},
//            arity = "0",
            description = "Directory to scan. Default: Current Directory.")
    String directory = System.getProperty("user.dir");

    @Option(names = "--no-recursion", negatable = true, description = "Disables recursive scan when supplied. Default: True")
    boolean recursive = true;

    @Option(names = {"-s", "--sort"}, description = "Available values: score-desc (descending), score-asc (ascending). Default: score-desc.")
    String sort = "score-desc";

    @Option(names = "--verbose", negatable = true, description = "Print verbose output. Default: False")
    boolean verbose = false;

    private static final Logger logger = LoggerFactory.getLogger(ScanCommand.class);

    /**
     * [Pauldron]
     * [Short Title] The Run function
     * [Context] Takes input from the user and returns a list of entries
     * [Impact] This could get very messy in future
     * [Absolute Value] 8
     * [Pauldron]
     */
    @Override
    public Integer call() throws Exception {
        printInputs();

        // First we need to set up all of the matchers that we'll use to find pauldron comments
        Pattern commentStartPattern = Pattern.compile("(?<PauldronCommentStart>\\[Pauldron\\].*$)");
        Pattern shortTitlePattern = Pattern.compile("(?<ShortTitle>\\[Short Title\\].*$)");
        Pattern impactPattern = Pattern.compile("(?<Impact>\\[Impact\\].*$)");
        Pattern contextPattern = Pattern.compile("(?<Context>\\[Context\\].*$)");
        Pattern absoluteValuePattern = Pattern.compile("(?<AbsoluteValue>\\[Absolute Value\\].*$)");

        ScanResultsCollection scanResults = readFiles(directory);

        scanResults.successfullyParsedFiles().forEach(file -> {
            convertCommentStringsToPauldronCommentObjects(
                    commentStartPattern,
                    shortTitlePattern,
                    impactPattern,
                    contextPattern,
                    absoluteValuePattern,
                    file);

            // TODO: Modify this so that all comments in one file are printed in asc | desc order by absolute value
            //  before moving on to the next file. May also be worthwhile sorting files by sum(absolute value)
            //  i.e. File 1 has 2 comments, abs_v of 1 and 3, file 1 is worth 4 total.
            //  File 2 has 3 comments, abs_v 1, 2, 3. Total of 6.
            //  Print order is File 2, comment 3, 2, 1 followed by file 1, comment 3, 1
            file.getProcessedCauldronCommentsList().stream()
                    .map(ScanCommand::formatPauldronComment)
                    .forEach(System.out::println);
        });

        if (!scanResults.filesThatCouldNotBeRead().isEmpty()) {


            // if verbose, print out much more detail about the files that failed to parse
            if (verbose) {
                System.out.println(Ansi.AUTO.string(String.format(
                        "@|bold,red Failed to parse the following files: |@\n"
                )));
                scanResults.filesThatCouldNotBeRead().forEach(path -> {
                    System.out.println(Ansi.AUTO.string(String.format(
                            "@|red %s |@", path)
                    ));
                });
            }
            // if not verbose, just print that there were errors, and inform the user about the verbose function
            else {
                System.out.println(Ansi.AUTO.string(String.format(
                        "@|bold,red Some files failed to parse. To see more information, enable verbose mode by appending '--verbose' to your command. |@"
                )));
            }
        }

        return 0;
    }

    private void convertCommentStringsToPauldronCommentObjects(
            Pattern commentStartPattern,
            Pattern shortTitlePattern,
            Pattern impactPattern,
            Pattern contextPattern,
            Pattern absoluteValuePattern,
            PauldronFile commentFile) {
        PauldronComment comment = new PauldronComment();

        for (String pauldronCommentLine : commentFile.getRawPauldronCommentList()) {
            Matcher commentStartMatcher = commentStartPattern.matcher(pauldronCommentLine);
            Matcher shortTitleMatcher = shortTitlePattern.matcher(pauldronCommentLine);
            Matcher impactMatcher = impactPattern.matcher(pauldronCommentLine);
            Matcher contextMatcher = contextPattern.matcher(pauldronCommentLine);
            Matcher absoluteValueMatcher = absoluteValuePattern.matcher(pauldronCommentLine);
            int startOfPauldronCommentContent = pauldronCommentLine.lastIndexOf(']') + 2;

            if (shortTitleMatcher.find()) {
                comment.setShortTitle(pauldronCommentLine.substring(startOfPauldronCommentContent));
            } else if (impactMatcher.find()) {
                comment.setImpact(pauldronCommentLine.substring(startOfPauldronCommentContent));
            } else if (contextMatcher.find()) {
                comment.setContext(pauldronCommentLine.substring(startOfPauldronCommentContent));
            } else if (absoluteValueMatcher.find()) {
                comment.setAbsoluteValue(Integer.parseInt(pauldronCommentLine.substring(startOfPauldronCommentContent)));
            } else if (commentStartMatcher.find()) {
                if (comment.getShortTitle() != null && !comment.getShortTitle().isEmpty()) {
                    comment.setParentFilePath(commentFile.getFilePath());
                    commentFile.getProcessedCauldronCommentsList().add(comment);
                }
                comment = new PauldronComment();
            }
        }
    }

    static private String formatPauldronComment(final PauldronComment comment) {
        return Ansi.AUTO.string(String.format(
                "@|bold,green %s|@\n" +
                        "File: %s\n" +
                        "Context: %s\n" +
                        "@|green Impact: |@%s\n" +
                        "@|bold,green Absolute Value: |@%d\n",
                comment.getShortTitle(),
                comment.getParentFilePath().toString(),
                comment.getContext(),
                comment.getImpact(),
                comment.getAbsoluteValue()
        ));
    }

    private void printInputs() {
        if (verbose) {
            System.out.printf(
                    "\nDirectory to scan: %s | Recursive scan: %s | Sort scheme: %s | Verbose: %s\n\n",
                    directory,
                    recursive,
                    sort,
                    verbose
            );
        } else {
            System.out.printf(
                    "\nDirectory to scan: %s | Sort scheme: %s\n\n",
                    directory,
                    sort
            );
        }
    }

    private ScanResultsCollection readFiles(String inputDirectory) {
        Set<Path> filesThatCouldNotBeRead = new HashSet<>();
        Set<PauldronFile> pauldronFilesToBeScanned = new HashSet<>();
        Set<PauldronFile> successfullyParsedFiles = new HashSet<>();

        if (recursive) {
            try (Stream<Path> walk = Files.walk(Paths.get(inputDirectory))) {
                List<String> result = walk.filter(Files::isRegularFile)
                        .map(Path::toString).collect(Collectors.toList());

                result.forEach(item -> {
                    PauldronFile pauldronFile = new PauldronFile();
                    pauldronFile.setFilePath(Path.of(item));
                    pauldronFilesToBeScanned.add(pauldronFile);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (Stream<Path> stream = Files.walk(Paths.get(inputDirectory), 1)) {
                List<String> result = stream
                        .filter(file -> !Files.isDirectory(file))
                        .map(Path::toString)
                        .collect(Collectors.toList());

                result.forEach(item -> {
                    PauldronFile pauldronFile = new PauldronFile();
                    pauldronFile.setFilePath(Path.of(item));
                    pauldronFilesToBeScanned.add(pauldronFile);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Then for each file in the specified directory, create a list of strings in the file
        pauldronFilesToBeScanned.forEach(file -> {
            List<String> returnedLines = new ArrayList<>();
            try {
                returnedLines = Files.readAllLines(Path.of(file.getFilePath().toString()), StandardCharsets.UTF_8);
                file.setRawPauldronCommentList(returnedLines);
                successfullyParsedFiles.add(file);
            } catch (IOException e) {
                filesThatCouldNotBeRead.add(file.getFilePath());
            }
        });

        ScanResultsCollection scanResultsCollection = new ScanResultsCollection(successfullyParsedFiles, filesThatCouldNotBeRead);

        return scanResultsCollection;
    }
}
