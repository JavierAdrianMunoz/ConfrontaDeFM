package Aplicacion.excel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class UtilsExcel {

    // * excel style
    public static XSSFCellStyle orangeStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle)workbook.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillPattern(FillPatternType.BIG_SPOTS);
        style.setFont(setFontTitles(workbook));
        return style;
    }
    public static XSSFCellStyle aquaStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle)workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(setFontTitles(workbook));
        return style;
    }
    public static XSSFCellStyle CvegeoStyle(Workbook workbook){
        XSSFCellStyle style = (XSSFCellStyle)workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //border color black
        setBorderThin(style);
        //Border color red
        setBorderBlack(style);

        style.setFont(setFontCveoper(workbook));
        return style;
    }
    public static XSSFCellStyle StyleGeometrySuccess(Workbook workbook){
        XSSFCellStyle style = (XSSFCellStyle)workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorderThin(style);
        setBorderBlack(style);
        style.setFont(setFontForBlackBackground(workbook));
        return style;
    }

    public static XSSFCellStyle StyleGeometryFailed(Workbook workbook){
        XSSFCellStyle style = (XSSFCellStyle)workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorderThin(style);
        setBorderBlack(style);
        style.setFont(setFontForBlackBackground(workbook));
        return style;
    }

    public static XSSFCellStyle StyleDefault(Workbook workbook){
        XSSFCellStyle style = (XSSFCellStyle)workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorderThin(style);
        setBorderBlack(style);
        //style.setFont(setFontForBlackBackground(workbook));
        return style;
    }

    public static void setBorderThin(XSSFCellStyle style){
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
    }
    public static void setBorderRed(XSSFCellStyle style) {
        style.setBottomBorderColor(IndexedColors.RED.getIndex());
        style.setLeftBorderColor(IndexedColors.RED.getIndex());
        style.setRightBorderColor(IndexedColors.RED.getIndex());
        style.setTopBorderColor(IndexedColors.RED.getIndex());
    }
    public static void setBorderGreen(XSSFCellStyle style) {
        style.setBottomBorderColor(IndexedColors.GREEN.getIndex());
        style.setLeftBorderColor(IndexedColors.GREEN.getIndex());
        style.setRightBorderColor(IndexedColors.GREEN.getIndex());
        style.setTopBorderColor(IndexedColors.GREEN.getIndex());
    }
    public static void setBorderBlack(XSSFCellStyle style) {
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    }
    public static Font setFontTitles(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)15); 
        font.setFontName("Aptos Narrow");
        font.setBold(true);
        return font;
    }
    public static Font setFontCveoper(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)11); 
        font.setFontName("Cascadia Code");
        font.setBold(false);
        // set text to white
        font.setColor(IndexedColors.WHITE.getIndex());
        return font;
    }

    public static Font setFontForBlackBackground(Workbook workbook){
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)11); 
        font.setFontName("Aptos Narrow");
        font.setBold(true);
        // set text to white
        font.setColor(IndexedColors.WHITE.getIndex());
        return font;
    }
    
}
