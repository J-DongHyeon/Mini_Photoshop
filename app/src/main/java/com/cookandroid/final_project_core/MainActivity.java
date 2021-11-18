package com.cookandroid.final_project_core;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageButton zoom_in, rotate, bright, gray, blur, emboss, mosaic, contrast,
            pen, stamp, eraser, snow;
    SeekBar sBar;
    Button getImg, saveImg;
    LinearLayout view_layout;

    myView myview;
    Bitmap getImg_buffer;

    boolean control_getImg = false;
    String select_sBar;
    boolean control_sBar_visible;
    boolean control_rotate_right = true;

    float scaleX = 1, scaleY = 1;
    float angle = 0;

    float get_progress ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zoom_in = (ImageButton) findViewById(R.id.zoom_in);
        rotate = (ImageButton) findViewById(R.id.rotate);
        bright = (ImageButton) findViewById(R.id.bright);
        gray = (ImageButton) findViewById(R.id.gray);
        blur = (ImageButton) findViewById(R.id.blur);
        emboss = (ImageButton) findViewById(R.id.emboss);
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
                control_sBar_visible = !control_sBar_visible;

                if (control_sBar_visible) {
                    sBar.setVisibility(View.VISIBLE);
                } else {
                    sBar.setVisibility(View.INVISIBLE);
                }

                select_sBar = "zoom_in";

            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control_sBar_visible = false;
                sBar.setVisibility(View.INVISIBLE);

                if (control_rotate_right) {
                    angle += 20;
                } else {
                    angle -= 20;
                }

                myview.invalidate();

            }
        });



        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                get_progress = sBar.getProgress();

                switch (select_sBar) {
                    case "zoom_in":
                        scaleX = scaleY = get_progress / 50;
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
        public myView(Context context) { super(context); }

        private Paint paint = new Paint();
        private Path path = new Path();

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int cenX = view_layout.getWidth() / 2;
            int cenY = view_layout.getHeight() / 2;

            canvas.scale(scaleX, scaleY, cenX, cenY);
            canvas.rotate(angle, cenX, cenY);


            if (control_getImg) {
                int picX = (view_layout.getWidth() - getImg_buffer.getWidth()) / 2;
                int picY = ((view_layout.getHeight() - getImg_buffer.getHeight()) / 2);
                canvas.drawBitmap(getImg_buffer, picX, picY, paint);

            }

        }

    }



}