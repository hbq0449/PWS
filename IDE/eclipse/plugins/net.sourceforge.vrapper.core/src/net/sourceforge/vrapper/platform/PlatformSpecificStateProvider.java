package net.sourceforge.vrapper.platform;

import net.sourceforge.vrapper.keymap.State;
import net.sourceforge.vrapper.vim.TextObjectProvider;
import net.sourceforge.vrapper.vim.commands.Command;
import net.sourceforge.vrapper.vim.modes.KeyMapResolver;
import net.sourceforge.vrapper.vim.modes.commandline.EvaluatorMapping;

/**
 * Provides States defining key mappings for platform specific commands.
 *
 * @author Matthias Radig
 */
public interface PlatformSpecificStateProvider {

    /**
     * Force initialization of the provider.
     * @param textObjProvider
     */
    void initializeProvider(TextObjectProvider textObjProvider);

    /**
     * @param modeName
     *            name of a mode
     * @return a {@link State} containing (platform specific) key bindings which
     *         will be integrated into the calling mode.
     */
    State<Command> getState(String modeName);

    /**
     * @param name
     *            name of a mode
     * @return a {@link State} containing key map bindings which will be
     *         integrated into the {@link KeyMapResolver} of the calling mode.
     */
    State<String> getKeyMaps(String name);

    /**
     * @return {@link EvaluatorMapping} containing platform-specific commands
     */
    EvaluatorMapping getCommands();

    /**
     * @return name of this PlatformSpecificStateProvider (for caching purposes)
     */
    String getName();

}
