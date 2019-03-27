package com.jasperexample.demo.controller;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PdfGenarateController {

    @Autowired
    private DataSource dataSource;

    @GetMapping(path = "/print")
    public void genaratePdf(HttpServletResponse response) throws SQLException {

        Connection conn = dataSource.getConnection();

        try {

            ServletOutputStream servletOutputStream = response.getOutputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis;
            BufferedInputStream bufferedInputStream;
            try {
                fis = new FileInputStream("D:/My/demoJasper/src/main/resources/templates/bill.jasper");
//                For Linux file path
//                fis = new FileInputStream("/data/app/episkey/back-end/jasper/bill.jasper");
                bufferedInputStream = new BufferedInputStream(fis);
                Map parameterMap = new HashMap<>();
                parameterMap.put("userName", "Chathura");
                parameterMap.put("InvoiceId", "18");

                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bufferedInputStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, conn);

                JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline");//use inline for open browser
                response.setContentLength(baos.size());
                baos.writeTo(servletOutputStream);

                fis.close();
                bufferedInputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                servletOutputStream.flush();
                servletOutputStream.close();
                baos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
