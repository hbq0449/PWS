package net.sourceforge.vrapper.eclipse.platform;

import net.sourceforge.vrapper.platform.TextContent;
import net.sourceforge.vrapper.utils.LineInformation;
import net.sourceforge.vrapper.utils.Space;
import net.sourceforge.vrapper.utils.TextRange;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.swt.custom.StyledText;

@SuppressWarnings("nls")
public class EclipseTextContent {

    protected final ITextViewer textViewer;
    protected ITextViewerExtension5 converter;
    protected TextContent modelSide;
    protected TextContent viewSide;
    protected int horizontalPosition;
    protected boolean lineWiseMouseSelection;

    public EclipseTextContent(ITextViewer textViewer) {
        this.textViewer = textViewer;
        this.converter = OffsetConverter.create(textViewer);
        modelSide = new ModelSideTextContent();
        viewSide = new ViewSideTextContent();
    }

    public TextContent getModelContent() {
        return modelSide;
    }

    public TextContent getViewContent() {
        return viewSide;
    }

    protected class ModelSideTextContent implements TextContent {

        public LineInformation getLineInformation(int line) {
            try {
                IRegion region = textViewer.getDocument().getLineInformation(
                        line);
                return new LineInformation(line, region.getOffset(), region
                        .getLength());
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }

        }

        public LineInformation getLineInformationOfOffset(int offset) {
            int line;
            try {
                line = textViewer.getDocument().getLineOfOffset(offset);
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
            return getLineInformation(line);
        }

        public int getNumberOfLines() {
            return textViewer.getDocument().getNumberOfLines();
        }

        public int getTextLength() {
            return textViewer.getDocument().getLength();
        }

        public String getText(int index, int length) {
            try {
                return textViewer.getDocument().get(index, length);
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }

        public String getText(TextRange range) {
            return getText(range.getLeftBound().getModelOffset(), range.getModelLength());
        }

        public void replace(int index, int length, String s) {
            try {
                IDocument doc = textViewer.getDocument();
                if (index > doc.getLength()) {
                    index = doc.getLength();
                }
                doc.replace(index, length, s);
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }

        public void smartInsert(int index, String s) {
            viewSide.smartInsert(converter.modelOffset2WidgetOffset(index), s);
        }

        public void smartInsert(String s) {
            viewSide.smartInsert(s);
        }

        public Space getSpace() {
            return Space.MODEL;
        }

    }

    protected class ViewSideTextContent implements TextContent  {

        public LineInformation getLineInformation(int line) {
            line = converter.widgetLine2ModelLine(line);
            IRegion region;
            try {
                region = textViewer.getDocument().getLineInformation(line);
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
            return new LineInformation(converter.modelLine2WidgetLine(line),
                    converter.modelOffset2WidgetOffset(region.getOffset()), region.getLength());
        }

        public LineInformation getLineInformationOfOffset(int offset) {
            int line = textViewer.getTextWidget().getLineAtOffset(offset);
            return getLineInformation(line);
        }

        public int getNumberOfLines() {
            return textViewer.getTextWidget().getLineCount();
        }

        public int getTextLength() {
            return textViewer.getTextWidget().getCharCount();
        }

        public String getText(int index, int length) {
            return textViewer.getTextWidget().getText(index, index + length - 1);
        }

        public String getText(TextRange range) {
            return getText(range.getLeftBound().getViewOffset(), range.getViewLength());
        }

        public void replace(int index, int length, String text) {
            // XXX: it was illegal in Vrapper. Why?
            textViewer.getTextWidget().replaceTextRange(index, length, text);
        }

        public void smartInsert(int index, String s) {
            StyledText textWidget = textViewer.getTextWidget();
            int oldIndex = textWidget.getCaretOffset();
            textWidget.setCaretOffset(index);
            textWidget.insert(s);
            if(oldIndex > 1 && textWidget.getTextRange(oldIndex-1, 2).equals("\r\n")) {
            	//can't put cursor between \r and \n of a Windows newline
            	oldIndex++;
            }
            textWidget.setCaretOffset(oldIndex);
        }

        public void smartInsert(String s) {
            StyledText textWidget = textViewer.getTextWidget();
            int pos = textWidget.getCaretOffset();
            // move caret after insertion to preserve position
            if (pos < textWidget.getCharCount()) {
            	try {
            		textWidget.setCaretOffset(pos+1);
	                textWidget.replaceTextRange(pos, 0, s);
	                textWidget.setCaretOffset(textWidget.getCaretOffset()-1);
            	}
            	catch (IllegalArgumentException e) {
            		/**
            		 * This exception should only happen if the cursor is on the
            		 * end of a line and the newlines are multi-byte characters.
            		 * Which is to say, Windows (\r\n).  If this happens, step
            		 * back one character and try again.
            		 */
            		textWidget.setCaretOffset(pos);
	                textWidget.replaceTextRange(pos, 0, s);
	                textWidget.setCaretOffset(textWidget.getCaretOffset()+1);
				}
            } else {
                textWidget.replaceTextRange(pos, 0, s);
                textWidget.setCaretOffset(textWidget.getCharCount());
            }
        }

        public Space getSpace() {
            return Space.VIEW;
        }
    }
}
