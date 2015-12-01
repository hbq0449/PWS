package net.sourceforge.vrapper.eclipse.platform;

import net.sourceforge.vrapper.utils.AbstractPosition;
import net.sourceforge.vrapper.utils.Position;
import net.sourceforge.vrapper.utils.StartEndTextRange;

import org.eclipse.jface.text.ITextViewer;

public class StickyColumnPosition extends AbstractPosition {

    private final ITextViewer textViewer;
    private final Position lineBegin;
    private final int stickyColumn;

    public StickyColumnPosition(ITextViewer textViewer, Position lineBegin, int stickyColumn) {
        this.textViewer = textViewer;
        this.lineBegin = lineBegin;
        this.stickyColumn = stickyColumn;
    }

    public Position addModelOffset(int offset) {
        return new StickyColumnPosition(textViewer, lineBegin, getModelOffset() + offset);
    }

    public Position addViewOffset(int offset) {
        return new StickyColumnPosition(textViewer, lineBegin, getViewOffset() + offset);
    }

    public int getModelOffset() {
        return new StartEndTextRange(lineBegin, lineBegin.addViewOffset(stickyColumn)).getModelLength();
    }

    public int getViewOffset() {
        return stickyColumn;
    }

    public Position setModelOffset(int offset) {
        throw new UnsupportedOperationException("setting of offsets not supported");
    }

    public Position setViewOffset(int offset) {
        throw new UnsupportedOperationException("setting of offsets not supported");
    }

}
