package net.sourceforge.vrapper.vim.commands;

import net.sourceforge.vrapper.platform.ViewportService;
import net.sourceforge.vrapper.utils.ViewPortInformation;
import net.sourceforge.vrapper.vim.EditorAdaptor;
import net.sourceforge.vrapper.vim.Options;
import net.sourceforge.vrapper.vim.commands.motions.ViewPortMotion;
import net.sourceforge.vrapper.vim.commands.motions.ViewPortMotion.Type;

public class CenterLineCommand extends CountIgnoringNonRepeatableCommand {

    public static final CenterLineCommand CENTER = new CenterLineCommand(Type.MIDDLE);
    public static final CenterLineCommand TOP = new CenterLineCommand(Type.HIGH);
    public static final CenterLineCommand BOTTOM = new CenterLineCommand(Type.LOW);

    private final ViewPortMotion.Type type;

    private CenterLineCommand(Type type) {
        super();
        this.type = type;
    }

    public void execute(EditorAdaptor editorAdaptor) {
        int line = editorAdaptor.getViewContent().getLineInformationOfOffset(
                editorAdaptor.getPosition().getViewOffset()).getNumber();
        doIt(editorAdaptor, line);
    }

    public void doIt(EditorAdaptor editorAdaptor, int line) {
        ViewportService view = editorAdaptor.getViewportService();
        ViewPortInformation info = view.getViewPortInformation();
        int scrolloff = editorAdaptor.getConfiguration().get(Options.SCROLL_OFFSET);
        int middle = type.calculateLine(info, scrolloff);
        int offset = line - middle;
        int target = Math.max(info.getTopLine()+offset, 0);
        view.setTopLine(target);
    }

}
