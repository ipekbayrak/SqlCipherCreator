package com.kardelenapp.sqlciphercreator;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mustafa on 2/21/2018.
 */

public class TextListReader {

    BufferedReader reader;
    InputStream file;
    String line;
    List<String> lines =  new ArrayList<String>();

    Context context;
    public TextListReader(Context context){
        this.context = context;


    }

    public void getFromAssets(String filename){
        try{
            file = context.getAssets().open(filename);
            reader = new BufferedReader(new InputStreamReader(file));


            line = reader.readLine();
            while(line != null){
                lines.add(line) ;
                line = reader.readLine();
            }

        } catch(IOException ioe){
            ioe.printStackTrace();
        }
    }


    public String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {

            lines.add(line) ;
        }
        reader.close();
        return sb.toString();
    }

    public void readFromUri(Uri uri){
        try{
            file = context.getContentResolver().openInputStream(uri);
            reader = new BufferedReader(new InputStreamReader(file));


            line = reader.readLine();
            while(line != null){
                lines.add(line) ;
                line = reader.readLine();
            }

        } catch(IOException ioe){
            ioe.printStackTrace();
        }


    }

    public  List<String> getList(){
        return lines;
    }
}
