package com.hai.nlp_project;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ActivityRead extends AppCompatActivity {

    TextView textView,errorTextView;
    static String fileName;
    SyllableTree st;

    static final String ALPHABET = "!?(),.";
    String fileContent;
    int errorCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_read);

        Intent intent = getIntent();
        String message = intent.getStringExtra("filename");
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fileName = root.getPath() + "/Download/" + message + ".pdf";
        Toast.makeText(ActivityRead.this, message, Toast.LENGTH_SHORT).show();
        textView= (TextView)findViewById(R.id.content);
        errorTextView= (TextView)findViewById(R.id.error);
        fileContent = readAll();
        textView.setText(fileContent);
        
        String content = readFromFile(getApplicationContext(), "VNsyl.txt");
        st = new SyllableTree(content);

        Button processBtn = (Button)findViewById(R.id.processBtn);
        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Scanner sc = new Scanner(fileContent);
                String result = "Từ sai: ";
                while (sc.hasNext()){
                    String s = sc.next();
                    s = s.toLowerCase();
                    s = repairWord(s);
                    if(!st.search(s)){
                        result += s + " ";
                        errorCount++;
                    }
                }
                result += " Tổng số lỗi: " + errorCount;
                errorTextView.setText(result);
            }
        });
    }

    //doc file pdf
    public static String readAll() {
        String content = "";
        try {
            PDDocument document = null;
            document = PDDocument.load(new File(fileName));
            document.getClass();
            if (!document.isEncrypted()) {
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                PDFTextStripper Tstripper = new PDFTextStripper();
                content = Tstripper.getText(document);
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    static String repairWord(String word){
        word = word.toLowerCase();
        int n = word.length();
        for(int i = n-1; i>=0; i--){
            if(checkChar(word.charAt(i))){
                word = deleteChar(word, i);
            }
        }
        return word;
    }

    static String deleteChar(String word, int index){
        return word.substring(0, index) + word.substring(index+1);
    }

    static boolean checkChar(char c){
        int n = ALPHABET.length();
        for(int i= 0; i<n; i++){
            if(c == ALPHABET.charAt(i)) return true;
        }
        return false;
    }

    String readFromFile(Context context, String file) {
        try {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte buffer[] = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return "" ;
        }
    }

}