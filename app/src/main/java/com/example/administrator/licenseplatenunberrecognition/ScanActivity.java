package com.example.administrator.licenseplatenunberrecognition;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;



public class ScanActivity extends AppCompatActivity {
    private TessBaseAPI  tessBaseAPI;
    private  Bitmap bitmap=null;
    private Bitmap prebitmap=null;
    private String result;
    private boolean finish=false;
    private SurfaceView cameraview_id;
    private Camera camera;
    private Camera.AutoFocusCallback autoFocusCB;
    private Runnable doAutoFocus;
    int PreviewWidth = 0;
    int PreviewHeight = 0;
    private static final String DATAPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"Tessract"+File.separator;
    private static final String tessdata = DATAPATH + "tessdata"+ File.separator;
    private static final String input=DATAPATH+"input"+File.separator;
    private static final String output=DATAPATH+"output"+File.separator;

    private static final String DEFAULT_LANGUAGE = "eng";
    private   ClearImageHelper clearImageHelper=new ClearImageHelper();

    /**
     * assets中的文件名
     */
    private static final String DEFAULT_LANGUAGE_NAME = DEFAULT_LANGUAGE + ".traineddata";
    /**
     * 保存到SD卡中的完整文件名
     */
    private static final String LANGUAGE_PATH = tessdata + DEFAULT_LANGUAGE_NAME;
    /**
     * 权限请求值
     */
    private static final int REQUEST_CODE_CONTACT=101;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ActionBar mActionBar = getSupportActionBar();

        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //动态获取读写权限
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        File f = new File(tessdata + DEFAULT_LANGUAGE_NAME), dir = new File(tessdata), in = new File(input), out = new File(output), path = new File(DATAPATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!in.exists()) {
            in.mkdirs();
        }
        if (!out.exists()) {
            out.mkdirs();
        }
        if (!f.exists()) {
            copyAssets(tessdata);
        }
            tessBaseAPI = new TessBaseAPI();
            tessBaseAPI.init(DATAPATH, DEFAULT_LANGUAGE);

        cameraview_id = (SurfaceView) findViewById(R.id.cameraview);
        /* 给SurfaceView设置监听器 */
        cameraview_id.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
                    Camera.getCameraInfo(cameraId, cameraInfo);
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        try {
                            camera = Camera.open(cameraId);
                        } catch (Exception e) {
                            if (camera != null) {
                                camera.release();
                                camera = null;
                            }
                        }
                        break;
                    }
                }
                //给照相机设置参数
                Camera.Parameters parameters = camera.getParameters();
                //设置每秒30栈
                parameters.setPreviewFrameRate(30);
                //设置照片的格式
                parameters.setPreviewFormat(PixelFormat.JPEG);

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                    // 选择合适的预览尺寸
//                    List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
//                    // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
//                    if (sizeList.size() > 1) {
//                        Iterator<Size> itor = sizeList.iterator();
//                        while (itor.hasNext()) {
//                            Camera.Size cur = itor.next();
//                            if (cur.width >= PreviewWidth
//                                    && cur.height >= PreviewHeight) {
//                                PreviewWidth = cur.width;
//                                PreviewHeight = cur.height;
//                                break;
//                            }
//                        }
//                    }
//                    parameters.setPictureSize(PreviewWidth,PreviewHeight);

                    //设置照片的质量
                    parameters.set("jpeg-quality", 100);

                //parameters.setPictureSize(2160,1080);
                //camera.setDisplayOrientation(90);

                    // 给照相机设置参数
               // camera.setParameters(parameters);

                //将画面展示到SurfaceView
                    try {
                        camera.setPreviewDisplay(cameraview_id.getHolder());
                        //开启预览
                        camera.startPreview();
                        camera.autoFocus(new AutoFocusCallback() {
                            public void onAutoFocus(boolean success, Camera camera) {
                                // 判断是否对焦成功
                                if (success) {
                                    return;
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera!=null){
                    camera.stopPreview();
                    camera.release();//释放资源
                }
            }
        });


    }


    public void btnClick_scan(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                camera.autoFocus(new AutoFocusCallback() {

                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        // 判断是否对焦成功
                        if (success) {
                            // 拍照 第三个参数为拍照回调
                            camera.takePicture(null, null, pic);
                        }
                    }
                });

        try {

           // FileInputStream fs=new FileInputStream(input+"p19.png");

           // Bitmap bitmap1 = BitmapFactory.decodeStream(fs);
           // bitmap=bitmap1;   clearImageHelper.cleanImage(prebitmap)
           // clearImageHelper.cleanImage();
//            tessBaseAPI.setImage(prebitmap );
//            result = tessBaseAPI.getUTF8Text();
        }catch(IllegalStateException e) {
            e.printStackTrace();
        }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // Log.i("ttt",result);
        tessBaseAPI.end();
        finish=true;
            }
        }).start();

        SystemClock.sleep(1000);
//        if(bitmap != null && !bitmap.isRecycled()){
//            bitmap.recycle();
//            bitmap = null;
//        }
//        System.gc();
//        prebitmap.recycle();
//        prebitmap=null;

        //content.add

    }

    private PictureCallback pic = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            byte[] rawImage;
//            ByteArrayOutputStream baos;
//            Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
//            BitmapFactory.Options newOpts = new BitmapFactory.Options();
//            newOpts.inJustDecodeBounds = true;
//            YuvImage yuvimage = new YuvImage(
//                    data,
//                    ImageFormat.NV21,
//                    previewSize.width,
//                    previewSize.height,
//                    null);
//            baos = new ByteArrayOutputStream();
//            yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
//            rawImage = baos.toByteArray();
//            //将rawImage转换成bitmap
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            prebitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
            // data为完整数据
           // prebitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            Camera.Size size = camera.getParameters().getPreviewSize();
//            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
//            if (image != null) {
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, stream);
//                Matrix matrix = new Matrix();
//                matrix.setRotate(90);
//                Bitmap newBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//               prebitmap = Bitmap.createBitmap(newBitmap, 0, 0,
//                       newBitmap.getWidth(), newBitmap.getHeight(), matrix, true);
//            }
            //旋转角度，保证保存的图片方向是对的
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            Matrix matrix = new Matrix();
//            matrix.setRotate(90);
//            prebitmap = Bitmap.createBitmap(bitmap, 0, 0,
//                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
             File file = new File(input+"a.jpg");
               //  使用流进行读写
            try {
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    fos.write(data);
                    // 关闭流
                    fos.close();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }

        }


    };
    //@param path  要存放在SD卡中的 路径名。这里是"/storage/emulated/0/tessdata/"
    private void copyAssets(String path) {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            // Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(path, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
               // Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {

                }
                }

            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }



    public class ClearImageHelper
    {
        /**
         *
         * @throws IOException
         */
        public  Bitmap cleanImage()
                throws IOException
        {


           // Bitmap bitmap1=null;
            //File destF = new File(destDir);
//            if (!destF.exists())
//            {
//                destF.mkdirs();
//            }
//            String srcFile=input+name;
//            FileInputStream fs=new FileInputStream(srcFile);
            //BitmapFactory.Options options=new BitmapFactory.Options();
           // bitmap1 = BitmapFactory.decodeStream(fs);
            int width=prebitmap.getWidth();
            int height=prebitmap.getHeight();

            int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
            // 设定二值化的域值，默认值为100
            //tmp = 180;
            prebitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int alpha = 0xFF << 24,tmp=127;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int grey = pixels[width * i + j];
                    // 分离三原色
                    alpha = ((grey & 0xFF000000) >> 24);
                    int red = ((grey & 0x00FF0000) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);
                    if (red > tmp) {
                        red = 255;
                    } else {
                        red = 0;
                    }
                    if (blue > tmp) {
                        blue = 255;
                    } else {
                        blue = 0;
                    }
                    if (green > tmp) {
                        green = 255;
                    } else {
                        green = 0;
                    }
                    pixels[width * i + j] = alpha << 24 | red << 16 | green << 8
                            | blue;
                    if (pixels[width * i + j] == -1) {
                        pixels[width * i + j] = -1;
                    } else {
                        pixels[width * i + j] = -16777216;
                    }
                }
            }
            // 新建图片
            Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 设置图片数据
            newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
            Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
           return resizeBmp;
           // 保存bitmap到本地
//            File file = new File(output+"p19.png");
//            if(file.exists()){
//                file.delete();
//            }
//            FileOutputStream out;
//            try{
//                out = new FileOutputStream(file);
//                if(resizeBmp.compress(Bitmap.CompressFormat.PNG, 90, out))
//                {
//                    out.flush();
//                    out.close();
//                }
//            }
//            catch (FileNotFoundException e)
//            {
//                e.printStackTrace();
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
       }
    }

}
