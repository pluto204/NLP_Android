package com.hai.nlp_project;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    GridView gv;
    public static String [] prgmNameList;
    public static int [] prgmImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        File file = new File(root.getPath() + "/Download/");

        ArrayList<String> nameList = new ArrayList<>();
        for(File f : file.listFiles()){
            String s = f.getPath();
            int l = s.length();

            if(s.substring(l-4, l).equals(".pdf")) {
                nameList.add(s.substring(file.getPath().length()+1, f.getPath().length()-4));
            }
        }

        int size = nameList.size();
        prgmNameList = nameList.toArray(new String[size]);
        prgmImages = new int[size];
        Arrays.fill(prgmImages, R.drawable.book);

        gv=(GridView) findViewById(R.id.gridView1);
        gv.setAdapter(new CustomAdapter(this, prgmNameList,prgmImages));

    }
}
