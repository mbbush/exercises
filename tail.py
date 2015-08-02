# -*- coding: utf-8 -*-
'''Clone of the built-in *nix tail program'''

import os
import re
import sys

# 1 MB
SMALL_SIZE = 1048576
# 4 kB
BLOCK_SIZE = 4096

def tail_small_file(f, count=10):
    out = f.readlines()[-count:]
    print ''.join(out),

def tail_lines(filename, count=10):
    count = int(count)
    with open(filename) as f:
        filesize = os.path.getsize(filename)
        if filesize < SMALL_SIZE:
            tail_small_file(f, count)
            return
        lines_found = 0
        blocks = []
        try:
            f.seek(0, os.SEEK_END)
            while lines_found < count:
                f.seek(-BLOCK_SIZE, os.SEEK_CUR)
                block = f.read(BLOCK_SIZE)
                f.seek(-BLOCK_SIZE, os.SEEK_CUR)
                matches = re.findall('\n.', block)
                lines_found = lines_found + len(matches)
                blocks.append(block)
            data = ''.join(blocks)
            lines = data.split('\n')
            out = '\n'.join(lines[-count:])
            print out,
        except IOError:
            tail_small_file(f, count)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print "Please specify a filename"
    else:
        tail_lines(*sys.argv[1:3])
