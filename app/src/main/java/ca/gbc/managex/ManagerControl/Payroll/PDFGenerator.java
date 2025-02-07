package ca.gbc.managex.ManagerControl.Payroll;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PDFGenerator {

    private Context context;

    public PDFGenerator(Context context) {
        this.context = context;
    }

    public void generatePayrollPDF(String employeeName, double grossPay, double totalHours,
                                   double netPay, double bonus, double statHol, double taxRate, String timePeriod,double payRate) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((android.app.Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        Document document = new Document();
        try {
            String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            File file = new File(directoryPath, "Payroll_" + employeeName + ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Title
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Paragraph title = new Paragraph("Payroll Summary", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Date
            String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
            Paragraph date = new Paragraph("Generated on: " + currentDate);
            Paragraph paragraphTimePeriod = new Paragraph("Time period: "+ timePeriod);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);

            document.add(new Paragraph("\n"));
            document.add(paragraphTimePeriod);
            document.add(new Paragraph("\n"));

            // Employee Info Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addTableHeader(table, "Employee Name", employeeName);
            addTableHeader(table, "Total Hours Worked", String.format("%.2f", totalHours));
            addTableHeader(table,"Pay Rate (hourly): ",String.format("%.2f",payRate));
            addTableHeader(table, "Gross Pay", "$" + String.format("%.2f", grossPay));
            addTableHeader(table, "Bonus", "$" + String.format("%.2f", bonus));
            addTableHeader(table, "Statutory Holiday Pay", "$" + String.format("%.2f", statHol));
            addTableHeader(table, "Tax Rate", taxRate + "%");
            addTableHeader(table, "Net Pay", "$" + String.format("%.2f", netPay));

            document.add(table);

            document.close();

            Toast.makeText(context, "PDF Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("PDFGenerator", "Error creating PDF", e);
            Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTableHeader(PdfPTable table, String key, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key));
        cell1.setPadding(5);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(value));
        cell2.setPadding(5);
        table.addCell(cell2);
    }
}
