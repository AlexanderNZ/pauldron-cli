package nz.ringfence.pauldron.scanner;

import nz.ringfence.pauldron.model.PauldronComment;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Help.Ansi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    @Option(names = {"-d", "--directory"}, description = "Directory to scan. Default: Current Directory.")
    String directory = System.getProperty("user.dir");

    @Option(names = {"--no-recursion"}, description = "Disables recursive scan when supplied.")
    boolean recursive = true;

    @Option(names = {"-s", "--sort"}, description = "Available values: score-desc (descending), score-asc (ascending). Default: score-desc.")
    String sort = "score-desc";

    @Option(names = {"--verbose"}, description = "Print verbose output.")
    boolean verbose;

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
        List<List<String>> directoryData = readFiles(directory);
        Pattern completeCommentPattern = Pattern.compile("(?<PauldronCommentStart>\\[Pauldron\\].*$)|(?<ShortTitle>\\[Short Title\\].*$)|(?<Context>\\[Context\\].*$)|(?<Impact>\\[Impact\\].*$)|(?<AbsoluteValue>\\[Absolute Value\\].*$)");
        Pattern commentStartPattern = Pattern.compile("(?<PauldronCommentStart>\\[Pauldron\\].*$)");
        Pattern shortTitlePattern = Pattern.compile("(?<ShortTitle>\\[Short Title\\].*$)");
        Pattern impactPattern = Pattern.compile("(?<Impact>\\[Impact\\].*$)");
        Pattern contextPattern = Pattern.compile("(?<Context>\\[Context\\].*$)");
        Pattern absoluteValuePattern = Pattern.compile("(?<AbsoluteValue>\\[Absolute Value\\].*$)");

        // Raw comments are ones that contain a line of text that matches a regex above, no processing applied.
        List<String> rawPauldronCommentList = new ArrayList<>();

        // Processed comments have had all content before the closing square brace removed.
        Set<PauldronComment> processedCauldronCommentsList = new HashSet<>();

        directoryData.forEach(file -> {
            extractLinesFromAllFiles(completeCommentPattern, rawPauldronCommentList, file);
            convertCommentStringsToPauldronCommentObjects(
                    commentStartPattern,
                    shortTitlePattern,
                    impactPattern,
                    contextPattern,
                    absoluteValuePattern,
                    rawPauldronCommentList,
                    processedCauldronCommentsList);
        });

        processedCauldronCommentsList.stream()
                .map(ScanCommand::formatQuestion)
                .forEach(System.out::println);
        return 0;
    }

    private void convertCommentStringsToPauldronCommentObjects(
            Pattern commentStartPattern,
            Pattern shortTitlePattern,
            Pattern impactPattern,
            Pattern contextPattern,
            Pattern absoluteValuePattern,
            List<String> rawPauldronCommentList,
            Set<PauldronComment> processedCauldronCommentsList) {
        PauldronComment comment = new PauldronComment();

        for (String pauldronCommentLine : rawPauldronCommentList) {
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
                    processedCauldronCommentsList.add(comment);
                }
                comment = new PauldronComment();
            }
        }
    }

    private void extractLinesFromAllFiles(Pattern completeCommentPattern, List<String> rawPauldronCommentList, List<String> file) {
        file.forEach(line -> {
            Matcher matcher = completeCommentPattern.matcher(line);
            if (matcher.find()) {
                rawPauldronCommentList.add(line);
            }
        });
    }

    static private String formatQuestion(final PauldronComment comment) {
        return Ansi.AUTO.string(String.format(
                "@|bold,green %s |@\n" +
                        "@|bold,green Context: |@ %s\n" +
                        "@|bold,green Impact: |@ %s\n" +
                        "@|bold,green Absolute Value: |@ %d \n",
                comment.getShortTitle(),
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

    private List<List<String>> readFiles(String inputDirectory) {
        List<Path> files = new ArrayList<>();

        if (recursive) {
            try (Stream<Path> walk = Files.walk(Paths.get(inputDirectory))) {
                List<String> result = walk.filter(Files::isRegularFile)
                        .map(Path::toString).collect(Collectors.toList());

                result.forEach(item -> files.add(Path.of(item)));

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (Stream<Path> stream = Files.walk(Paths.get(inputDirectory), 1)) {
                List<String> result = stream
                        .filter(file -> !Files.isDirectory(file))
                        .map(Path::toString)
                        .collect(Collectors.toList());

                result.forEach(item -> files.add(Path.of(item)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<List<String>> filesAndStrings = new ArrayList<>();

        // Then for each file in the specified directory, create a list of strings in the file
        files.forEach(file -> {
            List<String> returnedLines = new ArrayList<>();
            try (Stream<String> lines = Files.lines(Path.of(file.toString()), StandardCharsets.UTF_8)) {
                returnedLines = lines.collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            filesAndStrings.add(returnedLines);
        });
        return filesAndStrings;
    }
}
