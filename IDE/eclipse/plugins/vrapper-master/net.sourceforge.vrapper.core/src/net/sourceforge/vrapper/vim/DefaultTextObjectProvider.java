package net.sourceforge.vrapper.vim;

import static net.sourceforge.vrapper.keymap.StateUtils.union;
import static net.sourceforge.vrapper.keymap.vim.ConstructorWrappers.leafBind;
import static net.sourceforge.vrapper.keymap.vim.ConstructorWrappers.state;
import static net.sourceforge.vrapper.keymap.vim.ConstructorWrappers.transitionBind;
import net.sourceforge.vrapper.keymap.EmptyState;
import net.sourceforge.vrapper.keymap.State;
import net.sourceforge.vrapper.keymap.vim.CountingState;
import net.sourceforge.vrapper.keymap.vim.DelimitedTextObjectState;
import net.sourceforge.vrapper.keymap.vim.MatchAdHocDelimitedTextState;
import net.sourceforge.vrapper.keymap.vim.TextObjectState;
import net.sourceforge.vrapper.vim.commands.DelimitedText;
import net.sourceforge.vrapper.vim.commands.MotionPairTextObject;
import net.sourceforge.vrapper.vim.commands.QuoteDelimitedText;
import net.sourceforge.vrapper.vim.commands.SimpleDelimitedText;
import net.sourceforge.vrapper.vim.commands.TextObject;
import net.sourceforge.vrapper.vim.commands.XmlTagDelimitedText;
import net.sourceforge.vrapper.vim.commands.motions.Motion;
import net.sourceforge.vrapper.vim.commands.motions.MoveBigWORDEndRight;
import net.sourceforge.vrapper.vim.commands.motions.MoveBigWORDLeft;
import net.sourceforge.vrapper.vim.commands.motions.MoveBigWORDRight;
import net.sourceforge.vrapper.vim.commands.motions.MoveWordEndRight;
import net.sourceforge.vrapper.vim.commands.motions.MoveWordLeft;
import net.sourceforge.vrapper.vim.commands.motions.MoveWordRight;
import net.sourceforge.vrapper.vim.commands.motions.MoveWordRightForUpdate;
import net.sourceforge.vrapper.vim.commands.motions.ParagraphMotion.ParagraphTextObject;
import net.sourceforge.vrapper.vim.modes.CommandBasedMode;

public class DefaultTextObjectProvider implements TextObjectProvider {
    private static State<DelimitedText> coreDelimitedTexts;
    private static State<Motion> coreTextMotions;

    private State<DelimitedText> delimitedTexts = coreDelimitedTexts;
    private State<TextObject> textObjects;
    
    static {
        final DelimitedText inBracket = new SimpleDelimitedText('(', ')');
        final DelimitedText inSquareBracket = new SimpleDelimitedText('[', ']');
        final DelimitedText inBrace = new SimpleDelimitedText('{', '}');
        final DelimitedText inAngleBrace = new SimpleDelimitedText('<', '>');
        final DelimitedText inString = new QuoteDelimitedText('"');
        final DelimitedText inGraveString = new QuoteDelimitedText('`');
        final DelimitedText inChar = new QuoteDelimitedText('\'');
        final DelimitedText inTag = new XmlTagDelimitedText();

        @SuppressWarnings("unchecked")
        State<DelimitedText> delimitedTexts = state(
                leafBind('b', inBracket),
                leafBind('(', inBracket),
                leafBind(')', inBracket),
                leafBind('[', inSquareBracket),
                leafBind(']', inSquareBracket),
                leafBind('B', inBrace),
                leafBind('{', inBrace),
                leafBind('}', inBrace),
                leafBind('<', inAngleBrace),
                leafBind('>', inAngleBrace),
                leafBind('t', inTag),
                leafBind('"', inString),
                leafBind('\'', inChar),
                leafBind('`', inGraveString),
                transitionBind('m', MatchAdHocDelimitedTextState.INSTANCE));
        coreDelimitedTexts = delimitedTexts;
        // override the default motions for a few motions that act differently
        // in text mode
        @SuppressWarnings("unchecked")
        State<Motion> textMotions = union(
                        state(
                            leafBind('w', MoveWordRightForUpdate.MOVE_WORD_RIGHT_INSTANCE),
                            leafBind( 'W', MoveWordRightForUpdate.MOVE_BIG_WORD_RIGHT_INSTANCE)),
                            CommandBasedMode.motions());
        coreTextMotions = textMotions;
    }
    
    public DefaultTextObjectProvider() {
        updateTextObjects(EmptyState.<TextObject>getInstance());
    }

    @Override
    public State<DelimitedText> delimitedTexts() {
        return delimitedTexts;
    }
    
    @Override
    public State<TextObject> textObjects() {
        return textObjects;
    }

    @SuppressWarnings("unchecked")
    public void updateDelimitedTexts(State<DelimitedText> platformTexts) {
        delimitedTexts = union(platformTexts, coreDelimitedTexts);
    }
    
    @SuppressWarnings("unchecked")
    public void updateTextObjects(State<TextObject> platformTextObjects) {
        final TextObject innerWord = new MotionPairTextObject(MoveWordLeft.BAILS_OFF, MoveWordEndRight.BAILS_OFF);
        final TextObject aWord = new MotionPairTextObject(MoveWordLeft.BAILS_OFF, MoveWordRight.BAILS_OFF);
        final TextObject innerWORD = new MotionPairTextObject(MoveBigWORDLeft.BAILS_OFF, MoveBigWORDEndRight.BAILS_OFF);
        final TextObject aWORD = new MotionPairTextObject(MoveBigWORDLeft.BAILS_OFF, MoveBigWORDRight.BAILS_OFF);
        final TextObject innerParagraph = new ParagraphTextObject(false);
        final TextObject aParagraph = new ParagraphTextObject(true);

        textObjects = union(
                    platformTextObjects,
                    state(
                        transitionBind('i', union(
                                state(  leafBind('w', innerWord),
                                        leafBind('W', innerWORD),
                                        leafBind('p', innerParagraph)
                                ),
                                new DelimitedTextObjectState(delimitedTexts, DelimitedTextObjectState.INNER))),
                        transitionBind('a', union(
                                state(
                                        leafBind('w', aWord),
                                        leafBind('W', aWORD),
                                        leafBind('p', aParagraph)
                                ),
                                new DelimitedTextObjectState(delimitedTexts, DelimitedTextObjectState.OUTER)))),
                    new TextObjectState(coreTextMotions));
        textObjects = CountingState.wrap(textObjects);
    }
}
