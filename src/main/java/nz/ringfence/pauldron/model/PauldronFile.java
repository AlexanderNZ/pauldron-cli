package nz.ringfence.pauldron.model;

import java.nio.file.Path;
import java.util.*;

public class PauldronFile {
    List<String> rawPauldronCommentList = new ArrayList<>();
    Set<PauldronComment> processedCauldronCommentsList = new HashSet<>();
    Path filePath;

    public PauldronFile() {
    }

    public List<String> getRawPauldronCommentList() {
        return rawPauldronCommentList;
    }

    public void setRawPauldronCommentList(List<String> rawPauldronCommentList) {
        this.rawPauldronCommentList = rawPauldronCommentList;
    }

    public Set<PauldronComment> getProcessedCauldronCommentsList() {
        return processedCauldronCommentsList;
    }

    public void setProcessedCauldronCommentsList(Set<PauldronComment> processedCauldronCommentsList) {
        this.processedCauldronCommentsList = processedCauldronCommentsList;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "PauldronFile{" +
                "rawPauldronCommentList=" + rawPauldronCommentList +
                ", processedCauldronCommentsList=" + processedCauldronCommentsList +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PauldronFile that = (PauldronFile) o;
        return Objects.equals(getRawPauldronCommentList(), that.getRawPauldronCommentList()) &&
                Objects.equals(getProcessedCauldronCommentsList(), that.getProcessedCauldronCommentsList()) &&
                Objects.equals(getFilePath(), that.getFilePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRawPauldronCommentList(), getProcessedCauldronCommentsList(), getFilePath());
    }
}
