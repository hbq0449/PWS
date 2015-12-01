import string

rawdata = "Of zit kggd zitkt qkt\nygxk ortfzoeqs\nwqlatzwqssl qfr zvg\nortfzoeqs yggzwqssl.\nFgv oy ngx vqfz zg hxz\nzitd of gft soft.piv dgfn\nlgsxzogfl qkt zitkt?\nZohl:hstqlt eiqfut zit ygkd\ngy zit fxdwtk ngx\n utz.Zit Hkgukqddtkl!"
rawdata1 = "kggd zitkt qkt\nygxk ortfzoeqs\nwqlatzwqssl qfr zvg\nortfzoeqs yggzwqssl.\nFgv oy ngx vqfz zg hxz\nzitd of gft soft.piv dgfn\nlgsxzogfl qkt zitkt?\nZohl:hstqlt eiqfut zit ygkd\ngy zit fxdwtk ngx\n utz.Zit Hkgukqd"
rawdata2 = "eqs\nwqlatzwqssl qfr zvg\nortfzoeqs yggzwqssl.\nFgv oy ngx vqfz zg hxz\nzitd of gft soft.piv dgfn\nlgsxzogfl qkt zitkt?\nZohl:hstqlt eiqfut zit ygkd\ngy zit fxdwtk ngx\n utz.Zit Hkgukqd"
rawdata3 = "atzwqssl qfr zvg\nortfzoeqs yggzwqssl.\nFgv oy ngx vqfz zg hxz\nzitd of gft soft.piv dgfn\nlgsxzogfl qkt zitkt?\nZohl:hstqlt eiqfut zit ygkd\ngy zit fxdwtk ngx\n utz.Zit Hkgukqd"
rawdata4 = "r zvg\nortfzoeqs yggzwqssl.\nFgv oy ngx vqfz zg hxz\nzitd of gft soft.piv dgfn\nlgsxzogfl qkt zitkt?\nZohl:hstqlt eiqfut zit ygkd\ngy zit fxdwtk ngx\n utz.Zit Hkgukqd"
rawdata5 = "yggzwqssl.\nFgv oy ngx vqfz zg hxz\nzitd of gft soft.piv dgfn\nlgsxzogfl qkt zitkt?\nZohl:hstqlt eiqfut zit ygkd\ngy zit fxdwtk ngx\n utz.Zit Hkgukqd"
rawdata6 = "oft.piv dgfn\nlgsxzogfl qkt zitkt?\nZohl:hstqlt eiqfut zit ygkd\ngy zit fxdwtk ngx\n utz.Zit Hkgukqd"
rawdata7 = "d\ngy zit fxdwtk ngx\n utz.Zit Hkgukqd"

rawdataset = []
letters = "abcdefghijklmnopqrstuvwxyz"
replaceletters = "etoanirshdlcfumpywgbvkxjqz"


rawdataset.append(rawdata)
rawdataset.append(rawdata1)
rawdataset.append(rawdata2)
rawdataset.append(rawdata3)
rawdataset.append(rawdata4)
rawdataset.append(rawdata5)
rawdataset.append(rawdata6)
rawdataset.append(rawdata7)

class dataItem:
    rawstring = str()
    chance = dict()
    repstring = str()
    def __init__(self):
        self.rawstring = str()
        self.chance = dict()
        self.repstring = str()
        return
    
    def Initrawdata(self, value):
        self.rawstring = value
        return
    
    def GetCount(self):
        for char in letters:
            self.chance[char] = self.rawstring.count(char)
        return
            
    def Replace(self):
        seq = set()
        self.repstring = self.rawstring
        for key, value in self.chance.items():
            seq.add(value)
        
        for index in range(len(seq)):
            liseq = list(seq)
            for key, value in self.chance.items():
                if value != liseq[index]:
                    continue
                self.repstring = self.rawstring.replace(key, replaceletters[index])
        return
        
    
        
cvalue = []
for strvalue in rawdataset:
    cStrValue = dataItem()
    cStrValue.Initrawdata(strvalue)
    cStrValue.GetCount()
    #print "******************"
    #print cStrValue.rawstring
    cvalue.append(cStrValue)
    
for valueobj in cvalue:
    print "==================="
    print valueobj.rawstring
    print "-------------------"
    for key,value in valueobj.chance.items():
        if value == 0:
            continue
        print "key : %s count: %d" % (key, value)
        
    valueobj.Replace()
    #print valueobj.repstring

    

        

print rawdataset

