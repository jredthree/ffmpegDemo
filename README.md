#编译FFmpeg3.2 Android SO文件 步骤：

1.源代码下载，请到官网http://www.ffmpeg.org/下载最新的3.2代码
2.在linux环境下编译FFmpeg 首先将我们下载的ffmpeg包解压，然后进到我们的目录
3.修改ffmpeg-2.2/configure文件
将该文件中的如下四行：

SLIBNAME_WITH_MAJOR='$(SLIBNAME).$(LIBMAJOR)'

LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'

SLIB_INSTALL_NAME='$(SLIBNAME_WITH_VERSION)'

SLIB_INSTALL_LINKS='$(SLIBNAME_WITH_MAJOR)$(SLIBNAME)'

替换为：

SLIBNAME_WITH_MAJOR='$(SLIBPREF)$(FULLNAME)-$(LIBMAJOR)$(SLIBSUF)'

LIB_INSTALL_EXTRA_CMD='$$(RANLIB)"$(LIBDIR)/$(LIBNAME)"'

SLIB_INSTALL_NAME='$(SLIBNAME_WITH_MAJOR)'

SLIB_INSTALL_LINKS='$(SLIBNAME)'
4.编写build_android.sh脚本文件
build_android.sh的内容如下
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
这个脚本文件有几个地方需要注意：

*NDK,SYSROOT和TOOLCHAIN这三个环境变量一定要换成你自己机器里的。

*确保cross-prefix变量所指向的路径是存在的。

给build_android.sh增加可执行权限：


    $chmod+x build_android.sh  

执行build_android.sh


    $./build_android.sh  
配置该脚本完成对ffmpeg的配置，会生成config.h等配置文件，后面的编译会用到。如果未经过配置直接进行编译会提示无法找到config.h文件等错误。

    $make  
    $make install  
一定要先执行make再执行make install 不然会出错，还有权限一定要有，然后这个时候会在ffmpeg的同级目录生成一个lib目录，里面有include,pkgconfig两个文件夹
还有18个SO文件
5.创建一个普通的Android环境
*在工程的根目录创建文件夹jni
*在jni文件夹下创建prebuilt目录，再把18个SO文件放到该目录，并且把include中所有的文件拷贝到jni文件夹
*创建包含native方法的类，先在src下创建cn.dennishucd包，然后创建FFmpegNative.java类文件。主要包括加载so库文件和一个native测试方法两部分，其内容如下：

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
	
*用javah创建.头文件:进入bin/classes目录，执行：javah-jni cn.dennishucd.FFmpegNative                 

会在当前目录产生cn_dennishucd_FFmpegNative.h的C头文件;

根据头文件名，建立相同名字才C源文件cn_dennishucd_FFmpegNative.c
以上的文件在jni文件夹中
.c文件的主要内容为：
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
*编写Android.mk，内容如下：	
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

*编写Application.mk[可省略]
*编译so文件
打开cmd命令行，进入FFmpeg4Android\jni目录下，执行如下命令：

    $ndk-build  
	
截止本步骤完成，将在FFmpegDemo根目录下生成libs\armeabi目录，该目录除了包含上面的8个so之外，另外还生成了libffmpeg_codec.so文件。
*测试
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
代码中的28是H264的编解码ID，可以在ffmpeg的源代码中找到，它是枚举类型定义的。在C语言中，可以换算为整型值。这里测试能否找到H264编解码，如果能找到，说明调用ffmpeg的库函数是成功的，这也表明我们编译的so文件是基本可用。

