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

public class MainActivity extends AppCompatActivity {

    ImageButton zoom_in, rotate, bright, gray, blur, mosaic, contrast,
            pen, stamp, eraser, snow;
    SeekBar sBar;
    Button getImg, saveImg;
    LinearLayout view_layout;

    myView myview;
    Bitmap getImg_buffer;

    boolean control_getImg = false;
    String select_sBar;
    boolean control_rotate_right = true;
    boolean control_gray = false;
    boolean control_blur = false;
    final int NORMAL = 1, INNER = 2, OUTER = 3, SOLID = 4;
    int sel_blur_type = NORMAL;
    boolean control_contrast = false;
    boolean control_pen = false;

    float scaleX = 1, scaleY = 1;
    float angle = 0;
    float RGB_bright = 1;
    float blur_radius = 50.0f;
    int brightness = 0;
    int bright_sign = 1;
    int pen_thickness = 3;
    int pen_color = Color.BLACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        view_layout = (LinearLayout) findViewById(R.id.view_layout);

        myview = new myView(this);
        view_layout.addView(myview);

        registerForContextMenu(rotate);
        registerForContextMenu(blur);
        registerForContextMenu(pen);



        getImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                control_getImg = true;

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 1);

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
                sBar.setVisibility(View.VISIBLE);

                select_sBar = "zoom_in";

                sBar.setProgress((int) (scaleX * 50));

                myview.invalidate();
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBar.setVisibility(View.INVISIBLE);

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
                sBar.setVisibility(View.VISIBLE);
                control_contrast = false;

                select_sBar = "bright";

                sBar.setProgress((int) (RGB_bright * 50));

                myview.invalidate();
            }
        });

        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBar.setVisibility(View.INVISIBLE);

                control_gray = !control_gray;
                myview.invalidate();
            }
        });

        blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_blur = !control_blur;

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

            }
        });

        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_contrast = !control_contrast;
                sBar.setVisibility(View.VISIBLE);

                select_sBar = "contrast";

                sBar.setProgress((int) (RGB_bright * 50));

                myview.invalidate();
            }
        });

        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_pen = !control_pen;

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
                    getImg_buffer = img;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }




    private class myView extends View {

        Paint paint[] = new Paint[100];
        Path path[] = new Path[100];
        int path_idx = 1;

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
                int picX = (view_layout.getWidth() - getImg_buffer.getWidth()) / 2;
                int picY = ((view_layout.getHeight() - getImg_buffer.getHeight()) / 2);
                canvas.drawBitmap(getImg_buffer, picX, picY, paint[0]);
            }



            for (int i=1; i<=path_idx; i++) {
                canvas.drawPath(path[i], paint[i]);
            }


        }



        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (control_pen) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        path[path_idx].moveTo(x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        path[path_idx].lineTo(x, y);
                        break;
                    case MotionEvent.ACTION_UP:
                        path[path_idx++].lineTo(x, y);
                        if (path_idx > 100) path_idx = 100;
                        break;

                }

            }

            invalidate();
            return true;
        }



    }



}