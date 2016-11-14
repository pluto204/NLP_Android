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

public class ActivityRead extends AppCompatActivity {

    TextView textView;
    static String fileName;

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
        textView.setText(readAll());
    }

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
}