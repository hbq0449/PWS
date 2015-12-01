from ctypes import *

# this is a simple test for hello world
# encoding=utf8  

WORD = c_uint16
DWORD = c_uint32
LPBYTE = POINTER(c_ubyte)
LPTSTR = POINTER(c_char)
HANDLE = c_void_p

DEBUG_PROCESS = 0x00000001
CREATE_NEW_CONSOLE = 0x00000010

class STARTUPINFO(Structure):
    _fields_ = [
                ("cb",              DWORD),
                ("lpReserved",      LPTSTR),
                ("lpDesktop",       LPTSTR),
                ("lpTitle",         LPTSTR),
                ("dwX",             DWORD),
                ("dwY",             DWORD),
                ("dwXSize",         DWORD),
                ("dwYSize",         DWORD),
                ("dwXCountChars",   DWORD),
                ("dwYCountChars",   DWORD),
                ("dwFillAttribute", DWORD),
                ("dwFlags",         DWORD),
                ("wShowWindow",     WORD),
                ("cbReserved2",     WORD),
                ("lpReserved2",     LPBYTE),
                ("hStdInput",       HANDLE),
                ("hStdOutput",      HANDLE),
                ("hStdError",       HANDLE)
                ]
    
class PROCESS_INFOMATION(Structure):
    _fields_ = [
                ("hProcess", HANDLE),
                ("hThread", HANDLE),
                ("dwProcessId", DWORD),
                ("dwThreadId", DWORD)
                ]
    
    