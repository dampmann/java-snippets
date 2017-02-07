#include "Printer.h"
#include <iostream>
#include <string>

JNIEXPORT void JNICALL Java_Printer_Print(JNIEnv *env, jobject obj) {
    std::cout << "Hello from c++ lib \n";
}

