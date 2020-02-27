import subprocess
import os

tests = os.listdir("tests")
failedTests = []

for test in tests:
     proc = subprocess.Popen(["java Typecheck < tests/"+test], stdout=subprocess.PIPE, shell=True)
     expectOutput = "Program type checked successfully\n"
     if ("error" in str(test)):
          expectOutput = "Type error\n"
     (out, err) = proc.communicate()
     out = out.decode("utf-8") 
     passedTest = (out == expectOutput)
     if (not passedTest):
          failedTests.append(test)
     print ("Test",test,":",passedTest)
print("Failed Tests: ",failedTests)
