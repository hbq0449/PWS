package net.sourceforge.vrapper.vim.commands;

import net.sourceforge.vrapper.platform.Configuration;
import net.sourceforge.vrapper.platform.CursorService;
import net.sourceforge.vrapper.platform.TextContent;
import net.sourceforge.vrapper.utils.ContentType;
import net.sourceforge.vrapper.utils.LineInformation;
import net.sourceforge.vrapper.utils.Position;
import net.sourceforge.vrapper.utils.StartEndTextRange;
import net.sourceforge.vrapper.utils.TextRange;
import net.sourceforge.vrapper.vim.EditorAdaptor;
import net.sourceforge.vrapper.vim.modes.BlockwiseVisualMode;
import net.sourceforge.vrapper.vim.register.TextBlockRegisterContent;

public class BlockWiseSelection implements Selection {
    
    private final Position from;
    private final Position to;
    private final TextRange range;

    public BlockWiseSelection(final EditorAdaptor editor, final Position from, final Position to) {
        super();
        this.from = from;
        this.to = to;
        this.range = StartEndTextRange.exclusive(from, to);
    }
    
    @Override
    public String getModeName() {
        return BlockwiseVisualMode.NAME;
    }

    @Override
    public TextRange getRegion(final EditorAdaptor editorMode, final int count)
            throws CommandExecutionException {
        return range;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public TextObject withCount(final int count) {
        return this;
    }

    @Override
    public Position getEnd() {
        return range.getEnd();
    }

    @Override
    public Position getLeftBound() {
        return range.getLeftBound();
    }

    @Override
    public int getModelLength() {
        return range.getModelLength();
    }

    @Override
    public Position getRightBound() {
        return range.getRightBound();
    }

    @Override
    public Position getStart() {
        return range.getStart();
    }

    @Override
    public int getViewLength() {
        return range.getViewLength();
    }

    @Override
    public boolean isReversed() {
        return range.isReversed();
    }
    @Override
    public ContentType getContentType(final Configuration configuration) {
        return ContentType.TEXT_RECTANGLE;
    }
    @Override
    public Position getFrom() {
        return from;
    }

    @Override
    public Position getTo() {
        return to;
    }

    /**
     * Text block representation by a line range and a visual (think pixel)
     * range.
     */
    public static final class TextBlock {
        public int startLine;
        public int endLine;
        public int startVisualOffset;
        public int endVisualOffset;
    }

    /**
     * Calculates text block from too positions in text.
     */
    public static TextBlock getTextBlock(final Position from,
            final Position to, final TextContent textContent,
            final CursorService cursorService) {
        final LineInformation fromLine = textContent.getLineInformationOfOffset(from.getModelOffset());
        final LineInformation toLine   = textContent.getLineInformationOfOffset(to.getModelOffset());
        final int fromLineNo = fromLine.getNumber();
        final int toLineNo = toLine.getNumber();
        final TextBlock result = new TextBlock();
        if (fromLineNo < toLineNo) {
            result.startLine = fromLineNo;
            result.endLine = toLineNo;
        } else {
            result.startLine = toLineNo;
            result.endLine = fromLineNo;
        }
        result.endLine = Math.min(result.endLine, textContent.getNumberOfLines() - 1);
        final int fromVOffset = cursorService.getVisualOffset(from);
        final int toVOffset = cursorService.getVisualOffset(to);
        if (fromVOffset < toVOffset) {
            result.startVisualOffset = fromVOffset;
            result.endVisualOffset = toVOffset;
        } else {
            result.startVisualOffset = toVOffset;
            result.endVisualOffset = fromVOffset;
        }
        return result;
    }

    /**
     * Returns text block content for the block specified by range.
     */
    public static TextBlockRegisterContent getTextBlockContent(EditorAdaptor editorAdaptor, TextRange range) {
        final TextContent textContent = editorAdaptor.getModelContent();
        final CursorService cursorService = editorAdaptor.getCursorService();
        final TextBlock textBlock = BlockWiseSelection.getTextBlock(range.getStart(), range.getEnd(),
                editorAdaptor.getModelContent(), cursorService);
        TextBlockRegisterContent blockContent = new TextBlockRegisterContent(textBlock.endVisualOffset - textBlock.startVisualOffset);
        for (int line = textBlock.startLine; line <= textBlock.endLine; ++line) {
            final Position start = cursorService.getPositionByVisualOffset(line, textBlock.startVisualOffset);
            if (start == null) {
                // no characters at the visual offset, yank empty line
                blockContent.appendLine("");
                continue;
            }
            final int startOfs = start.getModelOffset();
            final Position end = cursorService.getPositionByVisualOffset(line, textBlock.endVisualOffset);
            int endOfs;
            if (end == null) {
                // the line is shorter that the end offset
                endOfs = textContent.getLineInformation(line).getEndOffset();
            } else {
                endOfs = end.addModelOffset(1).getModelOffset();
            }
            blockContent.appendLine(textContent.getText(startOfs, endOfs - startOfs));
        }
        return blockContent;
    }


    @Override
    public Position getStartMark(EditorAdaptor defaultEditorAdaptor) {
        return getFrom();
    }

    @Override
    public Position getEndMark(EditorAdaptor defaultEditorAdaptor) {
        return getTo();
    }

    @Override
    public Selection selectMarks(EditorAdaptor adaptor, Position start,
            Position end) {
        return new BlockWiseSelection(adaptor, start, end);
    }
}
