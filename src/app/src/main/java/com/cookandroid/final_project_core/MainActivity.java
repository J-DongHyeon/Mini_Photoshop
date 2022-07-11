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

    //각 이미지 버튼들이 활성화 됬는지 아닌지 나타내는 flag 역할을 하는 변수들
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
    boolean control_mosaic_draw = false;
    boolean control_mosaic_show = false;

    //이미지 확대, 축소, 회전 등 여러 가지 이미지 처리에 대한 값을 가지고 있는 변수들
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
        
        // 메인 layout을 화면에 나타낸다.
        setContentView(R.layout.activity_main);

        setTitle("미니 포토샵");

        // 메인 layout에 있는 여러 가지 요소들을 id로 찾아 가져온다.
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

        // myView 클래스의 객체를 담을 (그림판 역할을 할) layout 을 가져온다.
        view_layout = (LinearLayout) findViewById(R.id.view_layout);
        myview = new myView(this);
        view_layout.addView(myview);

        // rotate, blur, pen, stamp 이미지 버튼들은 long 클릭이 가능하도록 등록한다.
        // 이 이미지 버튼들은 long 클릭 했을 때 컨텍스트 메뉴가 나타날 것이다.
        registerForContextMenu(rotate);
        registerForContextMenu(blur);
        registerForContextMenu(pen);
        registerForContextMenu(stamp);

        // return_state 버튼은 되돌리기 버튼이다.
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
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                control_mosaic_show = false;
                myview.mosaic_eraser();


                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

                sBar.setProgress(50);
                myview.invalidate();
            }
        });

        // getImg 버튼은 가져오기 버튼이다.
        getImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                control_getImg = true;

                // getImg 이미지 버튼을 누르면 갤러리 액티비티를 실행시킨다.
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
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;

                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

                sBar.setProgress(50);

            }
        });

        // saveImg 버튼은 저장하기 버튼이다.
        saveImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

                SimpleDateFormat day = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date();
                String current_time = day.format(date) + "_capture";

                // 그림판에 그려져 있는 것을 cache로 가져온다.
                view_layout.buildDrawingCache();
                
                // cache에 저장되어 있는 것을 bitmap으로 가져온다.
                Bitmap bitmap = view_layout.getDrawingCache();
                
                FileOutputStream fos = null;
 
                // 비트맵을 저장할 폴더를 확인한다.
                File uploadFolder = Environment.getExternalStoragePublicDirectory("/DCIM/Camera/");
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdir();
                    Toast.makeText(getApplicationContext(), "폴더가 생성되었습니다.",
                            Toast.LENGTH_SHORT).show();
                }

                // 비트맵을 저장할 폴더의 경로
                String str_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/";

                try {
                    // fileoutputstream 을 생성한다. fileoutputstream 을 통해 특정 경로의 폴더에 접근한다.
                    fos = new FileOutputStream(str_path+current_time+".jpg");
                    
                    // 앞서 cache에서 가져온 bitmap을 압축하여 fos에 저장한다.
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    
                    Toast.makeText(getApplicationContext(), "이미지 저장",
                            Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                MediaScanner ms = MediaScanner.newInstance(getApplicationContext());

                try {
                    // 미디어스캐닝을 통해 특정 경로의 폴더를 스캔하여 갤러리로 가져온다. (갤러리 새로고침)
                    ms.mediaScanning(str_path + current_time + ".jpg");
                }catch (Exception e) {
                    e.printStackTrace();
                }

                view_layout.destroyDrawingCache();
            }
        });

        // zoom_in 버튼은 확대 버튼이다.
        zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_zoomin = !control_zoomin;

                if (control_zoomin) {
                    sBar.setVisibility(View.VISIBLE);
                } else {
                    sBar.setVisibility(View.INVISIBLE);
                }

                control_bright = false;
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);


                select_sBar = "zoom_in";

                sBar.setProgress((int) (scaleX * 50));

                // 화면을 무효화시킨 후 onDraw() 메소드를 실행시킨다. (화면 갱신)
                myview.invalidate();
            }
        });

        // rotate 버튼은 회전 버튼이다.
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBar.setVisibility(View.INVISIBLE);
                control_zoomin = false;
                control_bright = false;
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

                if (control_rotate_right) {
                    angle += 20;
                } else {
                    angle -= 20;
                }

                myview.invalidate();

            }
        });

        // bright 버튼은 밝기조절 버튼이다.
        bright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_bright = !control_bright;
                control_contrast = false;
                control_zoomin = false;
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

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

        // gray 버튼은 흑백 변환 버튼이다.
        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBar.setVisibility(View.INVISIBLE);
                control_zoomin = false;
                control_bright = false;
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

                control_gray = !control_gray;
                myview.invalidate();
            }
        });

        // blur 버튼은 blur 조절 버튼이다.
        blur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_blur = !control_blur;
                control_zoomin = false;
                control_bright = false;
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

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

        // mosaic 버튼은 모자이크  버튼이다.
        mosaic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이미지를 가져온 상태가 아니라면 모자이크 적용을 하지 않는다.
                if (!control_getImg) {
                    control_mosaic_draw = false;
                    control_mosaic_show = false;
                } else {
                    control_mosaic_draw = !control_mosaic_draw;
                    control_mosaic_show = true;
                }

                sBar.setVisibility(View.INVISIBLE);

                control_zoomin = false;
                control_bright = false;
                control_pen = false;
                control_stamp = false;

                if (control_mosaic_draw) {
                    mosaic.setBackgroundColor(0XFFaaaaaa);
                } else {
                    mosaic.setBackgroundColor(0XFFDDDDDD);
                }

                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
            }
        });

        // contrast 버튼은 색 반전 후 밝기 조절 버튼이다.
        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_contrast = !control_contrast;
                control_zoomin = false;
                control_bright = false;
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);


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

        // pen 버튼은 그림판에 펜 그리기 버튼이다.
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_pen = !control_pen;
                control_zoomin = false;
                control_bright = false;
                control_mosaic_draw = false;
                control_stamp = false;

                sBar.setVisibility(View.INVISIBLE);

                if (control_pen) {
                    pen.setBackgroundColor(0XFFaaaaaa);
                } else {
                    pen.setBackgroundColor(0XFFDDDDDD);
                }

                mosaic.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
            }
        });

        // stamp 버튼은 그림판에 스탬프 찍기 버튼이다.
        stamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_stamp = !control_stamp;
                control_zoomin = false;
                control_bright = false;
                control_mosaic_draw = false;
                control_pen = false;

                sBar.setVisibility(View.INVISIBLE);

                if (control_stamp) {
                    stamp.setBackgroundColor(0XFFaaaaaa);
                } else {
                    stamp.setBackgroundColor(0XFFDDDDDD);
                }

                pen.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

            }
        });

        // eraser 버튼은 그림판 지우기 버튼이다.
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_eraser = true;
                control_zoomin = false;
                control_bright = false;
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

                sBar.setVisibility(View.INVISIBLE);

                myview.invalidate();
            }
        });

        // snow 버튼은 그림판에 눈내리기 효과 버튼이다.
        snow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sBar.setVisibility(View.INVISIBLE);
                control_snow = !control_snow;
                control_zoomin = false;
                control_bright = false;
                control_pen = false;
                control_stamp = false;
                control_mosaic_draw = false;
                pen.setBackgroundColor(0XFFDDDDDD);
                stamp.setBackgroundColor(0XFFDDDDDD);
                mosaic.setBackgroundColor(0XFFDDDDDD);

                if (control_getImg) {
                    if (control_snow) {
                        // 원본 이미지에 눈내리기 효과를 
                        getImg_buffer = snowEffect(getImg_buffer);
                    } else {
                        getImg_buffer = getImg_buffer_sub;
                    }
                }
                myview.invalidate();
            }
        });

        // seekbar 값이 변할 때 실행되는 함수이다.
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
        
        // 원본 이미지를 1차원 배열에 담아 놓는다.
        int[] pixels = new int[width*height];
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        Random random = new Random();

        int R, G, B, index = 0, threshold;
        
        // 각 픽셀들을 순서대로 접근한다.
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                index = y*width + x;

                // 각 픽셀들의 r, g, b 값을 받아온다.
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);

                // 각 픽셀들을 돌다가, 무작위 값이 1998 이상이 되면 그 픽셀 주변의 r, g, b 값을 255로 하여 흰색으로 한다. (눈 효과)
                threshold = random.nextInt(2000);
                if (threshold > 1998) {
                    pixels[index] = Color.rgb(255, 255, 255);

                    // 현재 픽셀을 기준으로 상하좌우 10 픽셀 까지 접근한다.
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

    // 버튼을 long 클릭 했을 때 실행되는 메소드이다. (컨텍스트 메뉴 생성)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // 컨텍스트 메뉴에 대해 미리 xml 파일로 만들어 놓은 것을 menuinflater를 이용하여 가져올 것이다.
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

    // 컨텍스트 메뉴의 아이템을 클릭 했을 때 실행되는 메소드이다.
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

    // 갤러리 액티비티 실행이 끝났을 때 실행되는 메소드이다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    // 갤러리 액티비티 실행 후의 결과 데이터 (이미지) 를 입력 스트림에 저장한다.
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    
                    // 입력 스트림에 있는 이미지를 비트맵으로 반환한다.
                    Bitmap img = BitmapFactory.decodeStream(in);
                    
                    in.close();
                    getImg_buffer = getImg_buffer_sub = img;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 그림판 역할을 하는 view 이다. view_layout 레이아웃에 들어갈 것이다.
    private class myView extends View {

        // 이미지의 왼쪽 위 좌표와 오른쪽 아래 좌표
        int img_startX;
        int img_startY;
        int img_endX;
        int img_endY;

        // 그림판에 그림을 그릴 펜에 대한 paint와 path 이다. 여러 개의 펜으로 여러 경로를 그릴 수 있으므로 넉넉하게 100개를 만들었다.
        // 0번 인덱스 paint는 이미지에 대한 paint 이다.
        Paint paint[] = new Paint[100];
        Path path[] = new Path[100];
        int path_idx = 1;

        // 3개 종류 스탬프에 대한 위치 정보를 가지고 있는 배열이다. 여러 개의 스탬프를 찍을 수 있으므로 넉넉하게 50개를 만들었다.
        int stamp1_sites[][] = new int[50][2];
        int stamp1_idx = 0;
        int stamp2_sites[][] = new int[50][2];
        int stamp2_idx = 0;
        int stamp3_sites[][] = new int[50][2];
        int stamp3_idx = 0;

        // 모자이크의 왼쪽 위, 오른쪽 아래 좌표를 담을 변수이다.
        // draw_rect는 모자이크를 그리는 중인가 아닌가에 대한 flag 역할을 한다.
        int mosaic_cv_startX, mosaic_cv_startY;
        int mosaic_cv_endX, mosaic_cv_endY;
        boolean draw_rect = true;

        // 모자이크를 여러 개 그릴 수도 있다. 모자이크와 모자이크의 위치 정보를 배열에 저장해 둔다. 넉넉하게 100개 크기로 만들었다.
        int mosaic_idx = 0;
        Bitmap mosaic_bm_sub[] = new Bitmap[100];
        int mosaic_cv_startX_arr[] = new int[100];
        int mosaic_cv_startY_arr[] = new int[100];

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

            // view_layout의 중심 좌표이다.
            int cenX = view_layout.getWidth() / 2;
            int cenY = view_layout.getHeight() / 2;

            // 확대, 축소, 회전 정보에 따라 cenX, cenY를 중심으로 이미지 처리를 한다.
            canvas.scale(scaleX, scaleY, cenX, cenY);
            canvas.rotate(angle, cenX, cenY);

            // 현재 펜의 정보를 저장한다.
            paint[path_idx].setColor(pen_color);
            paint[path_idx].setStyle(Paint.Style.STROKE);
            paint[path_idx].setStrokeWidth(pen_thickness);

            // 색 반전 버튼이 눌려있으면 반전시키고 아니면 반전시키지 않는다.
            if (control_contrast) {
                bright_sign = -1;
                brightness = 255;
            } else {
                bright_sign = 1;
                brightness = 0;
            }

            // 여러 가지 색 정보에 따라서 color metrix를 생성한다.
            float[] array = {(bright_sign) * RGB_bright, 0, 0, 0, brightness,
                             0, (bright_sign) * RGB_bright, 0, 0, brightness,
                             0, 0, (bright_sign) * RGB_bright, 0, brightness,
                              0, 0, 0, 1, 0};
            ColorMatrix cm = new ColorMatrix(array);
            
            // gray 버튼이 눌려있으면 color metrix를 무시한다.
            if (control_gray) cm.setSaturation(0);

            paint[0].setColorFilter(new ColorMatrixColorFilter(cm));

            // sel_blur_type 의 정보에 따라 블러링 마스크를 생성한다.
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

            // 블러링 버튼이 눌려있으면 블러링 마스크를 적용한다.
            if (control_blur) {
                paint[0].setMaskFilter(bMask);
            } else {
                paint[0].setMaskFilter(null);
            }

            // 이미지가 view_layout에 출력되어 있는 상태에서 모자이크 버튼이 눌렸으면 모자이크 효과를 적용한다.
            // 모자이크 버튼은 안 눌렸으면 모자이크 효과는 넣지 않는다.
            if (control_getImg) {
                if (control_mosaic_show) {
                    mosaic_method(canvas);
                } else {
                    getImg_method(canvas);
                }
            }

            // 현재까지 저장되어 있는 path, paint 정보에 따라서 펜으로 그림판에 그린다.
            for (int i=1; i<=path_idx; i++) {
                canvas.drawPath(path[i], paint[i]);
            }

            // 현재까지 저장되어 있는 스탬프 정보에 따라서 스탬프를 그림판에 찍는다.
            draw_stamp(canvas);

            // 지우개 버튼이 눌 그림판의 효과들을 지운다.
            if (control_eraser) {
                eraser_method();
            }


        }

        // 현재 출력되어 있는 이미지에 대해 paint[0]를 적용시켜서 그림판에 출력한다.
        private void getImg_method(Canvas canvas) {
            img_startX = (view_layout.getWidth() - getImg_buffer.getWidth()) / 2;
            img_startY = (view_layout.getHeight() - getImg_buffer.getHeight()) / 2;
            img_endX = img_startX + getImg_buffer.getWidth();
            img_endY = img_startY + getImg_buffer.getHeight();

            canvas.drawBitmap(getImg_buffer, img_startX, img_startY, paint[0]);
        }

        // 모자이크 버튼이 눌렸으면 모자이크 효과를 적용한다.
        private void mosaic_method (Canvas canvas) {
            Paint mosaic_paint = new Paint();
            mosaic_paint.setColor(Color.GREEN);
            mosaic_paint.setStyle(Paint.Style.STROKE);
            mosaic_paint.setStrokeWidth(10);

            // 현재 그리는 중인 모자이크에 대한 사각형이다.
            Rect rect = new Rect(mosaic_cv_startX, mosaic_cv_startY, mosaic_cv_endX, mosaic_cv_endY);

            // 우선 원본 이미지를 그림판에 그린다.
            canvas.drawBitmap(getImg_buffer, img_startX, img_startY, paint[0]);

            // 모자이크를 그리는 중이면 그림판에 초록 사각형을 나타내고, 
            // 모자이크 그리기를 완료 했으면 모자이크 효과를 적용한다.
            if (draw_rect) {
                canvas.drawRect(rect, mosaic_paint);
            } else {
                mosaicEffect(canvas, getImg_buffer);
            }
        }

        // 이미지에 모자이크 효과를 내는 메소드이다.
        private void mosaicEffect (Canvas canvas, Bitmap source) {

            int mosaic_width = mosaic_cv_endX - mosaic_cv_startX;
            int mosaic_height = mosaic_cv_endY - mosaic_cv_startY;

            // 원본 이미지를 5 X 5 크기로 줄인 후, 이를 다시 모자이크 사각형 크기에 맞추어 늘린다.
            // (마지막 인자를 false로 하였으므로 5 X 5 픽셀 그대로 늘어난다. 따라서 이미지가 깨져 보이므로 모자이크 효과가 난다.)
            // 이 모자이크를 배열에 저장해 둔다.
            mosaic_bm_sub[mosaic_idx] = Bitmap.createScaledBitmap(source, 5, 5, false);
            mosaic_bm_sub[mosaic_idx] = Bitmap.createScaledBitmap(mosaic_bm_sub[mosaic_idx], mosaic_width, mosaic_height, false);

            // 모자이크의 위치 정보를 배열에 저장해 둔다.
            mosaic_cv_startX_arr[mosaic_idx] = mosaic_cv_startX;
            mosaic_cv_startY_arr[mosaic_idx] = mosaic_cv_startY;

            // 저장되어 있는 여러 모자이크 들을 순서대로 그림판에 그린다.
            for (int i=0; i<=mosaic_idx; i++) {
                canvas.drawBitmap(mosaic_bm_sub[i], mosaic_cv_startX_arr[i], mosaic_cv_startY_arr[i], paint[0]);
            }
            
            mosaic_idx++;
            if (mosaic_idx > 99) mosaic_idx = 99;


        }

        public void mosaic_eraser() {
            mosaic_idx = 0;
        }

        // 현재까지 저장되어 있는 스탬프 정보에 따라서 스탬프를 그림판에 찍는다.
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

        // 지우개 버튼이 눌리면 그림판의 효과들을 지운다.
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

        // myview에 터치 했을 때 실행되는 메소드이다.
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                
                // 마우스 왼쪽 클릭을 한 경우
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
                        invalidate();
                    }

                    if (control_getImg && control_mosaic_draw) {
                        if (x < img_startX) {
                            mosaic_cv_startX = img_startX;
                        } else if (x > img_endX) {
                            mosaic_cv_startX = img_endX;
                        } else {
                            mosaic_cv_startX = x;
                        }

                        if (y < img_startY) {
                            mosaic_cv_startY = img_startY;
                        } else if (y > img_endY) {
                            mosaic_cv_startY = img_endY;
                        } else {
                            mosaic_cv_startY = y;
                        }

                        mosaic_cv_endX = mosaic_cv_startX;
                        mosaic_cv_endY = mosaic_cv_startY;

                        draw_rect = true;
                        invalidate();
                    }

                    break;

                // 왼쪽 버튼을 클릭한 채로 마우스를 움직이는 경우
                case MotionEvent.ACTION_MOVE:
                    if (control_pen) {
                        path[path_idx].lineTo(x, y);
                        invalidate();
                    }

                    if (control_getImg && control_mosaic_draw) {
                        if (x < img_startX) {
                            mosaic_cv_endX = img_startX;
                        } else if (x > img_endX) {
                            mosaic_cv_endX = img_endX;
                        } else {
                            mosaic_cv_endX = x;
                        }

                        if (y < img_startY) {
                            mosaic_cv_endY = img_startY;
                        } else if (y > img_endY) {
                            mosaic_cv_endY = img_endY;
                        } else {
                            mosaic_cv_endY = y;
                        }
                        invalidate();
                    }
                    break;

                // 마우스 왼쪽 버튼을 땐 경우
                case MotionEvent.ACTION_UP:
                    if (control_pen) {
                        path[path_idx++].lineTo(x, y);
                        if (path_idx > 100) path_idx = 100;

                        invalidate();
                    }

                    if (control_getImg && control_mosaic_draw) {

                        if (mosaic_cv_startX > mosaic_cv_endX) {
                            int temp = mosaic_cv_startX;
                            mosaic_cv_startX = mosaic_cv_endX;
                            mosaic_cv_endX = temp;
                        }

                        if (mosaic_cv_startY > mosaic_cv_endY) {
                            int temp = mosaic_cv_startY;
                            mosaic_cv_startY = mosaic_cv_endY;
                            mosaic_cv_endY = temp;
                        }

                        draw_rect = false;
                        invalidate();
                    }

                    break;
            }
            return true;
        }
    }
}
