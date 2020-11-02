package nz.ringfence.pauldron.model;

import java.nio.file.Path;
import java.util.Objects;

/**
 * [Pauldron]
 * [Short Title] Pauldron Comment Declaration
 * [Context] In real life I probably wouldn't document this, it's just a model
 * [Impact] Minimal
 * [Absolute Value] 2
 * [Pauldron]
 */
public class PauldronComment {
    String shortTitle;
    Path parentFilePath;
    String impact;
    String context;
    Integer absoluteValue;

    public PauldronComment() {
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public Path getParentFilePath() {
        return parentFilePath;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public void setParentFilePath(Path parentFilePath) {
        this.parentFilePath = parentFilePath;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Integer getAbsoluteValue() {
        return absoluteValue;
    }

    public void setAbsoluteValue(Integer absoluteValue) {
        this.absoluteValue = absoluteValue;
    }

    @Override
    public String toString() {
        return "PauldronComment{" +
                "shortTitle='" + shortTitle + '\'' +
                ", context='" + context + '\'' +
                ", absoluteValue=" + absoluteValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PauldronComment that = (PauldronComment) o;
        return Objects.equals(getShortTitle(), that.getShortTitle()) &&
                Objects.equals(getContext(), that.getContext()) &&
                Objects.equals(getAbsoluteValue(), that.getAbsoluteValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShortTitle(), getContext(), getAbsoluteValue());
    }
}