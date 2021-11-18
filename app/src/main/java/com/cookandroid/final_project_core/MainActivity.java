package com.cookandroid.final_project_core;

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
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ImageButton zoom_in, rotate, bright, gray, blur, emboss, mosaic, contrast,
            pen, stamp, eraser, snow;
    SeekBar sBar;
    Button getImg, saveImg;
    LinearLayout view_layout;

    Bitmap getImg_buffer;

    boolean control_getImg = false;

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
        view_layout.addView(new myView(this));

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

            }
        });

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

            if (control_getImg) {
                int picX = (view_layout.getWidth() - getImg_buffer.getWidth()) / 2;
                int picY = ((view_layout.getHeight() - getImg_buffer.getHeight()) / 2);
                canvas.drawBitmap(getImg_buffer, picX, picY, paint);
            }

        }



    }

}