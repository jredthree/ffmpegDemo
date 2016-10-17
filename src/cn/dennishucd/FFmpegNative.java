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
