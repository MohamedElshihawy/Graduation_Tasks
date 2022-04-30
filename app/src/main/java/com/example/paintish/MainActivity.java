package com.example.paintish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout ;
    ImageButton undo,redo,color,save,clear,brush , next , previous ;
    RangeSlider strokeWidth;
    CustomView clipboard;
    int letterIndex;
    boolean show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        color = findViewById(R.id.color_palette);
        save = findViewById(R.id.save);
        clear = findViewById(R.id.clear);
        previous = findViewById(R.id.back_arrow);
        next = findViewById(R.id.forward_Arrow);
        brush = findViewById(R.id.show_slider);
        strokeWidth = findViewById(R.id.width_slider);
        clipboard = findViewById(R.id.clipboard);
        linearLayout = findViewById(R.id.linearLayout);
        Matrix matrix = new Matrix();
        letterIndex = 0;

        show = false;

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(letterIndex<Matrix.letters.size())
                {

                    clipboard.drawLetters(Matrix.letters.get(letterIndex));
                    letterIndex +=1;
                    Log.i("TAG", "onClick: " + letterIndex );
                }
                else
                {
                    Toast.makeText(MainActivity.this, "This is the last letter", Toast.LENGTH_SHORT).show();
                }
            }
        });


        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(letterIndex>0)
                {

                    clipboard.drawLetters(Matrix.letters.get(letterIndex));
                    letterIndex -=1;
                }
                else
                {
                    Toast.makeText(MainActivity.this, "No previous Letters ", Toast.LENGTH_SHORT).show();
                }
            }
        });



        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipboard.undoChanges();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipboard.redoChanges();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipboard.clearScreen();

            }
        });

        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strokeWidth.getVisibility()==View.VISIBLE){
                    strokeWidth.setVisibility(View.GONE);
                }
                else if(strokeWidth.getVisibility()==View.GONE){
                    strokeWidth.setVisibility(View.VISIBLE);
                }
            }
        });

        strokeWidth.setValueFrom(0.0f);
        strokeWidth.setValueTo(100.0f);

        strokeWidth.addOnChangeListener(new RangeSlider.OnChangeListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                clipboard.setStrokeSize((int)value);
            }
        });


        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openColorPicker();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                savingDialog();

            }
        });




        ViewTreeObserver vto = clipboard.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                clipboard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = clipboard.getMeasuredWidth();
                int height = clipboard.getMeasuredHeight();
                clipboard.initialize(height, width);
            }
        });


    }

    private void savingDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        EditText imageName = new EditText(this);

        dialog.setTitle("Image Name").setMessage("Enter a valid name ").setView(imageName);

        dialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = imageName.getText().toString();
                saveBitmap(clipboard.saveDrawing(),name);
            }
        });
        dialog.show();
    }

    private void saveBitmap( Bitmap bitmap, String name) {
        String Name = name + "." + "jpg";
        FileOutputStream outputStream = null ;

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File savingDirectory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File path = new File(savingDirectory,Name);

        try {
            outputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            Log.i("TAG", "saveBitmap:  finisheeeeeeeeeeeeeeeed");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                assert outputStream != null;
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openColorPicker() {


        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, Color.BLACK, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                clipboard.setColor(color);
            }
        });
        ambilWarnaDialog.show();
    }



}
