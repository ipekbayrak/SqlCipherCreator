package com.kardelenapp.sqlciphercreator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    EditText editText_dbname;
    EditText editText_password;
    private Button button_create;
    Button button_connect;
    Button button_export;
    Button button_remove;
    private DBHelper mydb ;

    String path = "/data/data/com.kardelenapp.sqlciphercreator/databases/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(getApplicationContext());

        isStoragePermissionGranted();

        LinearLayout layout = (LinearLayout) findViewById(R.id.adsContainer);
        AdsController adsController = new AdsController(this);
        adsController.loadBanner(layout);

        button_export = (Button) findViewById(R.id.button_export);
        button_create = (Button) findViewById(R.id.button_create);
        button_connect = (Button) findViewById(R.id.button_connect);
        button_remove = (Button) findViewById(R.id.button_remove);
        editText_dbname = (EditText) findViewById(R.id.editText_dbname);
        editText_password = (EditText) findViewById(R.id.editText_password);



        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    File file = new File(path+editText_dbname.getText().toString()+ ".db");
                    if(file.exists()){
                        Toast.makeText(getApplicationContext(), "Already Exist...", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        mydb = new DBHelper(getApplicationContext());
                        mydb.CreateDB(editText_dbname.getText().toString()+ ".db",editText_password.getText().toString());
                        Toast.makeText(getApplicationContext(), "Creaded Successfuly...", Toast.LENGTH_LONG).show();
                        //check if
                    }

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error...", Toast.LENGTH_LONG).show();
                }


            }
        });

        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    File file = new File(path+editText_dbname.getText().toString()+ ".db");
                    if(file.exists()){

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "DB Not Exist...", Toast.LENGTH_LONG).show();
                       return;
                    }



                    mydb = new DBHelper(getApplicationContext());

                    SQLiteDatabase db = mydb.ConnectDB(editText_dbname.getText().toString() + ".db",editText_password.getText().toString());
                    int i = db.getVersion();
                    if (db.isOpen()){
                        db.close();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error...", Toast.LENGTH_LONG).show();
                    }


                    Intent myIntent = new Intent(getApplicationContext(), CodeExec.class);
                    Paper.book().write("dbname",editText_dbname.getText().toString() + ".db");
                    Paper.book().write("password",editText_password.getText().toString());

                    startActivity(myIntent);}
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Error...", Toast.LENGTH_LONG).show();
                    }





            }
        });

        button_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> lines =  new ArrayList<String>();

                String path = "/data/data/com.kardelenapp.sqlciphercreator/databases/";
                File dir = new File(path);
                File[] files = dir.listFiles();
                for (int i = 0; i< files.length;i++){
                    lines.add(files[i].getName().toString());
                }

                showList(lines,"export");



            }
        });

        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> lines =  new ArrayList<String>();

                String path = "/data/data/com.kardelenapp.sqlciphercreator/databases/";
                File dir = new File(path);
                File[] files = dir.listFiles();
                for (int i = 0; i< files.length;i++){
                    lines.add(files[i].getName().toString());
                }

                showList(lines,"remove");



            }
        });

    }

    public  boolean isStoragePermissionGranted() {
        String TAG = "TAH";
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String TAG = "TAH";

        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }


    public void showList(List<String> list, final String mod){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Select a Database");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(list);

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                Log.v("DATA 5",strName);
                if(mod.equals("export")){
                    exportDatabase(strName);
                }
                else if (mod.equals("remove")){
                    removeDatabase(strName);
                }

            }
        });
        builderSingle.show();
    }

    public void exportDatabase(String name){


        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "SqlCipherExports");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        else{
            success = true;
        }

        if (success) {
            String path = "/data/data/com.kardelenapp.sqlciphercreator/databases/" + name;


            File dest = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "SqlCipherExports/"+name);

            FileInputStream inStream = null;
            FileOutputStream outStream = null;
            try {
                inStream = new FileInputStream(path);
                outStream = new FileOutputStream(dest);
                FileChannel inChannel = inStream.getChannel();
                FileChannel outChannel = outStream.getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                inStream.close();
                outStream.close();
                Toast.makeText(getApplicationContext(), "Success...", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error...", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error...", Toast.LENGTH_LONG).show();
            } finally {
            }



        } else {
            Toast.makeText(getApplicationContext(), "Error...", Toast.LENGTH_LONG).show();
        }

    }


    public void removeDatabase(String name){


            String path = "/data/data/com.kardelenapp.sqlciphercreator/databases/" + name;

        File file = new File(path);
        boolean deleted = file.delete();

        if (deleted){
            Toast.makeText(getApplicationContext(), "Deleted...", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "Error...", Toast.LENGTH_LONG).show();
        }




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {

            case R.id.about:
                Intent myIntent = new Intent(this, Hakkinda.class);
                this.startActivity(myIntent);

                break;
        }
        return true;
    }


}
