//
// Created by jumbada com on 29.11.2018.
//

#include <jni.h>
#include <string>





extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativetest_MainActivity_jni(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


extern "C" JNIEXPORT jint JNICALL
Java_com_example_nativetest_MainActivity_testRubberBand(
        JNIEnv *env,
        jobject /* this */) {

    jint i = 101;
    return i;
}
