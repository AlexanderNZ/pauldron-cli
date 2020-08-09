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

        String[] args = {"-d", "currentDir", "-s", "sort-asc"};
        PicocliRunner.call(ScanCommand.class, ctx, args);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(baos.toString()).as("as description").isEqualTo("\nDirectory to scan: currentDir | Sort scheme: sort-asc\n");
        });
    }

    @Test
    void callVerbose() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST);

        String[] args = {"-d", "currentDir", "-s", "sort-asc", "--verbose", "--no-recursion"};
        PicocliRunner.call(ScanCommand.class, ctx, args);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(baos.toString()).as("as description").isEqualTo("\nDirectory to scan: currentDir | Recursive scan disabled: true | Sort scheme: sort-asc | Verbose: true\n");
        });
    }
}