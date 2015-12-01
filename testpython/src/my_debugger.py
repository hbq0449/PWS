# this is a simple test for hello world
# encoding=utf8  
from ctypes import *
from winappdbg import win32
from winappdbg.win32 import kernel32

win32.PROCESS_INFORMATION

class debugger():
    def __init__(self):
        pass
    
    def load(self, path_to_exe):
        creation_flags = win32.DEBUG_PROCESS
        startupinfo = win32.STARTUPINFO()
        processinfo = win32.PROCESS_INFORMATION()
        startupinfo.dwFlags = 0x1
        startupinfo.wShowWindow = 0x0
                
        startupinfo.cb = sizeof(startupinfo)
        
        if win32.CreateProcess(path_to_exe,
                                   None,
                                   creation_flags,
                                   None,
                                   None,
                                   byref(startupinfo),
                                   byref(processinfo)):
            print "we have success createprocess: %d" % processinfo.dwProcessId
        else:
            print "create process error: 0x%08x" % win32.GetLastError()
            

        
    def open_process(self, pid):
        h_process = win32.OpenProcess(win32.PROCESS_ALL_ACCESS, False, pid)
        return h_process
    
    def attach(self, pid):
        self.h_process = self.open_process(pid)
        if win32.DebugActiveProcess(pid):
            self.debugger_actived = True
            self.pid = int(pid)
            self.run()
        else:
            print "[*] unable to attach to process"
        
    def run(self):
        while self.debugger_actived == True:
            self.get_debug_event()
    
    def get_debug_event(self):
        knr32 = windll.kernel32
        dbg_event = win32.DEBUG_EVENT()
        continue_status = win32.DBG_CONTINUE
        
        if knr32.WaitForDebugEvent(byref(dbg_event), win32.INFINITE):
            raw_input("press a key to continue...")
            self.debugger_actived = False
            knr32.ContinueDebugEvent(dbg_event.dwProcessId, dbg_event.dwThreadId, continue_status)
        
    def detach(self):
        if win32.DebugActiveProcessStop(self.pid):
            print "[*] finished debug exiting"
            return True
        else:
            print "there is an error"
            return False
            