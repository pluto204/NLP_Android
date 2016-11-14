package com.hai.nlp_project;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ActivityRead extends AppCompatActivity {

    TextView textView;
    static String fileName;
    static final String ALPHABET = "!?(),.";
    static Scanner sc;

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
        String s = readAll();
        sc = new Scanner(s);
        textView.setText(s);
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

    //Hàm này sử dụng cho xử lý tìm lỗi sai, trả về numOfWord từ tiếp theo của các từ đã đọc.
    public static String[] readPart(int numOfWord) {
        String[] result = new String[numOfWord];
        for (int i = 0; i < numOfWord; i++) {
            if(sc.hasNext()){
                result[i] = repairWord(sc.next());
            }
        }
        sc.close();
        return result;
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
}
