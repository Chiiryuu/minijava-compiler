import subprocess
import os
import sys

file = sys.argv[1]
proc = subprocess.Popen(["java J2V < "+file+" > "+file+".vapor"], stdout=subprocess.PIPE, shell=True)
(out, err) = proc.communicate()

proc = subprocess.Popen(["java -jar vapor.jar run "+file+".vapor"], stdout=subprocess.PIPE, shell=True)
(out, err) = proc.communicate()
print("Result:")
print(out.decode("utf-8") )