# java-snippets
This repository includes some java snippets I liked to play with

+ **jni (Java Native Interface)**    

  Use a very simple C++ library function in Java to print a message.
  I tried this on a mac.


  Compile Printer.java using the following command:
  ``javac Printer.java``


  Create the header file to get the signature for you native library
  method you want to call with the following command

  ``javah Printer``

  Use ``/usr/libexec/java_home`` to figure out what your environment variable
  JAVA_HOME should look like.


  Then compile the C++ program lib.cpp like that:
  
  
  ``g++ -Wall -shared -fPIC -I$JAVA_HOME/include/ \
      -I$JAVA_HOME/include/darwin -o libmylib.dylib lib.cpp``


  Execute your java program using
  ``java -Djava.library.path="." Printer``
