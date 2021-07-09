package nz.ringfence.pauldron.scanner;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class ScanCommandTest {

    @Test
    void call() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST);

        String[] args = {"-d", "src/test/java/nz/ringfence/pauldron/commentSamples", "-s", "sort-asc"};
        PicocliRunner.call(ScanCommand.class, ctx, args);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(baos.toString()).as("CLI Output").isEqualTo(
                    """
                            
                            Directory to scan: src/test/java/nz/ringfence/pauldron/commentSamples | Sort scheme: sort-asc
                                                        
                            Level One A
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/levelOne/LevelOneSampleA.java
                            Context: Next Level
                            Impact: Minimal
                            Absolute Value: 2
                                                        
                            Level Two A
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/levelOne/levelTwo/LevelTwoSampleA.java
                            Context: Next Level
                            Impact: Minimal
                            Absolute Value: 5
                                                        
                            Top Level
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/TopLevelSample.java
                            Context: Entry to the application
                            Impact: Minimal
                            Absolute Value: 1
                                                        
                            Level One C
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/levelOne/LevelOneSampleC.java
                            Context: Next Level
                            Impact: Minimal
                            Absolute Value: 4
                                                        
                            Level One B
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/levelOne/LevelOneSampleB.java
                            Context: Next Level
                            Impact: Minimal
                            Absolute Value: 3
                            
                            """
            );
        });
    }

    @Test
    void callVerbose() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST);

        String[] args = {"-d", "src/test/java/nz/ringfence/pauldron/commentSamples", "-s", "sort-asc", "--verbose", "--no-recursion"};
        PicocliRunner.call(ScanCommand.class, ctx, args);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(baos.toString()).as("CLI Output").isEqualTo("\n" +
                    "Directory to scan: src/test/java/nz/ringfence/pauldron/commentSamples | Recursive scan: false | Sort scheme: sort-asc | Verbose: true\n" +
                    "\n" +
                    "Top Level\n" +
                    "File: src/test/java/nz/ringfence/pauldron/commentSamples/TopLevelSample.java\n" +
                    "Context: Entry to the application\n" +
                    "Impact: Minimal\n" +
                    "Absolute Value: 1\n" +
                    "\n");
        });
    }

    @Test
    void callVerboseRecursively() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST);

        String[] args = {"-d", "src/test/java/nz/ringfence/pauldron/commentSamples", "-s", "sort-asc", "--verbose"};
        PicocliRunner.call(ScanCommand.class, ctx, args);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(baos.toString()).as("CLI Output").isEqualTo(
                    """
                            
                            Directory to scan: src/test/java/nz/ringfence/pauldron/commentSamples | Recursive scan: true | Sort scheme: sort-asc | Verbose: true
                                                        
                            Level One A
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/levelOne/LevelOneSampleA.java
                            Context: Next Level
                            Impact: Minimal
                            Absolute Value: 2
                                                        
                            Level Two A
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/levelOne/levelTwo/LevelTwoSampleA.java
                            Context: Next Level
                            Impact: Minimal
                            Absolute Value: 5
                                                        
                            Top Level
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/TopLevelSample.java
                            Context: Entry to the application
                            Impact: Minimal
                            Absolute Value: 1
                                                        
                            Level One C
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/levelOne/LevelOneSampleC.java
                            Context: Next Level
                            Impact: Minimal
                            Absolute Value: 4
                                                        
                            Level One B
                            File: src/test/java/nz/ringfence/pauldron/commentSamples/levelOne/LevelOneSampleB.java
                            Context: Next Level
                            Impact: Minimal
                            Absolute Value: 3
                            
                            """
            );
        });
    }
}