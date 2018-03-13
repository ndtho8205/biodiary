#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_edu_bk_thesis_biodiary_BioDiaryMainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
