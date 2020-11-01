package nz.ringfence.pauldron.model;

public final class PauldronCommentBuilder {
    String shortTitle;
    String context;
    Integer absoluteValue;

    private PauldronCommentBuilder() {
    }

    public static PauldronCommentBuilder aPauldronComment() {
        return new PauldronCommentBuilder();
    }

    public PauldronCommentBuilder withShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
        return this;
    }

    public PauldronCommentBuilder withContext(String context) {
        this.context = context;
        return this;
    }

    public PauldronCommentBuilder withAbsoluteValue(Integer absoluteValue) {
        this.absoluteValue = absoluteValue;
        return this;
    }

    public PauldronComment build() {
        PauldronComment pauldronComment = new PauldronComment();
        pauldronComment.setShortTitle(shortTitle);
        pauldronComment.setContext(context);
        pauldronComment.setAbsoluteValue(absoluteValue);
        return pauldronComment;
    }
}
