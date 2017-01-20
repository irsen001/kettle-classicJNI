#include "_onload.h"
#include "utilbase.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <android/log.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <android/native_window_jni.h>
#include "otgset.h"

//#include <linux/delay.h>
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>

#define  LOG_TAG    "K10"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
typedef unsigned int speed_tt;

static char* dev_name = "/dev/teaset";
static char* temp_name = "/sys/class/power_supply/battery/voltage_now";
static int fd = -1;
char temp1[8] = {0} , err;
int t_arry[80][2] = {0};


int native_get_temp(JNIEnv *env, jobject thiz, jint state) {
    int err, value,i,j,m;
    unsigned long getv = 0;
    char temp[10] = {0};

    fd = open (temp_name, O_RDONLY | O_NONBLOCK, 0);
    err = read(fd, temp, 10);
    LOGE(" %s\n", temp);

    j=1;
    for(i = 0;i<10;i++){
        if(temp[i] == 10){
            for(m = i;m>0;m--){
                getv += (temp[m-1] - 48)*j;
               // LOGE("open water_ops =%d, j = %d ,getv=%lu\n", (temp[m-1] - 48), j,getv);
                j = j*10;
            }
        }
    }

value = getv/1000;

//LOGE("open water_ops 74[%d][%d],75[%d][%d],76[%d][%d] ,getv=%lu, value = %d\n",t_arry[74][0],t_arry[74][1],t_arry[75][0],t_arry[75][1],t_arry[76][0],t_arry[76][1] ,getv, value);
    for(i = 0; i<77;i++){
       // if(t_arry[i][1] == value)//getv)
        if(value > t_arry[i+1][1] && value <= t_arry[i][1])
            return t_arry[i][0];
    }
    /*switch (getv) {
    default:
       		return -1;
    }*/
    if(value > 1500) return -1;
    return 25;
}

static void native_water_ops(JNIEnv *env, jobject thiz, jint ops, jint state) {
	int err,i;
    unsigned int mstate = state;

  //  LOGE("open water_ops =%d, state = %d\n", ops, state);
    fd = open (dev_name, O_RDWR | O_NONBLOCK, 0);
	if (-1 == fd) {
	    LOGE("cannot open file\n");
		return;
	}
		//	err = ioctl (fd, MSMGPIO_BD_POWERON, 0);
    	//	err = ioctl (fd, MSMGPIO_UART_BD, 0);
    switch(ops){
        case K10_ONOFF:
            err = ioctl (fd, TEA_IOCTL_POWNON, mstate);
        break;
        case K10_AUTO_WATER:
            err = ioctl (fd, TEA_IOCTL_AUTO_PUMP_WATER, mstate);
        break;
        case K10_PUMP_WATER:
            err = ioctl (fd, TEA_IOCTL_PUMP_WATER, mstate);
        break;
        case K10_HEATING:
            err = ioctl (fd, TEA_IOCTL_BURNING, mstate);
        break;
    }
    close(fd);
   // LOGE("err = %d \n",err);
}

void init_t_arry()
{
    int i,j;
    int init_t = 25, init_vol = 1100;
    for(i=0;i<76;i++){
        t_arry[i][0] = init_t;
        t_arry[i][1] = init_vol;
        init_t++;
        init_vol -= 12;
    }
}
jint registerNativeMethods(JNIEnv* env, const char *class_name, JNINativeMethod *methods, int num_methods) {
	int result = 0;

    init_t_arry();

	jclass clazz = env->FindClass(class_name);
	if (LIKELY(clazz)) {
		int result = env->RegisterNatives(clazz, methods, num_methods);
		if (UNLIKELY(result < 0)) {
			LOGI("registerNativeMethods failed(class=%s)", class_name);
		}
	} else {
		LOGI("registerNativeMethods: class'%s' not found", class_name);
	}
	return result;
}
extern int native_start_test(int argc);
static JNINativeMethod methods[] = {
	{ "water_ops",					"(II)V", (void *) native_water_ops },
	{ "get_temp",					"(I)I", (void *) native_get_temp },
};


int register_uvccamera(JNIEnv *env) {
	if (registerNativeMethods(env,
		"com/savvy/floatingwindowexample/MainActivity",
		methods, NUM_ARRAY_ELEMENTS(methods)) < 0) {
		return -1;
	}
    return 0;
}


jint JNI_OnLoad(JavaVM *vm, void *reserved) {
	    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    // register native methods
    int result = register_uvccamera(env);

    return JNI_VERSION_1_6;
}
