package Aplicacion.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Utility.Colors;

public class ExportToExcel{
    String sheetName;
    Map<String, Object[]> SET_DATA_EXCEL;
    boolean wasCreated;
    XSSFWorkbook workbook;
    XSSFSheet sheet;
    File outPutFile;
    public ExportToExcel(File outPutFile){
        this.outPutFile = outPutFile;
    }

    Colors colors = new Colors();

    public void createWorkBook(){
       // ? create a blank woorkbook
        workbook = new XSSFWorkbook();
    }
    public void createSheet(String sheetName){
        // ? Create a sheet and name it
        sheet = workbook.createSheet(sheetName!=null ? sheetName : "Sheet excel"); // Pestaña
    }
    public void CreateExcel(){
        try{
        System.out.println(colors.console_amarillo + "Intentando crear archivo excel..." + colors.console_reset);

        // ? Create a row object
        XSSFRow row;
        // ? Data needs to be written (Object[])
        Map<String, Object[]> excelData;
        if(SET_DATA_EXCEL != null){
            excelData = SET_DATA_EXCEL;
        }else{
            excelData = new TreeMap<String,Object[]>();
            excelData = setSampleData(excelData);
        }

        Set<String> keyid = excelData.keySet();
        int rowid = 0;
        // * write data into the sheets
        for (String key : keyid) {
            row = sheet.createRow(rowid++);
            Object[] objectArr = excelData.get(key);
            int cellid = 0;

            for(Object obj : objectArr){
                XSSFCell cell = row.createCell(cellid++);
                cell.setCellValue((String)obj);
            }

        }
        // .xlsx is the format for excel sheets
        //writting the workbook into the file
        FileOutputStream out = new FileOutputStream(outPutFile);
        workbook.write(out);
        out.close();
        wasCreated = true;

    }catch(Exception e){
        e.printStackTrace();
        wasCreated = false;
    }
        
    }

    @Deprecated
    public void setSheet(String sheet){
        this.sheetName = sheet;
    }
    public boolean wasCreated(){
        return wasCreated;
    }

    private Map<String, Object[]> setSampleData(Map<String, Object[]> excelData) {
        
            
        excelData.put("1", new Object[]{"Roll No", "NAME", "Year"});
        excelData.put("2", new Object[]{"123", "Adrian Munoz", "1996"});
        excelData.put("3", new Object[]{"456", "Juan Perez", "1997"});
        excelData.put("4", new Object[]{"789", "Maria Rodriguez", "1998"});
        excelData.put("5", new Object[]{"369", "Molinares Perez", "2002"});
        return excelData;
    }

    public void setData(Map<String, Object[]> excelData){
        SET_DATA_EXCEL = excelData;
    }


    
}
