# this is a simple test for hello world
# encoding=utf8  
from winappdbg import *

"""
两个list取交集的办法
#方法一:
a=[2,3,4,5]
b=[2,5,8]
tmp = [val for val in a if val in b]
print tmp
#[2, 5]

#方法二
print list(set(a).intersection(set(b)))
"""

def test(pid):
    crackme = Process(pid)