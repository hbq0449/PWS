/**
 *
 */
package net.sourceforge.vrapper.vim.commands;

import net.sourceforge.vrapper.utils.Position;
import net.sourceforge.vrapper.utils.VimUtils;
import net.sourceforge.vrapper.vim.EditorAdaptor;
import net.sourceforge.vrapper.vim.commands.motions.StickyColumnPolicy;
import net.sourceforge.vrapper.vim.modes.EditorMode;
import net.sourceforge.vrapper.vim.modes.InsertMode;
import net.sourceforge.vrapper.vim.modes.NormalMode;
import net.sourceforge.vrapper.vim.modes.TemporaryMode;

public class LeaveVisualModeCommand extends CountIgnoringNonRepeatableCommand {

    public static final LeaveVisualModeCommand INSTANCE = new LeaveVisualModeCommand();

    private LeaveVisualModeCommand() { /* NOP */ }

    public void execute(EditorAdaptor editorAdaptor) throws CommandExecutionException {
        // FIXME: compatibility option: don't set caret offset
        
        // Only save selection when this command is executed - other commands call doIt() as well.
        editorAdaptor.rememberLastActiveSelection();

        Selection selection = editorAdaptor.getSelection();
        
        // Get last position of the cursor.
        Position exitPoint = selection.getTo();

        // Regular selection is 1 position longer on the right side to include last character.
        // Linewise visual mode on the other hand has a 'to' field which can be anywhere on the line
        // so don't move the cursor.
        if ( ! selection.isReversed() && ! (selection instanceof LineWiseSelection)) {
            exitPoint = VimUtils.safeAddModelOffset(editorAdaptor, selection.getTo(), -1, true);
        }
        editorAdaptor.setPosition(exitPoint, StickyColumnPolicy.ON_CHANGE);

        doIt(editorAdaptor);
    }

    public static void doIt(EditorAdaptor editorAdaptor) throws CommandExecutionException {
        editorAdaptor.setSelection(null);
        EditorMode currentMode = editorAdaptor.getMode(editorAdaptor.getCurrentModeName());
        if (currentMode instanceof TemporaryMode) {
            editorAdaptor.changeMode(InsertMode.NAME);
        } else {
            editorAdaptor.changeMode(NormalMode.NAME);
        }
    }
}
