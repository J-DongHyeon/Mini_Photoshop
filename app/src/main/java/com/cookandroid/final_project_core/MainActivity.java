package com.cookandroid.final_project_core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageButton zoom_in, rotate, bright, gray, blur, mosaic, contrast,
            pen, stamp, eraser, snow;
    SeekBar sBar;
    Button getImg, saveImg, return_state;
    LinearLayout view_layout;

    myView myview;
    Bitmap getImg_buffer, getImg_buffer_sub;

    boolean control_getImg = false;
    String select_sBar;
    boolean control_zoomin = false;
    boolean control_rotate_right = true;
    boolean control_bright = false;
    boolean control_gray = false;
    boolean control_blur = false;
    final int NORMAL = 1, INNER = 2, OUTER = 3, SOLID = 4;
    int sel_blur_type = NORMAL;
    boolean control_contrast = false;
    boolean control_pen = false;
    boolean control_stamp = false;
    boolean control_eraser = false;
    boolean control_snow = false;
    boolean control_mosaic = false;

    float scaleX = 1, scaleY = 1;
    float angle = 0;
    float RGB_bright = 1;
    float blur_radius = 50.0f;
    int brightness = 0;
    int bright_sign = 1;
    int pen_thickness = 3;
    int pen_color = Color.BLACK;
    int select_stamp = R.drawable.stamp1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("미니 포토샵");

        zoom_in = (ImageButton) findViewById(R.id.zoom_in);
        rotate = (ImageButton) findViewById(R.id.rotate);
        bright = (ImageButton) findViewById(R.id.bright);
        gray = (ImageButton) findViewById(R.id.gray);
        blur = (ImageButton) findViewById(R.id.blur);
        mosaic = (ImageButton) findViewById(R.id.mosaic);
        contrast = (ImageButton) findViewById(R.id.contrast);
        pen = (ImageButton) findViewById(R.id.pen);
        stamp = (ImageButton) findViewById(R.id.stamp);
        eraser = (ImageButton) findViewById(R.id.eraser);
        snow = (ImageButton) findViewById(R.id.snow);

        sBar = (SeekBar) findViewById(R.id.sBar);

        getImg = (Button) findViewById(R.id.getImg);
        saveImg = (Button) findViewById(R.id.saveImg);
        return_state = (Button) findViewById(R.id.return_state);

        view_layout = (LinearLayout) findViewById(R.id.view_layout);

        myview = new myView(this);
        view_layout.addView(myview);

        registerForContextMenu(rotate);
        registerForContextMenu(blur);
        registerForContextMenu(pen);
        registerForContextMenu(stamp);

        return_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleX = 1; scaleY = 1;
                angle = 0;
                RGB_bright = 1;
                blur_radius = 50.0f;
                brightness = 0;
                bright_sign = 1;
                control_gray = false;
                control_blur = false;
                control_contrast = false;

                sBar.setProgress(50);
                myview.invalidate();
            }
        });

        getImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                control_getImg = true;

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 1);

                scaleX = 1; scaleY = 1;
                angle = 0;
                RGB_bright = 1;
                blur_radius = 50.0f;
                brightness = 0;
                bright_sign = 1;
                control_gray = false;
                control_blur = false;
                control_contrast = false;

                sBar.setProgress(50);

            }
        });

        saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat day = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date();
                String current_time = day.format(date) + "_capture";

                view_layout.buildDrawingCache();
                Bitmap bitmap = view_layout.getDrawingCache();
                FileOutputStream fos = null;

                File uploadFolder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/");

                if (!uploadFolder.exists()) {
                    uploadFolder.mkdir();
                    Toast.makeText(getApplicationContext(), "폴더가 생성되었습니다.",
                            Toast.LENGTH_SHORT).show();
                }

                String str_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/";


                try {
                    fos = new FileOutputStream(str_path+current_time+".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    Toast.makeText(getApplicationContext(), "이미지 저장",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                MediaScanner ms = MediaScanner.newInstance(getApplicationContext());

                try {
                    ms.mediaScanning(str_path + current_time + ".jpg");
                }catch (Exception e) {
                    e.printStackTrace();
                }

                view_layout.destroyDrawingCache();
            }
        });

        zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_zoomin = !control_zoomin;
                control_bright = false;
                control_mosaic = false;

                if (control_zoomin) {
                    sBar.setVisibility(View.VISIBLE);
                } else {
                    sBar.setVisibility(View.INVISIBLE);
                }


                select_sBar = "zoom_in";

                sBar.setProgress((int) (scaleX * 50));

                myview.invalidate();
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBar.setVisibility(View.INVISIBLE);
                control_zoomin = false;
                control_bright = false;
                control_mosaic = false;

                if (control_rotate_right) {
                    angle += 20;
                } else {
                    angle -= 20;
                }

                myview.invalidate();

            }
        });

        bright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_bright = !control_bright;
                control_contrast = false;
                control_zoomin = false;
                control_mosaic = false;

                if (control_bright) {
                    sBar.setVisibility(View.VISIBLE);
                } else {
                    sBar.setVisibility(View.INVISIBLE);
                }

                select_sBar = "bright";

                sBar.setProgress((int) (RGB_bright * 50));

                myview.invalidate();
            }
        });

        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBar.setVisibility(View.INVISIBLE);
                control_zoomin = false;
                control_bright = false;
                control_mosaic = false;

                control_gray = !control_gray;
                myview.invalidate();
            }
        });

        blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_blur = !control_blur;
                control_zoomin = false;
                control_bright = false;
                control_mosaic = false;

                if (control_blur) {
                    sBar.setVisibility(View.VISIBLE);
                } else {
                    sBar.setVisibility(View.INVISIBLE);
                }

                select_sBar = "blur";

                sBar.setProgress((int) blur_radius);

                myview.invalidate();
            }
        });

        mosaic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_mosaic = !control_mosaic;
                control_zoomin = false;
                control_bright = false;


            }
        });

        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_contrast = !control_contrast;
                control_zoomin = false;
                control_bright = false;
                control_mosaic = false;


                if (control_contrast) {
                    sBar.setVisibility(View.VISIBLE);
                } else {
                    sBar.setVisibility(View.INVISIBLE);
                }

                select_sBar = "contrast";

                sBar.setProgress((int) (RGB_bright * 50));

                myview.invalidate();
            }
        });

        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_pen = !control_pen;
                control_zoomin = false;
                control_bright = false;
                control_mosaic = false;
                control_stamp = false;

                if (control_pen) {
                    pen.setBackgroundColor(0XFFaaaaaa);
                    stamp.setBackgroundColor(0XFFDDDDDD);
                } else {
                    pen.setBackgroundColor(0XFFDDDDDD);
                }


            }
        });

        stamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_stamp = !control_stamp;
                control_zoomin = false;
                control_bright = false;
                control_mosaic = false;
                control_pen = false;

                if (control_stamp) {
                    stamp.setBackgroundColor(0XFFaaaaaa);
                    pen.setBackgroundColor(0XFFDDDDDD);
                } else {
                    stamp.setBackgroundColor(0XFFDDDDDD);
                }

            }
        });

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_eraser = true;
                control_zoomin = false;
                control_bright = false;
                control_mosaic = false;

                myview.invalidate();
            }
        });

        snow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBar.setVisibility(View.INVISIBLE);
                control_snow = !control_snow;
                control_zoomin = false;
                control_bright = false;
                control_mosaic = false;

                if (control_getImg) {
                    if (control_snow) {
                        getImg_buffer = snowEffect(getImg_buffer);
                    } else {
                        getImg_buffer = getImg_buffer_sub;
                    }
                }


                myview.invalidate();
            }
        });





        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float get_progress = sBar.getProgress();

                switch (select_sBar) {
                    case "zoom_in":
                        scaleX = scaleY = get_progress / 50;
                        myview.invalidate();
                        break;
                    case "bright" :
                        RGB_bright = get_progress / 50;
                        myview.invalidate();
                        break;
                    case "blur" :
                        blur_radius = get_progress;
                        myview.invalidate();
                        break;
                    case "contrast" :
                        RGB_bright = get_progress / 50;
                        myview.invalidate();
                        break;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        }

    private Bitmap snowEffect(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width*height];
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        Random random = new Random();

        int R, G, B, index = 0, threshold;
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                index = y*width + x;

                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);

                threshold = random.nextInt(2000);
                if (threshold > 1998) {
                    pixels[index] = Color.rgb(255, 255, 255);

                    if (index + 9*width+9 < pixels.length) {
                        for(int i=1; i<10; i++) {
                            pixels[index+i] = Color.rgb(255, 255, 255);
                        }

                        for (int i=1; i<10; i++) {
                            for (int j=0; j<10; j++) {
                                pixels[index + i*width+j] = Color.rgb(255, 255, 255);
                            }
                        }

                    }

                }
            }
        }

        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);


        return bmOut;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();

        if (v == rotate) {
            menu.setHeaderTitle("회전 방향");

            menuInflater.inflate(R.menu.menu_rotate, menu);
        } else if (v == blur) {
            menu.setHeaderTitle("블러링 종류");

            menuInflater.inflate(R.menu.menu_blur, menu);
        } else if (v == pen) {
            menu.setHeaderTitle("선 두께");

            menuInflater.inflate(R.menu.menu_pen, menu);
        } else if (v == stamp) {
            menu.setHeaderTitle("스탬프 선택");

            menuInflater.inflate(R.menu.menu_stamp, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.right :
                control_rotate_right = true;
                break;
            case R.id.left :
                control_rotate_right = false;
                break;
            case R.id.normal :
                sel_blur_type = NORMAL;
                myview.invalidate();
                break;
            case R.id.inner :
                sel_blur_type = INNER;
                myview.invalidate();
                break;
            case R.id.outer :
                sel_blur_type = OUTER;
                myview.invalidate();
                break;
            case R.id.solid :
                sel_blur_type = SOLID;
                myview.invalidate();
                break;
            case R.id.mm3 :
                pen_thickness = 3;
                myview.invalidate();
                break;
            case R.id.mm5 :
                pen_thickness = 5;
                myview.invalidate();
                break;
            case R.id.mm10 :
                pen_thickness = 10;
                myview.invalidate();
                break;
            case R.id.mm15 :
                pen_thickness = 15;
                myview.invalidate();
                break;
            case R.id.mm20 :
                pen_thickness = 20;
                myview.invalidate();
                break;
            case R.id.black :
                pen_color = Color.BLACK;
                myview.invalidate();
                break;
            case R.id.gray :
                pen_color = Color.GRAY;
                myview.invalidate();
                break;
            case R.id.red :
                pen_color = Color.RED;
                myview.invalidate();
                break;
            case R.id.green :
                pen_color = Color.GREEN;
                myview.invalidate();
                break;
            case R.id.blue :
                pen_color = Color.BLUE;
                myview.invalidate();
                break;
            case R.id.stamp1 :
                select_stamp = R.drawable.stamp1;
                myview.invalidate();
                break;
            case R.id.stamp2 :
                select_stamp = R.drawable.stamp2;
                myview.invalidate();
                break;
            case R.id.stamp3 :
                select_stamp = R.drawable.stamp3;
                myview.invalidate();
                break;

        }

        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    getImg_buffer = getImg_buffer_sub = img;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }




    private class myView extends View {

        int img_startX;
        int img_startY;
        int img_endX;
        int img_endY;

        Paint paint[] = new Paint[100];
        Path path[] = new Path[100];
        int path_idx = 1;

        int stamp1_sites[][] = new int[50][2];
        int stamp1_idx = 0;
        int stamp2_sites[][] = new int[50][2];
        int stamp2_idx = 0;
        int stamp3_sites[][] = new int[50][2];
        int stamp3_idx = 0;

        int mosaic_rect_startX, mosaic_rect_startY;
        int mosaic_rect_endX, mosaic_rect_endY;
        boolean draw_rect = true;

        public myView(Context context) {
            super(context);
            init();
        }

        private void init() {
            for(int i=0; i<path.length; i++) {
                paint[i] = new Paint();
                path[i] = new Path();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int cenX = view_layout.getWidth() / 2;
            int cenY = view_layout.getHeight() / 2;

            canvas.scale(scaleX, scaleY, cenX, cenY);
            canvas.rotate(angle, cenX, cenY);

            paint[path_idx].setColor(pen_color);
            paint[path_idx].setStyle(Paint.Style.STROKE);
            paint[path_idx].setStrokeWidth(pen_thickness);


            if (control_contrast) {
                bright_sign = -1;
                brightness = 255;
            } else {
                bright_sign = 1;
                brightness = 0;
            }

            float[] array = {(bright_sign) * RGB_bright, 0, 0, 0, brightness,
                             0, (bright_sign) * RGB_bright, 0, 0, brightness,
                             0, 0, (bright_sign) * RGB_bright, 0, brightness,
                              0, 0, 0, 1, 0};

            ColorMatrix cm = new ColorMatrix(array);
            if (control_gray) cm.setSaturation(0);

            paint[0].setColorFilter(new ColorMatrixColorFilter(cm));

            BlurMaskFilter bMask;
            switch (sel_blur_type) {
                case INNER :
                    bMask = new BlurMaskFilter(blur_radius+1, BlurMaskFilter.Blur.INNER);
                    break;
                case OUTER :
                    bMask = new BlurMaskFilter(blur_radius+1, BlurMaskFilter.Blur.OUTER);
                    break;
                case SOLID :
                    bMask = new BlurMaskFilter(blur_radius+1, BlurMaskFilter.Blur.SOLID);
                    break;
                default :
                    bMask = new BlurMaskFilter(blur_radius+1, BlurMaskFilter.Blur.NORMAL);
            }

            if (control_blur) {
                paint[0].setMaskFilter(bMask);
            } else {
                paint[0].setMaskFilter(null);
            }


            if (control_getImg) {
                getImg_method(canvas);
            }

            for (int i=1; i<=path_idx; i++) {
                canvas.drawPath(path[i], paint[i]);
            }

            draw_stamp(canvas);

            if (control_eraser) {
                eraser_method();
            }

            if (control_getImg && control_mosaic) {
                mosaic_method(canvas);
            }

        }

        private void getImg_method(Canvas canvas) {
            img_startX = (view_layout.getWidth() - getImg_buffer.getWidth()) / 2;
            img_startY = (view_layout.getHeight() - getImg_buffer.getHeight()) / 2;
            img_endX = img_startX + getImg_buffer.getWidth();
            img_endY = img_startY + getImg_buffer.getHeight();

            canvas.drawBitmap(getImg_buffer, img_startX, img_startY, paint[0]);
        }

        private void mosaic_method (Canvas canvas) {
            Paint mosaic_paint = new Paint();
            mosaic_paint.setColor(Color.GREEN);
            mosaic_paint.setStyle(Paint.Style.STROKE);
            mosaic_paint.setStrokeWidth(10);

            Rect rect = new Rect(mosaic_rect_startX, mosaic_rect_startY, mosaic_rect_endX, mosaic_rect_endY);

            if (draw_rect) {
                canvas.drawRect(rect, mosaic_paint);
            } else {
                getImg_buffer = mosaicEffect(getImg_buffer);
                canvas.drawBitmap(getImg_buffer, img_startX, img_startY, paint[0]);

            }
        }

        private Bitmap mosaicEffect (Bitmap source) {

            int width = source.getWidth();
            int height = source.getHeight();

            int mosaic_startX = mosaic_rect_startX - img_startX;
            int mosaic_startY = mosaic_rect_startY - img_startY;
            int mosaic_endX = mosaic_rect_endX - img_startX;
            int mosaic_endY = mosaic_rect_endY - img_startY;

            int mosaic_width = mosaic_endX-mosaic_startX;
            int mosaic_height = mosaic_endY-mosaic_startY;

            int[] pixels = new int[width*height];
            source.getPixels(pixels, 0, width, 0, 0, width, height);

            int[] pixels_sub = new int [(mosaic_width) * (mosaic_height)];

            for (int i=0; i<mosaic_height; i++) {
                for (int j=0; j<mosaic_width; j++) {
                    pixels_sub[i*(mosaic_width) + j] = pixels[(i+mosaic_startY)*width + mosaic_startX + j];
                }
            }

            Bitmap bmRect = Bitmap.createBitmap(mosaic_width, mosaic_height, Bitmap.Config.ARGB_8888);
            bmRect.setPixels(pixels_sub, 0, mosaic_width, 0, 0, mosaic_width, mosaic_height);

            Bitmap temp = Bitmap.createScaledBitmap(bmRect, 5, 5, false);
            bmRect = Bitmap.createScaledBitmap(temp, bmRect.getWidth(), bmRect.getHeight(), false);

            bmRect.getPixels(pixels_sub, 0, bmRect.getWidth(), 0, 0, bmRect.getWidth(), bmRect.getHeight());

            for (int i=0; i<mosaic_height; i++) {
                for (int j=0; j<mosaic_width; j++) {
                    pixels[(i+mosaic_startY)*width + mosaic_startX + j] = pixels_sub[i*(mosaic_width) + j];
                }

            }

            bmRect.recycle();
            temp.recycle();


            Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmOut.setPixels(pixels, 0, width, 0, 0, width, height);


            return bmOut;
        }




        private void draw_stamp(Canvas canvas) {
            Bitmap stamp1_img = BitmapFactory.decodeResource(getResources(), R.drawable.stamp1);

            for (int i=0; i<stamp1_idx; i++) {
                canvas.drawBitmap(stamp1_img, stamp1_sites[i][0], stamp1_sites[i][1], paint[0]);
            }
            stamp1_img.recycle();

            Bitmap stamp2_img = BitmapFactory.decodeResource(getResources(), R.drawable.stamp2);

            for (int i=0; i<stamp2_idx; i++) {
                canvas.drawBitmap(stamp2_img, stamp2_sites[i][0], stamp2_sites[i][1], paint[0]);
            }
            stamp2_img.recycle();

            Bitmap stamp3_img = BitmapFactory.decodeResource(getResources(), R.drawable.stamp3);

            for (int i=0; i<stamp3_idx; i++) {
                canvas.drawBitmap(stamp3_img, stamp3_sites[i][0], stamp3_sites[i][1], paint[0]);
            }
            stamp3_img.recycle();
        }

        private void eraser_method() {
            path_idx = 1;
            stamp1_idx = 0;
            stamp2_idx = 0;
            stamp3_idx = 0;

            for (int i=0; i<path.length; i++) {
                path[i] = new Path();
            }

            control_eraser = false;

            invalidate();
        }



        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();



            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (control_pen)
                        path[path_idx].moveTo(x, y);

                    if (control_stamp) {

                        switch (select_stamp) {
                            case R.drawable.stamp1 :
                                stamp1_sites[stamp1_idx][0] = x;
                                stamp1_sites[stamp1_idx++][1] = y;
                                if (stamp1_idx > 50) stamp1_idx = 50;
                                break;
                            case R.drawable.stamp2 :
                                stamp2_sites[stamp2_idx][0] = x;
                                stamp2_sites[stamp2_idx++][1] = y;
                                if (stamp2_idx > 50) stamp2_idx = 50;
                                break;
                            case R.drawable.stamp3 :
                                stamp3_sites[stamp3_idx][0] = x;
                                stamp3_sites[stamp3_idx++][1] = y;
                                if (stamp3_idx > 50) stamp3_idx = 50;
                                break;
                        }


                    }

                    if (control_mosaic) {
                        if (x < img_startX) {
                            mosaic_rect_startX = img_startX;
                        } else if (x > img_endX) {
                            mosaic_rect_startX = img_endX;
                        } else {
                            mosaic_rect_startX = x;
                        }

                        if (y < img_startY) {
                            mosaic_rect_startY = img_startY;
                        } else if (y > img_endY) {
                            mosaic_rect_startY = img_endY;
                        } else {
                            mosaic_rect_startY = y;
                        }

                        mosaic_rect_endX = mosaic_rect_startX;
                        mosaic_rect_endY = mosaic_rect_startY;

                        draw_rect = true;
                    }

                    break;

                case MotionEvent.ACTION_MOVE:
                    if (control_pen)
                        path[path_idx].lineTo(x, y);

                    if (control_mosaic) {
                        if (x < img_startX) {
                            mosaic_rect_endX = img_startX;
                        } else if (x > img_endX) {
                            mosaic_rect_endX = img_endX;
                        } else {
                            mosaic_rect_endX = x;
                        }

                        if (y < img_startY) {
                            mosaic_rect_endY = img_startY;
                        } else if (y > img_endY) {
                            mosaic_rect_endY = img_endY;
                        } else {
                            mosaic_rect_endY = y;
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (control_pen) {
                        path[path_idx++].lineTo(x, y);
                        if (path_idx > 100) path_idx = 100;
                    }

                    if (control_mosaic) {
                        draw_rect = false;
                    }

                    break;

            }



            invalidate();
            return true;
        }





    }



}