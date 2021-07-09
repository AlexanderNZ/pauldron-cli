package nz.ringfence.pauldron.scanner;

import nz.ringfence.pauldron.model.PauldronFile;

import java.nio.file.Path;
import java.util.Set;

public record ScanResultsCollection(Set<PauldronFile> successfullyParsedFiles, Set<Path> filesThatCouldNotBeRead) {
}
