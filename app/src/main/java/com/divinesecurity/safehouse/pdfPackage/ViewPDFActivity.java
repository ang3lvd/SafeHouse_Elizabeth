package com.divinesecurity.safehouse.pdfPackage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.divinesecurity.safehouse.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ViewPDFActivity extends AppCompatActivity {

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        PDFView pdfView = findViewById(R.id.pdfView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            file = new File(bundle.getString("path", ""));
        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }
}
