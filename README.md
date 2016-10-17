#����FFmpeg3.2 Android SO�ļ� ���裺

1.Դ�������أ��뵽����http://www.ffmpeg.org/�������µ�3.2����
2.��linux�����±���FFmpeg ���Ƚ��������ص�ffmpeg����ѹ��Ȼ��������ǵ�Ŀ¼
3.�޸�ffmpeg-2.2/configure�ļ�
�����ļ��е��������У�

SLIBNAME_WITH_MAJOR='$(SLIBNAME).$(LIBMAJOR)'

LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'

SLIB_INSTALL_NAME='$(SLIBNAME_WITH_VERSION)'

SLIB_INSTALL_LINKS='$(SLIBNAME_WITH_MAJOR)$(SLIBNAME)'

�滻Ϊ��

SLIBNAME_WITH_MAJOR='$(SLIBPREF)$(FULLNAME)-$(LIBMAJOR)$(SLIBSUF)'

LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'

SLIB_INSTALL_NAME='$(SLIBNAME_WITH_MAJOR)'

SLIB_INSTALL_LINKS='$(SLIBNAME)'
4.��дbuild_android.sh�ű��ļ�
build_android.sh����������
***
    #!/bin/bash  
    NDK=/home/dennis/android-ndk-r9d  
    SYSROOT=$NDK/platforms/android-9/arch-arm/  
    TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.8/prebuilt/linux-x86_64  
  
    function build_one  
    {  
    ./configure \  
    --prefix=$PREFIX \  
    --enable-shared \  
    --disable-static \  
    --disable-doc \  
    --disable-ffserver \  
    --enable-cross-compile \  
    --cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \  
    --target-os=linux \  
    --arch=arm \  
    --sysroot=$SYSROOT \  
    --extra-cflags="-Os -fpic $ADDI_CFLAGS" \  
    --extra-ldflags="$ADDI_LDFLAGS" \  
    $ADDITIONAL_CONFIGURE_FLAG  
    }  
    CPU=arm  
    PREFIX=$(pwd)/android/$CPU  
    ADDI_CFLAGS="-marm"  
    build_one  
***
����ű��ļ��м����ط���Ҫע�⣺

*NDK,SYSROOT��TOOLCHAIN��������������һ��Ҫ�������Լ�������ġ�

*ȷ��cross-prefix������ָ���·���Ǵ��ڵġ�

��build_android.sh���ӿ�ִ��Ȩ�ޣ�


    $chmod+x build_android.sh  

ִ��build_android.sh


    $./build_android.sh  
���øýű���ɶ�ffmpeg�����ã�������config.h�������ļ�������ı�����õ������δ��������ֱ�ӽ��б������ʾ�޷��ҵ�config.h�ļ��ȴ���

    $make  
    $make install  
һ��Ҫ��ִ��make��ִ��make install ��Ȼ���������Ȩ��һ��Ҫ�У�Ȼ�����ʱ�����ffmpeg��ͬ��Ŀ¼����һ��libĿ¼��������include,pkgconfig�����ļ���
����18��SO�ļ�
5.����һ����ͨ��Android����
*�ڹ��̵ĸ�Ŀ¼�����ļ���jni
*��jni�ļ����´���prebuiltĿ¼���ٰ�18��SO�ļ��ŵ���Ŀ¼�����Ұ�include�����е��ļ�������jni�ļ���
*��������native�������࣬����src�´���cn.dennishucd����Ȼ�󴴽�FFmpegNative.java���ļ�����Ҫ��������so���ļ���һ��native���Է��������֣����������£�

    package cn.dennishucd;

    public class FFmpegNative {
	 static{  
		 
         System.loadLibrary("avcodec-57");  
         System.loadLibrary("avdevice-57");  
         System.loadLibrary("avfilter-6");  
         System.loadLibrary("avformat-57");  
         System.loadLibrary("avutil-55");  
         System.loadLibrary("postproc-54"); 
         System.loadLibrary("swresample-2");  
         System.loadLibrary("swscale-4");  
         System.loadLibrary("ffmpeg_codec");  
         
    }  
    public native int avcodec_find_decoder(int codecID);  
    }
	
*��javah����.ͷ�ļ�:����bin/classesĿ¼��ִ�У�javah-jni cn.dennishucd.FFmpegNative                 

���ڵ�ǰĿ¼����cn_dennishucd_FFmpegNative.h��Cͷ�ļ�;

����ͷ�ļ�����������ͬ���ֲ�CԴ�ļ�cn_dennishucd_FFmpegNative.c
���ϵ��ļ���jni�ļ�����
.c�ļ�����Ҫ����Ϊ��
    JNIEXPORT jint JNICALL Java_cn_dennishucd_FFmpegNative_avcodec_1find_1decoder
    (JNIEnv *env, jobject obj, jint codecID)
    {
	 AVCodec *codec = NULL;

	/* register all formats and codecs */
	av_register_all();

	codec = avcodec_find_decoder(codecID);

	if (codec != NULL)
	{
		return 0;
	}
	else
	{
		return -1;
	}
    }
***
*��дAndroid.mk���������£�	
    LOCAL_PATH := $(call my-dir)

    include $(CLEAR_VARS)
    LOCAL_MODULE := avcodec-57-prebuilt
    LOCAL_SRC_FILES := prebuilt/libavcodec-57.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := avdevice-57-prebuilt
    LOCAL_SRC_FILES := prebuilt/libavdevice-57.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := avfilter-6-prebuilt
    LOCAL_SRC_FILES := prebuilt/libavfilter-6.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE := avformat-57-prebuilt
    LOCAL_SRC_FILES := prebuilt/libavformat-57.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE :=  avutil-55-prebuilt
    LOCAL_SRC_FILES := prebuilt/libavutil-55.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE :=  libpostproc-54-prebuilt
    LOCAL_SRC_FILES := prebuilt/libpostproc-54.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE :=  avswresample-2-prebuilt
    LOCAL_SRC_FILES := prebuilt/libswresample-2.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)
    LOCAL_MODULE :=  swscale-4-prebuilt
    LOCAL_SRC_FILES := prebuilt/libswscale-4.so
    include $(PREBUILT_SHARED_LIBRARY)

    include $(CLEAR_VARS)

    LOCAL_MODULE := ffmpeg_codec
    LOCAL_SRC_FILES := cn_dennishucd_FFmpegNative.c

    LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid
    LOCAL_SHARED_LIBRARIES := avcodec-57-prebuilt avdevice-57-prebuilt avfilter-6-prebuilt avformat-57-prebuilt avutil-55-prebuilt

    include $(BUILD_SHARED_LIBRARY)

*��дApplication.mk[��ʡ��]
*����so�ļ�
��cmd�����У�����FFmpeg4Android\jniĿ¼�£�ִ���������

    $ndk-build  
	
��ֹ��������ɣ�����FFmpegDemo��Ŀ¼������libs\armeabiĿ¼����Ŀ¼���˰��������8��so֮�⣬���⻹������libffmpeg_codec.so�ļ���
*����
```
package com.example.ffmpegdemo;

import cn.dennishucd.FFmpegNative;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


@SuppressLint("NewApi") public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  

		setContentView(R.layout.activity_main);  

		TextView tv = (TextView)this.findViewById(R.id.textview_hello);  

		FFmpegNative ffmpeg = new FFmpegNative();  
		int codecID = 28; //28 is the H264 Codec ID  

		int res = ffmpeg.avcodec_find_decoder(codecID);  

		if(res ==0) {  
			tv.setText("Success!");  
		}  
		else{  
			tv.setText("Failed!");  
		}  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
```
�����е�28��H264�ı����ID��������ffmpeg��Դ�������ҵ�������ö�����Ͷ���ġ���C�����У����Ի���Ϊ����ֵ����������ܷ��ҵ�H264����룬������ҵ���˵������ffmpeg�Ŀ⺯���ǳɹ��ģ���Ҳ�������Ǳ����so�ļ��ǻ������á�

