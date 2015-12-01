package net.sourceforge.vrapper.platform;

import net.sourceforge.vrapper.vim.TextObjectProvider;


public interface Platform {

    TextContent getModelContent();
    TextContent getViewContent();
    CursorService getCursorService();
    SelectionService getSelectionService();
    FileService getFileService();
    ViewportService getViewportService();
    HistoryService getHistoryService();
    UserInterfaceService getUserInterfaceService();
    ServiceProvider getServiceProvider();
    KeyMapProvider getKeyMapProvider();
    UnderlyingEditorSettings getUnderlyingEditorSettings();
    GlobalConfiguration getConfiguration();
    PlatformSpecificStateProvider getPlatformSpecificStateProvider(
            TextObjectProvider textObjectProvider);
    PlatformSpecificModeProvider getPlatformSpecificModeProvider();
    PlatformSpecificTextObjectProvider getPlatformSpecificTextObjectProvider();
    SearchAndReplaceService getSearchAndReplaceService();
    HighlightingService getHighlightingService();
    String getEditorType();
}
