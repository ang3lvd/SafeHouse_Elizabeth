package com.divinesecurity.safehouse.pdfPackage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by ang3l on 1/3/2018.
 */

public class TemplatePDF {
    private Context context;
    private File pdfFile;
    private Document document;
    private Paragraph paragraph;
    private Font fTitle    = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private Font fText     = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.RED);

    private Font fHeadText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.WHITE);

    public TemplatePDF(Context context) {
        this.context = context;
    }

    public void openDocument(){
        createFile();
        try {
            document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

        } catch (Exception e){
            //Log.e("openDocument", e.toString());
        }
    }

    private  void createFile(){
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");

        if (!folder.exists())
            folder.mkdirs();
        pdfFile = new File(folder, "TemplatePDF.pdf");
    }

    public  void closeDocument(){
        document.close();
    }

    public void addMetaData(String title, String subject, String author){
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String subject){
        try{
            paragraph = new Paragraph();
            addChild(new Paragraph(title, fTitle));
            addChild(new Paragraph(subject, fSubTitle));
            //addChild(new Paragraph("Generado: "+date, fHighText));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        } catch (Exception e){
            //Log.e("addTitles", e.toString());
        }
    }

    private  void addChild(Paragraph childParagrah){
        childParagrah.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagrah);
    }

    public void addParagrah(String text){
        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(20);
            document.add(paragraph);
        } catch (Exception e){
            //Log.e("addParagrah", e.toString());
        }
    }


    public void createTableDates(String dated, String ldate, String invNo) {
        try {
            paragraph = new Paragraph();
            PdfPCell cell;
            // Main table
            PdfPTable mainTable = new PdfPTable(3);
            mainTable.setWidthPercentage(50.0f);
            mainTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            // First table
            PdfPCell firstTableCell = new PdfPCell();
            firstTableCell.setBorder(PdfPCell.NO_BORDER);
            PdfPTable firstTable = new PdfPTable(1);
            firstTable.setWidthPercentage(100.0f);
            cell = new PdfPCell(new Phrase("Date", fHeadText));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            firstTable.addCell(cell);
            cell = new PdfPCell(new Phrase(dated));
            firstTable.addCell(cell);
            firstTableCell.addElement(firstTable);
            mainTable.addCell(firstTableCell);

            // Second table
            PdfPCell secondTableCell = new PdfPCell();
            secondTableCell.setBorder(PdfPCell.NO_BORDER);
            PdfPTable secondTable = new PdfPTable(1);
            secondTable.setWidthPercentage(100.0f);
            cell = new PdfPCell(new Phrase("Due Date", fHighText));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            secondTable.addCell(cell);
            cell = new PdfPCell(new Phrase(dated));
            secondTable.addCell(cell);
            secondTableCell.addElement(secondTable);
            mainTable.addCell(secondTableCell);

            // Third table
            PdfPCell thirdTableCell = new PdfPCell();
            thirdTableCell.setBorder(PdfPCell.NO_BORDER);
            PdfPTable thirdTable = new PdfPTable(1);
            thirdTable.setWidthPercentage(100.0f);
            cell = new PdfPCell(new Phrase("Invoice #", fHeadText));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            thirdTable.addCell(cell);
            cell = new PdfPCell(new Phrase(invNo));
            thirdTable.addCell(cell);
            thirdTableCell.addElement(thirdTable);
            mainTable.addCell(thirdTableCell);

            paragraph.add(mainTable);
            document.add(paragraph);
        } catch (Exception e){
            //Log.e("createTableDates", e.toString());
        }
    }

    public void createTableClientDet(String[] userDes){
        try {

            paragraph = new Paragraph();
            paragraph.setFont(fText);

            PdfPTable pdfTable = new PdfPTable(1);
            pdfTable.setWidthPercentage(40);
            pdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell pdfPCell;
            //int indexC = 0;

            pdfPCell = new PdfPCell(new Phrase("Bill To:", fHeadText));
            //pdfPCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfPCell.setBackgroundColor(BaseColor.DARK_GRAY);
            pdfTable.addCell(pdfPCell);


            for (String userDe : userDes) {
                pdfPCell.addElement(new Phrase(userDe));
                pdfPCell.setBackgroundColor(BaseColor.WHITE);

            }
            pdfTable.addCell(pdfPCell);

            paragraph.add(pdfTable);
            document.add(paragraph);
        } catch (Exception e){
            //Log.e("createTableClientDet", e.toString());
        }
    }

    public void createTableBill(String[] header, ArrayList<String[]> clients){
        try {

            paragraph = new Paragraph();
            paragraph.setFont(fText);

            PdfPTable pdfTable = new PdfPTable(header.length);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{2,1,5,1,1});
            pdfTable.setSpacingBefore(20);

            PdfPCell pdfPCell;
            int indexC = 0;

            while (indexC<header.length){
                pdfPCell = new PdfPCell(new Phrase(header[indexC++], fSubTitle));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBackgroundColor(BaseColor.GREEN);
                pdfTable.addCell(pdfPCell);
            }

            for (int indexR=0; indexR<clients.size(); indexR++){
                String[] row = clients.get(indexR);
                for (indexC=0; indexC<header.length; indexC++){
                    pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setFixedHeight(40);
                    pdfTable.addCell(pdfPCell);
                }
            }

            paragraph.add(pdfTable);
            document.add(paragraph);
        } catch (DocumentException e) {
            //Log.e("createTable", e.toString());
        }
    }

    public void createTableTotal(String total) {
        try {
            paragraph = new Paragraph();
            PdfPCell cell;
            // Main table
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(22.0f);
            mainTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            mainTable.setSpacingBefore(10);

            // First table
            PdfPCell firstTableCell = new PdfPCell();
            firstTableCell.setBorder(PdfPCell.NO_BORDER);
            PdfPTable firstTable = new PdfPTable(1);
            firstTable.setWidthPercentage(100.0f);
            cell = new PdfPCell(new Phrase("Total", fSubTitle));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(PdfPCell.NO_BORDER);
            firstTable.addCell(cell);
            firstTableCell.addElement(firstTable);
            mainTable.addCell(firstTableCell);

            // Second table
            PdfPCell secondTableCell = new PdfPCell();
            secondTableCell.setBorder(PdfPCell.NO_BORDER);
            PdfPTable secondTable = new PdfPTable(1);
            secondTable.setWidthPercentage(100.0f);
            cell = new PdfPCell(new Phrase(total, fSubTitle));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(PdfPCell.NO_BORDER);
            secondTable.addCell(cell);
            secondTableCell.addElement(secondTable);
            mainTable.addCell(secondTableCell);

            paragraph.add(mainTable);
            document.add(paragraph);
        } catch (Exception e){
            //Log.e("createTableDates", e.toString());
        }
    }


    /*public void viewPDF(){
        Intent intent = new Intent(context, ViewPDFActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }*/

    public void appViewPDF(Activity activity){
        if (pdfFile.exists()){
            Uri uri = Uri.fromFile(pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            try{
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e){
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), "You do not have an application to view PDF", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), "The file was not found", Toast.LENGTH_LONG).show();
        }
    }
}
