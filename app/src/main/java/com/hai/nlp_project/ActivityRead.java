package com.hai.nlp_project;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ActivityRead extends AppCompatActivity {


    //region [varibale]
    TextView textView, errorTextView;
    static String fileName;
    SyllableTree st;
    String string_text;
    String fileContent;
    int errorCount = 0; //tong so loi
    static final String ALPHABET = "123456789:-+*&%=@`~^$!?(),.“”\"\\/<>{}[]|;";
    ProgressBar spinner;
    Button processBtn, speechBtn, nextBtn, prevBtn;
//    static PDDocument document = null;
    int currentPage;
    String result = "";
    String message;
    String s;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_read);

        Intent intent = getIntent();
        message = intent.getStringExtra("filename");

        textView= (TextView)findViewById(R.id.content);
        errorTextView= (TextView)findViewById(R.id.error);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        //noi dung tu dien
        String dictContent = readFromFile(getApplicationContext(), "VNsyl.txt");
        st = new SyllableTree(dictContent);

        processBtn = (Button)findViewById(R.id.processBtn);
        speechBtn = (Button)findViewById(R.id.speechBtn);
        currentPage = 1;

        //endregion

        //region [speech and process button]
        processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        result = "Từ sai: ";
                        File totalFile = new File("/mnt/sdcard/Download/" + message);
                        Automata au = new Automata();
                        au.loadAutomata();
                        for(File f : totalFile.listFiles()){
                            String ss = readText(f);
                            NGramModel ngm  =new NGramModel(f.getPath());
                            ArrayList<String> al = ngm.checkSentence(ss, au);
                            for(String s : al){
                                result += s + " ";
                            }
                            errorTextView.post(new Runnable() {
                                @Override
                                public void run() {
                                    spinner.setVisibility(View.GONE);
                                    errorTextView.setText(result);
                                }
                            });
                        }
                    }
                }).start();

            }
        });

        speechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(fileContent);
                startSpeechtotext();
            }
        });
        //endregion

        processBtn.setActivated(false);
        speechBtn.setActivated(false);

        //region [thread load content of PDF file]
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                File totalFile = new File("/mnt/sdcard/Download/" + message);
                for(File f : totalFile.listFiles()){
                    if(f.getName().endsWith(".txt")){
                        s = readText(f);
                    }
                }
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.GONE);
                        processBtn.setActivated(true);
                        speechBtn.setActivated(true);
                        textView.setText(s);
                    }
                });
            }
        });
        t.start();
        //endregion
    }

    public static String readText(File file){
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
        }
        return new String(text);
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
        int n = ALPHABET.length(), i;
        for(i= 0; i<n; i++){
            if(c == ALPHABET.charAt(i)){
                return true;
            }
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

    //region [speech to text and highlight function]
    public void startSpeechtotext(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "vi-VN");
        try {
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //h
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                string_text = result.get(0);
                if(string_text!=null){
                    Toast.makeText(getApplicationContext(),
                            "Bạn đã tìm kiếm"+string_text,
                            Toast.LENGTH_SHORT).show();
                    highlight(string_text);
                }
                else Toast.makeText(getApplicationContext(),
                        "Không tìm thấy kết quả nào ở trang này",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    //huy
    public void highlight(String ts){
        String tvt =textView.getText().toString();
        int ofe = tvt.indexOf(ts,0);
        Spannable WordtoSpan = new SpannableString(textView.getText());
        int offset=tvt.indexOf(ts);
        int line = textView.getLayout().getLineForOffset(offset-2);
        line = textView.getLayout().getLineTop(line);
        textView.scrollTo(0,line);
        for(int ofs=0;ofs<tvt.length() && ofe!=-1;ofs=ofe+1)
        {
            ofe = tvt.indexOf(ts,ofs);
            if(ofe == -1)
                break;
            else
            {
                WordtoSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), ofe, ofe+ts.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
            }
        }
    }
    //endregion

}
