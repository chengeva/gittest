#include "isp.h"
#include <android/log.h>
#include <jni.h>
#include<stdlib.h>
#include <stdio.h>   /* required for file operations */
// __android_log_print(ANDROID_LOG_INFO, "JNIMsg", "[[e_cmd_battery_r]]");
/**
 *isp tool for android
 *code migration & maintain:
 *Jordan Han
 */
#define LOG_TAG "firmware_tool_debug_new"
#define O_RDONLY         00
#define O_WRONLY         01
#define O_RDWR           02
/**
 *code to test init of isp
 */
void Java_co_acaia_firmwaretool_ndkhelper_acaiaFirmwareNDKHelper_testhelloisp(
		JNIEnv* env, jobject javaThis) {
	__android_log_print(ANDROID_LOG_INFO, "firmware_tool_debug", "hello isp!");
}

/**
 *code to testing read file
 */
void Java_co_acaia_firmwaretool_ndkhelper_acaiaFirmwareNDKHelper_testfileread(
		JNIEnv* env, jobject javaThis, jstring file_path) {
	const char *filepath = (*env)->GetStringUTFChars(env, file_path, NULL);
	// .. do something with it

	__android_log_print(ANDROID_LOG_INFO, "firmware_tool_debug",
			"testing file read!");

	FILE *fr = fopen(filepath, "rt"); /* open the file for reading */
	/* elapsed.dta is the name of the file */
	/* "rt" means open the file for reading text */
	 char line[80];
	 int lines=0;
	while (fgets(line, 80, fr) != NULL) {
		lines++;
		/* get a line, up to 80 chars from fr.  done if NULL */

	}
	fclose(fr); /* close the file prior to exiting the routine */
	if(lines>0)
	__android_log_print(ANDROID_LOG_INFO, "firmware_tool_debug",
					"read some stuff");
	(*env)->ReleaseStringUTFChars(env, file_path, filepath);
}

