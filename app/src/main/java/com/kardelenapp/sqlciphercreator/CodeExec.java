package com.kardelenapp.sqlciphercreator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;


import java.io.File;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by mustafa on 2/23/2018.
 */

public class CodeExec extends AppCompatActivity {

    EditText editText_code;

    private Button button_execute;
    private Button button_file;

    private DBHelper mydb ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_exec);
        button_execute = (Button) findViewById(R.id.button_execute);
        button_file = (Button) findViewById(R.id.button_file);
        editText_code = (EditText) findViewById(R.id.editText_code);

        Paper.init(getApplicationContext());

        LinearLayout layout = (LinearLayout) findViewById(R.id.adsContainer2);
        AdsController adsController = new AdsController(this);
        adsController.loadBanner(layout);

         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         getSupportActionBar().setDisplayShowHomeEnabled(true);

        button_execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dbname = Paper.book().read("dbname");
                String password = Paper.book().read("password");

                mydb = new DBHelper(getApplicationContext());

                SQLiteDatabase db = mydb.ConnectDB(dbname,password);


                try{
                    db.execSQL(editText_code.getText().toString());
                    Toast.makeText(getApplicationContext(), "Başarılı...", Toast.LENGTH_LONG).show();
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                finally {
                    db.close();
                }





            }
        });


        button_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dbname = Paper.book().read("dbname");
                String password = Paper.book().read("password");

                mydb = new DBHelper(getApplicationContext());

                SQLiteDatabase db = mydb.ConnectDB(dbname,password);



                try{

                    Toast.makeText(getApplicationContext(), "Successful...", Toast.LENGTH_LONG).show();
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                finally {
                    db.close();
                }


                showFileChooser();


            }
        });

    }



    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                   //String path = getRealPathFromURI(getApplicationContext(),uri);
                    Log.d("DATA 4", "File Uri: " + uri);

                    TextListReader tlr = new TextListReader(getApplicationContext());
                    tlr.readFromUri(uri);
                    List<String> list = tlr.getList();





                    String dbname = Paper.book().read("dbname");
                    String password = Paper.book().read("password");

                    mydb = new DBHelper(getApplicationContext());

                    SQLiteDatabase db = mydb.ConnectDB(dbname,password);

                    try{
                        for (String listitem : list){
                            db.execSQL(listitem);
                        }

                        Toast.makeText(getApplicationContext(), "Successful...", Toast.LENGTH_LONG).show();
                    }
                    catch(Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    finally {
                        db.close();
                    }








                }
                else if (resultCode == 100) {
                    String Fpath = data.getDataString();
                    //TODO handle your request here
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

        //geri düğmesi
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

