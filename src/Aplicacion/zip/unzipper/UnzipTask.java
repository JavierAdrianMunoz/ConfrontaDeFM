package Aplicacion.zip.unzipper;
import javax.swing.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.data.general.DefaultPieDataset;

import Aplicacion.JsonProcessor;
import Aplicacion.Chart.ChartTools;
import Aplicacion.Chart.PieChart;
import Aplicacion.excel.ExportToExcel;
import Aplicacion.excel.UtilsExcel;
import Utility.Colors;
import Utility.ObjetoFrentes;
import Utility.ObjetoManzanas;

public class UnzipTask extends SwingWorker<Boolean, Integer> {
    private List<File> files;
    private File destination;
    private JProgressBar progressBar;
    private JTextArea textArea;
    private static final int MAX_LINES = 700; // Máximo número de líneas permitidas en el JTextArea
    private JButton addButton, btnProcesar;
    Colors colors = new Colors();
    static StringBuilder logContent = new StringBuilder();
    static Queue<String> logLines = new LinkedList<>();
    UtilsExcel utilsExcel = new UtilsExcel();
    JsonProcessor jsonProcessor;
    
    Map<String, List<File>> folderMap = new HashMap<>(); // Mapa para agrupar archivos por carpeta base
    List<List<File>> indexedFiles = new ArrayList<>(); // Lista multidimensional para almacenar los archivos importantes

    Double ManzanaGeometriaError = 0.0, ManzanaNoTieneFrente = 0.0, FrenteGeometriaError = 0.0, FrenteSinError = 0.0, ManzanaSinError = 0.0;
    Double LocalidadesGeometriaError = 0.0, LocalidadesSinError = 0.0;
    int MANZANAS = 0, FRENTES = 1, LOCALIDADES = 2;
    public UnzipTask(List<File> files, File destination, JProgressBar progressBar,JTextArea textArea, JButton addButton, JButton btnProcesar) {
        this.files = files;
        this.destination = destination;
        this.progressBar = progressBar;
        this.textArea = textArea;
        this.addButton = addButton;
        this.btnProcesar = btnProcesar;
    }

    @Override
    protected Boolean doInBackground() {
        boolean result = false;
        byte[] buffer = new byte[1024];
        double current = 0.0;
        double total = 0.0;
        File parentFolder = new File(".\\OUTPUT\\" + "LOG\\" +LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MMMM/dd/HHmm_ss")));
        try {
            // deshabitamos los botones
            enableButtons(result);
            String FinalDestination = getDestinationParentFile(destination.getAbsolutePath(), files.get(0).getAbsolutePath());
            System.out.println("FinalDestination: " + FinalDestination);
            // primera fase
           
            //List<File> parentFiles = new Unzipper().UnzipParentFiles(files, destination,progressBar, buffer, current, total, textArea);// ? multiples hilos
            List<File> parentFiles = UnzipParentFiles(files, destination, progressBar, buffer, current, total,textArea);
            // Segunda fase
            if(parentFilesHasZipFiles(parentFiles)) {
            boolean wasUnzip = UnzipChildFiles(parentFiles, progressBar, buffer, current, total,textArea);
            }
                //tercera fase
                saveJsonFiles(parentFiles);
            
            result = true;
            // Procesamos la lista de archivos importantes
            processImportantFiles(indexedFiles, destination,parentFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean parentFilesHasZipFiles(List<File> parentFiles) {
        return parentFiles.stream().anyMatch(file -> file.getName().endsWith(".zip"));
    }

    private String getDestinationFile(String destinationPath, String absolutePath) {
        System.out.println(colors.console_purpura+"-------------------getDestinationFile----------------" + colors.console_reset);
        System.out.println("| destinationPath: "+colors.console_verde+destinationPath +colors.console_reset+ " | absolutePath: "+colors.console_amarillo+absolutePath +colors.console_reset);
        String absoluteDestination = destinationPath;       
        String directoryName = absolutePath.substring(absolutePath.lastIndexOf(File.separator) -2);
        directoryName = directoryName.replace(".zip", "");
        //absoluteDestination += File.separator + directoryName;
        System.out.println("| directoryName: "+colors.console_verde+directoryName + colors.console_reset + "| absoluteDestination: "+ colors.console_amarillo +absoluteDestination + colors.console_reset);
        System.out.println(colors.console_purpura+"-------------------getDestinationFile----------------" + colors.console_reset);
        return absoluteDestination;
    }
    private String getDestinationParentFile(String OutputFile, String fullPathFile) {
        //System.out.println("OutputFile: "+OutputFile + " | fullPathFile: "+fullPathFile);
        File fullPathFile_File = new File(fullPathFile);
        //obtener el nombre del archivo fullPathFile sin ".zip"
        String nameFile = fullPathFile.substring(fullPathFile.lastIndexOf(File.separator) + 1).replace(".zip","");
        String relativePath = getRelativePath(fullPathFile_File.getParent());
        // agregar como directorio el primero 2 caracteres del nombre del archivo
        String absoluteDestination = OutputFile + File.separator + relativePath  + File.separator + nameFile;
        // ejemplo de absoluteDestination: C:\Users\JAVIER.MOLINA\eclipse-workspace\ConfrontaDeFM\output\20\20340507104009894
        //System.out.println("absoluteDestination: "+absoluteDestination);
        
        return absoluteDestination;
    }
    private String getRelativePath(String fullPath) {
        Path path = Paths.get(fullPath);
        StringBuilder relativePath = new StringBuilder();

        Pattern pattern = Pattern.compile("^\\d{2}$"); // Patrón para encontrar una carpeta con dos dígitos
        boolean folderFound = false;
        for (Path part : path) {
            if (folderFound) {
                if (relativePath.length() > 0) {
                    relativePath.append(File.separator);
                }
                relativePath.append(part.toString());
            } else if (pattern.matcher(part.toString()).matches()) {
                folderFound = true;
                relativePath.append(part.toString());
            }
        }

        return relativePath.toString();
    }
    

    @Override
    protected void process(List<Integer> chunks) {
        for (int progress : chunks) {
            progressBar.setValue(progress);
        }
    }

    @Override
    protected void done() {
        try {
            boolean result = get();
            if (result) {
                JOptionPane.showMessageDialog(null, "Descompresión completada.");
                enableButtons(result);
                files.clear();
            } else {
                JOptionPane.showMessageDialog(null, "Hubo un error durante la descompresión.");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<File> UnzipParentFiles(List<File> files, File destination, JProgressBar progressBar, byte[] buffer, double current, double total, JTextArea textArea) throws Exception {
        List<File> childFiles = new ArrayList<>();
        for (File file : files) {
            if (fileIsZip(file)) {
                System.out.println(colors.console_amarillo + "Root ZIP: " + file.getName() + colors.console_reset);
                try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(file))) {
                    ZipEntry entry;
                    while ((entry = zipIn.getNextEntry()) != null) {
                        String filePath = getDestinationParentFile(destination.getPath(), file.getAbsolutePath()) + File.separator + entry.getName();
                        System.out.println(colors.console_purpura + "\t\\" + entry.getName() + colors.console_reset);

                        //PRINT_LOG(textArea,"\t\\" +"Carpeta: [" + entry.getName()+ "]");
                        

                        File newFile = new File(filePath);
                        if (entry.isDirectory()) {
                            newFile.mkdirs();
                        } else {
                            if(!entry.getName().endsWith(".zip")) {
                                PRINT_LOG(textArea, "\t\t/" + newFile.getPath() );
                                System.out.println(colors.console_verde + "\t\t/" + newFile.getPath() + colors.console_reset);
                            }
                            // Asegúrate de que el directorio padre exista
                            newFile.getParentFile().mkdirs();
                            try (FileOutputStream fileOut = new FileOutputStream(newFile)) {
                                int bytesRead;
                                while ((bytesRead = zipIn.read(buffer)) != -1) {
                                    fileOut.write(buffer, 0, bytesRead);
                                    current += bytesRead;
                                    int progress = (int) (current * 100 / total);
                                    publish(progress); // Publica el progreso para actualizar la barra
                                }
                            }
                            String message = "Descomprimiendo: " + filePath.replace("/./", filePath);
                            PRINT_LOG(textArea, message);
                            childFiles.add(newFile); // Agrega el archivo al listado
                        }
                        zipIn.closeEntry();
                    }
                }
            } else {
                // Copiar archivo no .zip a la carpeta de destino
                System.out.println(colors.console_amarillo + "File is not zip: " + file.getName() + colors.console_reset);
                File destFile = new File(destination, file.getName());
                copyFile(file, destFile, buffer);
                String message = "Copiando archivo no ZIP: " + destFile.getAbsolutePath();
                PRINT_LOG(textArea, message);
                childFiles.add(destFile);
            }
        }
        return childFiles;
    } 
    private boolean fileIsZip(File file) {
        return file.getName().endsWith(".zip");
    }
    private void copyFile(File source, File dest, byte[] buffer) throws IOException {
        System.out.println("Copying file: " + colors.console_verde + source + " to: " + dest + colors.console_reset);
        try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(dest)) {
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
    private boolean UnzipChildFiles(List<File> files, JProgressBar progressBar, byte[] buffer, double current, double total, JTextArea textArea) throws Exception {
        System.out.println("ChildFiles to unzip: " + files+" | destination: "+destination);

        for (int i = 0; i < files.size(); i++) {
            String fileDestinationWithoutZipEnd = files.get(i).getAbsolutePath().replace(".zip", "");
            File destinationByFile = new File(fileDestinationWithoutZipEnd);

            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(files.get(i)));
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = getDestinationFile(destinationByFile.toString(), files.get(i).getAbsolutePath()) + File.separator + entry.getName();
                //System.out.println("filePath: "+filePath);
                File newFile = new File(filePath);
                newFile.getParentFile().mkdirs();
                if (!entry.isDirectory()) {
                    String message = "Descomprimiendo: " +getDestinationFile(destinationByFile.getPath(), files.get(i).getAbsolutePath()) +"/" + newFile.getName();
                    //System.out.println(message);
                    FileOutputStream fileOut = new FileOutputStream(newFile);
                    int bytesRead;
                    while ((bytesRead = zipIn.read(buffer)) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                        current += bytesRead;
                        total = files.size() * (entry.getSize() + 1024);
                        int progress = (int) (current * 100 / total);
                        
                        publish(progress); // Publica el progreso para actualizar la barra
                    }
                    PRINT_LOG(textArea, message);
                    fileOut.close();
                    // Agregar a la lista si es uno de los archivos importantes
/*                     if (newFile.getName().equals("mza.json") || newFile.getName().equals("locPunt.json") || newFile.getName().equals("frentes.json")) {
                        importantFiles.add(newFile);
                    } */

                // Extraer la carpeta base para agrupar los archivos
                            String folderBase = newFile.getParentFile().getAbsolutePath();
                            folderMap.putIfAbsent(folderBase, new ArrayList<>(Arrays.asList(null, null, null)));
                            
                            // Agregar el archivo a la posición correspondiente en la lista
                            if (newFile.getName().equals("mza.json")) {
                                folderMap.get(folderBase).set(0, newFile);
                            } else if (newFile.getName().equals("frentes.json")) {
                                folderMap.get(folderBase).set(1, newFile);
                            } else if (newFile.getName().equals("locPunt.json")) {
                                folderMap.get(folderBase).set(2, newFile);
                            }
                            if (newFile.getName().endsWith(".json")) {
                                System.out.println("Guardando archivo: " + colors.console_azul + newFile.getName() + colors.console_reset);
                            }
                } else {
                    newFile.mkdirs();
                }
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        }
        boolean wasDeleted = deleteJustZipTempFiles(files);
        indexedFiles.addAll(folderMap.values());
        return true;
    }

    private boolean deleteJustZipTempFiles(List<File> filesToDelete) {

        boolean wasDeleted = false;
        for(File file : filesToDelete){
            if(fileIsZip(file)){
                wasDeleted = file.delete();
            }

        }
        return wasDeleted;
    }
    // Método para agregar mensajes al log y actualizar el JTextArea
    public static void PRINT_LOG(JTextArea textArea_, String message) {
    //System.out.println("PRINT_LOG: " + message); // Imprime el mensaje en la consola

    logLines.add(message); // Agrega el mensaje a la cola

    if (logLines.size() > MAX_LINES) {
        logLines.poll(); // Elimina las líneas antiguas
    }

    logContent.setLength(0); // reinicia el StringBuilder
    for (String line : logLines) {
        logContent.append(line).append("\n");
    }

    textArea_.setText(logContent.toString());
    
    textArea_.setCaretPosition(textArea_.getDocument().getLength()); // Mover el caret al final
}
    
private void enableButtons(boolean enabled) {
        if(enabled){
            addButton.setBackground(colors.warningColor);
            btnProcesar.setBackground(colors.accentColor);
            btnProcesar.setEnabled(true);
            addButton.setEnabled(true);
        }else{
            addButton.setBackground(colors.base_100);
            btnProcesar.setBackground(colors.base_100);
            btnProcesar.setEnabled(false);
            addButton.setEnabled(false);

        }
    }

    // * PROCESAR ARCHIVOS IMPORTANTES
    private void processImportantFiles(List<List<File>> importantFiles, File outputFile,File parentFolder) {
        // open terminal output
        
        ObjetoFrentes objetoFrentes = new ObjetoFrentes();
        ObjetoManzanas objetoManzanas = new ObjetoManzanas();
    
        // Exportar importantFiles a un TXT
        System.out.println("-------------------processImportantFiles-------------------"); 
        System.out.println(colors.console_amarillo+importantFiles + colors.console_reset); 
        System.out.println("-------------------processImportantFiles-------------------"); 
    
        for (int i = 0; i < importantFiles.size(); i++) {
            List<File> filesList = importantFiles.get(i);
            
            
            System.out.println("Índice: " + colors.console_amarillo + i + colors.console_reset);
            String pathFrente = null;
            String pathManzana = null;
            String pathLocalidades = null;
            ExportToExcel excel = new ExportToExcel(new File(parentFolder,"LOG.xlsx"));
            excel.createWorkBook();

            int just_once = 0;
            for (int j = 0; j < filesList.size(); j++) {
                File file = filesList.get(j);
                if (file != null) {
                    if (file.getName().equals("frentes.json")) {
                        pathFrente = file.getAbsolutePath();
                    } else if (file.getName().equals("mza.json")) {
                        pathManzana = file.getAbsolutePath();
                    }else if(file.getName().equals("locPunt.json")){
                        pathLocalidades = file.getAbsolutePath();
                        if (pathLocalidades!=null && jsonProcessor!=null) {
                            System.out.println("PathLocalidades: "+colors.console_amarillo+pathLocalidades+colors.console_reset);
                            CompareFilesJsonLocalidades(new JsonProcessor(pathLocalidades), pathLocalidades,excel, parentFolder);
                        }
                    }
                    if (pathFrente != null && pathManzana != null && just_once == 0) {
                        jsonProcessor = new JsonProcessor(pathFrente, pathManzana,objetoManzanas, objetoFrentes);
                        just_once++;
                        System.out.println("Frente: " + colors.console_verde + pathFrente+ colors.console_reset + "\nManzana: " + colors.console_cyan + pathManzana);
                        CompareFilesJson(jsonProcessor, pathManzana, pathFrente,excel,parentFolder);
                    }
                } else {
                    System.out.println(colors.console_rojo + "null" + colors.console_reset);
                }
            }// Cierre del for j

            // Llamar a JsonProcessor si los paths no son null

        }// Cierre del for i
        // Procesar los archivos con JsonProcessor
        // JsonProcessor jsonProcessor = new JsonProcessor(importantFiles, objetoManzanas, objetoFrentes);

        //imprimir chart con resultados generales:
            PrintChart("Manzanas", "Manzanas", ManzanaNoTieneFrente, ManzanaGeometriaError, ManzanaSinError,FrenteGeometriaError, FrenteSinError);
            PrintChart("Frentes", "Frentes",FrenteGeometriaError, FrenteSinError);
            PrintChart("Localidades", "Localidades", LocalidadesGeometriaError, LocalidadesSinError);
            String message = "---------------------------------- GENERAL ----------------------------------\n"+
            "Manzanas sin frentes: " + colors.console_rojo + ManzanaNoTieneFrente + colors.console_reset + " | Manzanas con error de geometría: " + colors.console_rojo + ManzanaGeometriaError + colors.console_reset + " | Manzanas sin error: " + colors.console_verde+ ManzanaSinError + colors.console_reset + "\n"+
            "Frentes sin error: " + colors.console_verde + FrenteSinError + colors.console_reset + " | Frentes con error de geometría: " + colors.console_rojo + FrenteGeometriaError + colors.console_reset+"\n"+
            "Localidades sin error: " + colors.console_verde + LocalidadesSinError + colors.console_reset + " | Localidades con error de geometría: " + colors.console_rojo + LocalidadesGeometriaError + colors.console_reset + "\n" +
            "---------------------------------- ------- ----------------------------------";
            String message_no_styles= "---------------------------------- GENERAL ----------------------------------\n"+
            "Manzanas sin frentes: " + ManzanaNoTieneFrente + " | Manzanas con error de geometría: " + ManzanaGeometriaError+ " | Manzanas sin error: " + ManzanaSinError  + "\n"+
            "Frentes sin error: "+ FrenteSinError + " | Frentes con error de geometría: " + FrenteGeometriaError+"\n"+
            "Localidades sin error: "  + LocalidadesSinError + " | Localidades con error de geometría: "+ LocalidadesGeometriaError + "\n" +
            "---------------------------------- ------- ----------------------------------";
            System.out.println(message);
            PRINT_LOG(textArea,message_no_styles);
    }

    private void CompareFilesJson(JsonProcessor jsonProcessorData, String pathManzana, String pathFrente, ExportToExcel excel, File parentFolder) {

        List<List<String>> ManzanasCG = jsonProcessorData.getCGgeoManzana();
        List<List<String>> FrenteCG = jsonProcessorData.getCGgeoFrente();

        List<String> ManzanasListCvegeo = ManzanasCG.get(0);
        List<String> ManzanasListGeometry = ManzanasCG.get(1);

        List<String> FrentesListCvegeo = FrenteCG.get(0);
        List<String> FrentesListGeometry = FrenteCG.get(1);
        String[][] ManzanasData = new String[ManzanasListCvegeo.size()][2];
        String[][] FrentesData = new String[FrentesListCvegeo.size()][2];
        String[][] ManzanaNoTieneFrentes = new String[ManzanasListCvegeo.size()][2];

        // Llenar los datos de Manzanas
        for (int i = 0; i < ManzanasListCvegeo.size(); i++) {
            ManzanasData[i][0] = ManzanasListCvegeo.get(i);
            ManzanasData[i][1] = ManzanasListGeometry.get(i);
        }
        for(int i = 0; i< FrentesListCvegeo.size(); i++){
            FrentesData[i][0] = FrentesListCvegeo.get(i);
            FrentesData[i][1] = FrentesListGeometry.get(i);
        }



        // parent folder dependiendo la fecha por YYYYMMDDHHMMSS

        
        if(!parentFolder.exists()){
            parentFolder.mkdirs();
        }

        processTableData(ManzanasData, ManzanasListCvegeo, FrentesListCvegeo, new File(parentFolder, "Manzanas_sin_frentes.txt"),pathManzana, MANZANAS,excel);
        processTableData(FrentesData,  new File(parentFolder, "Frentes_error_geometría.txt"),pathFrente, FRENTES,excel);


         // ?por falta de librerías no funciona "exportToExcel"
        exportToExcel(ManzanasData, ManzanasListCvegeo, FrentesListCvegeo, new File(parentFolder, "LOG_MFL".concat(parentFolder.getName()).concat(".xlsx")),pathManzana, "Manzanas");
        exportToExcel(FrentesData,null,null,  new File(parentFolder, "LOG_MFL".concat(parentFolder.getName()).concat(".xlsx")),pathFrente, "Frentes");
    }
    
    private void CompareFilesJsonLocalidades(JsonProcessor jsonProcessorData,String pathLocalidades, ExportToExcel excel, File parentFolder){
        System.out.println(colors.console_verde + "CompareFilesJsonLocalidades" + colors.console_reset + "\tpathLocalidades" + "\t" + colors.console_amarillo + pathLocalidades + colors.console_reset);
        List<List<String>> LocalidadesCG = jsonProcessorData.getCGgeoLocalidades();
        List<String> LocalidadesListCvegeo = LocalidadesCG.get(0);
        List<String> LocalidadesListGeometry = LocalidadesCG.get(1);
        String[][] LocalidadesData = new String[LocalidadesListCvegeo.size()][2];
        for(int i = 0; i< LocalidadesListCvegeo.size(); i++){
            LocalidadesData[i][0] = LocalidadesListCvegeo.get(i);
            LocalidadesData[i][1] = LocalidadesListGeometry.get(i);
        }

        processTableData(LocalidadesData, new File(parentFolder,"Localidades_error_geometría.txt"), pathLocalidades, LOCALIDADES, excel);
        exportToExcel(LocalidadesData,null,null,  new File(parentFolder, "LOG_MFL".concat(parentFolder.getName()).concat(".xlsx")),pathLocalidades, "Localidades");
    }
    /**
     * Compares the data from the given arrays and writes the result to a text file.
     *
     * @param  manzanasData        a 2D array containing data for manzanas
     * @param  manzanasListCvegeo   a list of strings representing the cvegeo values for manzanas
     * @param  frentesListCvegeo   a list of strings representing the cvegeo values for frentes
     * @param  frentesData         a 2D array containing data for frentes
     */
    private void CompareDataAndWriteTXT(String[][] manzanasData, List<String> manzanasListCvegeo,
        List<String> frentesListCvegeo, String[][] frentesData) {

        if(manzanasListCvegeo != null)  {
            String[][] ManzanaNoTieneFrentes = new String[manzanasListCvegeo.size()][2];
        }
    }
    public void processTableData(String[][] data, File outputFile, String filePath, int FROM_FILE,ExportToExcel excel) {
        processTableData(data, null, null, outputFile, filePath, FROM_FILE, excel);
    }

    boolean manzanaIsOK = false, frenteIsOK = false, localidadIsOK = false;
    
    public void processTableData(String[][] data, List<String> manzanasListCvegeo, List<String> frentesListCvegeo, File outputFile, String filePath, int FROM_FILE,ExportToExcel excel) {
        Map<String, Object[]> excel_data = new TreeMap<String,Object[]>();
        excel.createSheet(outputFile.getName().replace("txt", "").replace("_", " "));
        excel_data.put("1", new Object[]{"CVEGEO", "GEOMETRY", "ERROR_TYPE"}); // Encabezados de las columnas



        Double Local_ManzanaGeometriaError = 0.0, Local_ManzanaNoTieneFrente = 0.0, Local_FrenteGeometriaError = 0.0, Local_FrenteSinError = 0.0, Local_ManzanaSinError = 0.0, Local_LocalidadeSinError = 0.0, Local_LocalidadeGeometriaError = 0.0;
        String DateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY/MM/dd/HH:MM"));
        String[][] manzanaNoTieneFrentes = null;
        if (manzanasListCvegeo != null) {
            manzanaNoTieneFrentes = new String[manzanasListCvegeo.size()][2];
        }

        int excelRowIndex = 2; // Índice inicial para las filas de datos en el Excel

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) { // true para modo append
            File filePathFile = new File(filePath);
            writer.write("------------------------- " + DateTimeNow + " | " + filePathFile.getCanonicalPath() + " ----------------------------");
            writer.newLine();

            String[] column = {"CVEGEO", "GEOMETRY"};
            for (int row = 0; row < data.length; row++) {
                String cvegeo = data[row][0];
                String geometry = data[row][1];

                String errorType = null;
    

                if (isMultiPolygonOrLineStringOrMultiPoint(geometry)) {
                    // * sin errores
                    if(FROM_FILE == MANZANAS) {
                        Local_ManzanaSinError++;
                    }else{
                        if(FROM_FILE == FRENTES){
                            Local_FrenteSinError++;
                        }
                        if(FROM_FILE == LOCALIDADES){
                            Local_LocalidadeSinError++;
                        }
                    }
                } else {

                    writer.write("ERROR | ROW: " + row + " | GEOMETRY: " + geometry + " | CVEGEO: " + cvegeo);
                    writer.newLine();
                    errorType = "GEOMETRY_ERROR";

                    if (manzanaNoTieneFrentes != null) {
                    
                        manzanaNoTieneFrentes[row][0] = geometry;
                        manzanaNoTieneFrentes[row][1] = cvegeo;
                        Local_ManzanaGeometriaError++;
                    }else{
                        // Aquí solo entrara si solo se necesita verificar la geometria
                        data[row][0] = geometry;
                        data[row][1] = cvegeo;
                        if(FROM_FILE == FRENTES){
                            Local_FrenteGeometriaError++;
                        }
                        if(FROM_FILE == LOCALIDADES){
                            Local_LocalidadeGeometriaError++;

                        }
                    }
                }

                if (FROM_FILE == MANZANAS) {
                    if (!manzanaHasFrente(manzanasListCvegeo, frentesListCvegeo, cvegeo)) {
                        errorType = "NO_FRONT_ERROR";
                        writer.write("ERROR - ROW: " + row + " | GEOMETRY: " + geometry + " | CVEGEO: " + cvegeo);
                        writer.newLine();
                        if (manzanaNoTieneFrentes != null) {
                            manzanaNoTieneFrentes[row][0] = cvegeo;
                            manzanaNoTieneFrentes[row][1] = geometry;
                            Local_ManzanaNoTieneFrente++;
                        }
                    }
                }
                if (errorType != null) {
                    excel_data.put(String.valueOf(excelRowIndex), new Object[]{cvegeo, geometry, errorType});
                    excelRowIndex++;
                }
            }// end for

            excel.setData(excel_data);

            if (FROM_FILE == MANZANAS) {
                System.out.println("Manzanas sin frentes: " + colors.console_rojo + Local_ManzanaNoTieneFrente + colors.console_reset + " | Manzanas con error de geometria: " + colors.console_rojo +Local_ManzanaGeometriaError + colors.console_reset + " | Manzanas sin error: " + colors.console_verde + Local_ManzanaSinError + colors.console_reset);
                ManzanaNoTieneFrente = ManzanaNoTieneFrente + Local_ManzanaNoTieneFrente;
                ManzanaGeometriaError = ManzanaGeometriaError + Local_ManzanaGeometriaError;
                ManzanaSinError = ManzanaSinError + Local_ManzanaSinError;
                if(manzanaIsOK){
                    System.out.println(colors.console_cyan + "Manzana estaba marcada como OK previamente, cambiando su estado a false y preguntar si tiene errores sus json"+ colors.console_reset);
                    manzanaIsOK = false;
                }
                if(Local_ManzanaNoTieneFrente == 0.0 && Local_ManzanaGeometriaError == 0.0 && Local_ManzanaSinError != 0.0) {
                    manzanaIsOK = true;
                }
                
            }
            PRINT_LOG(textArea, "\n");
            System.out.println("---------------------------------- ARCHIVO "+new File(filePath).getName()+" ----------------------------------");
            PRINT_LOG(textArea, "---------------------------------- ARCHIVO "+new File(filePath).getCanonicalPath()+" ----------------------------------");
            if(FROM_FILE == FRENTES) {
                System.out.println("Frentes sin error: " + colors.console_verde + Local_FrenteSinError + colors.console_reset + " | Frentes con error de geometria: " + colors.console_verde +Local_FrenteGeometriaError + colors.console_reset);
                PRINT_LOG(textArea, "Frentes sin error: " + Local_FrenteSinError + " | Frentes con error de geometria: " + Local_FrenteGeometriaError);
                FrenteGeometriaError = FrenteGeometriaError + Local_FrenteGeometriaError;
                FrenteSinError = FrenteSinError + Local_FrenteSinError;

                if(frenteIsOK){
                    System.out.println(colors.console_cyan + "Frente estaba marcada como OK previamente, cambiando su estado a false y preguntar si tiene errores sus json"+ colors.console_reset);
                    
                    frenteIsOK = false;
                } 
                if(Local_FrenteGeometriaError == 0.0 && Local_FrenteSinError != 0.0) {
                    frenteIsOK = true;
                }
            }
            if(FROM_FILE == LOCALIDADES){
                System.out.println("Localidades sin error: " + colors.console_verde + Local_LocalidadeSinError + colors.console_reset+ colors.console_rojo + " | Localidades con error de geometria: " + colors.console_reset + colors.console_verde +Local_LocalidadeGeometriaError + colors.console_reset);
                PRINT_LOG(textArea, "Localidades sin error: "+ Local_LocalidadeSinError + " | Localidades con error de geometria: " + Local_LocalidadeGeometriaError);
                LocalidadesSinError = LocalidadesSinError + Local_LocalidadeSinError;
                LocalidadesGeometriaError = LocalidadesGeometriaError + Local_LocalidadeGeometriaError;
                if(localidadIsOK){
                    System.out.println(colors.console_cyan + "Localidad estaba marcada como OK previamente, cambiando su estado a false y preguntar si tiene errores sus json"+ colors.console_reset);
                    localidadIsOK = false;
                }
                if (Local_LocalidadeGeometriaError == 0.0 && Local_LocalidadeSinError != 0.0) {
                    localidadIsOK = true;
                }
                
            }
            System.out.println("Frente OK: " + colors.console_amarillo +frenteIsOK + colors.console_reset + " | Manzana OK: " + colors.console_amarillo + manzanaIsOK + colors.console_reset + " | Localidad OK: " + colors.console_amarillo + localidadIsOK + colors.console_reset);
            PRINT_LOG(textArea, "Frente OK: " +frenteIsOK + " | Manzana OK: " +  manzanaIsOK + " | Localidad OK: " +  localidadIsOK );
            if(manzanaIsOK && frenteIsOK){
                System.out.println(colors.console_amarillo+"Frente y manzana están OK, procediendo a crear el paquete."+colors.console_reset);
                CrearPaquete(filePath);
            }
            excel.CreateExcel();
            if(excel.wasCreated()){
                System.out.println("Excel creado con éxito!");
            }else{
                System.out.println("Error al crear el excel");
            }
            PRINT_LOG(textArea, "------------------------------------------------------------------------------------");
            System.out.println("------------------------------------------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     
    
     public void processTableDataAndExportExcel(String[][] data, List<String> manzanasListCvegeo, List<String> frentesListCvegeo, File outputFile, String filePath, int FROM_FILE, ExportToExcel excel) {
        Map<String, Object[]> excel_data = new TreeMap<>();
        excel.createSheet(outputFile.getName().replace("txt", "").replace("_", " "));
        excel_data.put("1", new Object[]{"CVEGEO", "GEOMETRY", "ERROR_TYPE"}); // Encabezados de las columnas
    
        Double Local_ManzanaGeometriaError = 0.0, Local_ManzanaNoTieneFrente = 0.0, Local_FrenteGeometriaError = 0.0, Local_FrenteSinError = 0.0, Local_ManzanaSinError = 0.0, Local_LocalidadeSinError = 0.0, Local_LocalidadeGeometriaError = 0.0;
        String DateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY/MM/dd/HH:MM"));
        String[][] manzanaNoTieneFrentes = null;
        if (manzanasListCvegeo != null) {
            manzanaNoTieneFrentes = new String[manzanasListCvegeo.size()][2];
        }
    
        int excelRowIndex = 2; // Índice inicial para las filas de datos en el Excel
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) { // true para modo append
            File filePathFile = new File(filePath);
            writer.write("------------------------- " + DateTimeNow + " | " + filePathFile.getCanonicalPath() + " ----------------------------");
            writer.newLine();
    
            for (int row = 0; row < data.length; row++) {
                String cvegeo = data[row][0];
                String geometry = data[row][1];
                String errorType = null;
    
                if (isMultiPolygonOrLineStringOrMultiPoint(geometry)) {
                    if (FROM_FILE == MANZANAS) {
                        Local_ManzanaSinError++;
                    } else {
                        if (FROM_FILE == FRENTES) {
                            Local_FrenteSinError++;
                        }
                        if (FROM_FILE == LOCALIDADES) {
                            Local_LocalidadeSinError++;
                        }
                    }
                } else {
                    writer.write("ERROR | ROW: " + row + " | GEOMETRY: " + geometry + " | CVEGEO: " + cvegeo);
                    writer.newLine();
                    errorType = "GEOMETRY_ERROR";
    
                    if (manzanaNoTieneFrentes != null) {
                        manzanaNoTieneFrentes[row][0] = geometry;
                        manzanaNoTieneFrentes[row][1] = cvegeo;
                        Local_ManzanaGeometriaError++;
                    } else {
                        data[row][0] = geometry;
                        data[row][1] = cvegeo;
                        if (FROM_FILE == FRENTES) {
                            Local_FrenteGeometriaError++;
                        }
                        if (FROM_FILE == LOCALIDADES) {
                            Local_LocalidadeGeometriaError++;
                        }
                    }
                }
    
                if (FROM_FILE == MANZANAS && !manzanaHasFrente(manzanasListCvegeo, frentesListCvegeo, cvegeo)) {
                    writer.write("ERROR - ROW: " + row + " | GEOMETRY: " + geometry + " | CVEGEO: " + cvegeo);
                    writer.newLine();
                    errorType = "NO_FRONT_ERROR";
                    if (manzanaNoTieneFrentes != null) {
                        manzanaNoTieneFrentes[row][0] = cvegeo;
                        manzanaNoTieneFrentes[row][1] = geometry;
                        Local_ManzanaNoTieneFrente++;
                    }
                }
    
                if (errorType != null) {
                    excel_data.put(String.valueOf(excelRowIndex), new Object[]{cvegeo, geometry, errorType});
                    excelRowIndex++;
                }
            }
    
            excel.setData(excel_data);
    
            excel.CreateExcel();
            if (excel.wasCreated()) {
                System.out.println("Excel creado con éxito!");
            } else {
                System.out.println("Error al crear el excel");
            }
            System.out.println("------------------------------------------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
     private void CrearPaquete(String filePath) {
        // Mover los archivos de TEMP correctos a Output bajo /Output/xx/xxxxxxxxx/mcc/json/[TODOS LOS JSON AQUI]
        System.out.println("Creando paquete en: "+filePath);
        PRINT_LOG(textArea, "Creando paquete en: "+filePath);
        File tempDirFiles = new File(filePath);
        File Outputdirectory = new File(".\\OUTPUT"); 
        
        String[] directories = filePath.split("\\\\");
/*         for(String dir : directories){
            System.out.println("Directories: "+dir);
            System.out.println("directories[directories.length-4]: " + directories[directories.length-4]);
            System.out.println("directories[directories.length-3]: " + directories[directories.length-3]);
        } */
        String Directory = directories[directories.length-4] + "\\" + directories[directories.length-3] +"\\"+ directories[directories.length-2] +"\\" + "mcc";
        System.out.println("Directory: " + Directory);
        File Directories = new File(Outputdirectory + "\\" + Directory);
        tempDirFiles.getParent();
        System.out.println("Moviendo desde: ".concat(tempDirFiles.getParent())+ "Archivos: ");
        System.out.println("Copiar paquete en: ".concat(Directories.toString()));
        PRINT_LOG(textArea, "Moviendo desde: ".concat(tempDirFiles.getParent())+ "Archivos: \n"+"Copiar paquete en: ".concat(Directories.toString()) );
        try {
            File listFilesTemp = new File(tempDirFiles.getParent());
            for(File file : listFilesTemp.listFiles()) {
                System.out.println(file.getName());
            }
            for (File file : Objects.requireNonNull(listFilesTemp.listFiles())) {
                try {
                    // Crear ruta si no existe 

                    if (!Directories.exists()) {
                        Directories.mkdirs();
                    }
                    
                    Path destinationPath = Paths.get(Directories.getAbsolutePath() + "\\" + file.getName());
/*                     int i = 1;
                    while (Files.exists(destinationPath)) {
                        String newFileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "(" + i + ")" + file.getName().substring(file.getName().lastIndexOf('.'));
                        destinationPath = Paths.get(Directories.getAbsolutePath() + "\\" + newFileName);
                        i++;
                    } */
                if(Files.exists(destinationPath)){
                    System.out.println("Archivos ya se encuentran: " + destinationPath);
                    PRINT_LOG(textArea, "Archivos ya se encuentran: " + destinationPath);
                }else{
                    Files.copy(file.toPath(), destinationPath);
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        

    }

    public void exportToExcel(String[][] data, List<String> manzanasListCvegeo, List<String> frentesListCvegeo, File outputFile, String filePath, String sheetN) {
        Workbook workbook = null;
        Sheet sheet;


        try {
            // Si el archivo ya existe, ábrelo
            if (outputFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(outputFile);
                workbook = WorkbookFactory.create(fileInputStream);
                fileInputStream.close();
            } else {
                workbook = new XSSFWorkbook();
            }

            // orange backrgound
            XSSFCellStyle orangeStyle = utilsExcel.orangeStyle(workbook); 
    
            XSSFCellStyle AquaStyle = utilsExcel.aquaStyle(workbook); 
            XSSFCellStyle cveoperStyle = utilsExcel.CvegeoStyle(workbook);

            XSSFCellStyle styleGeometrySuccess = utilsExcel.StyleGeometrySuccess(workbook);
            XSSFCellStyle styleGeometryFail = utilsExcel.StyleGeometryFailed(workbook);
            XSSFCellStyle styleDefault = utilsExcel.StyleDefault(workbook);
            // Crear o obtener la hoja
            String sheetName = sheetN;
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            String[] headers = {"Error","CVEGEO","GEOMETRY","PATH"};


            // crear encabezados para la tabla
            Row rowHeader = sheet.createRow(0);
            for(int i =0;i<headers.length;i++){

                
                Cell cell = rowHeader.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(AquaStyle);
                
            }
            // Añadir línea de separación
            int lastRow = sheet.getLastRowNum();
            /*             
                
                Row separatorRow = sheet.createRow(++lastRow);
                Cell separatorCell = separatorRow.createCell(0);
                separatorCell.setCellValue("-------------------- " + filePath + " -----------------------"); 
            */

            // Procesar y escribir datos en el archivo Excel
            for (int row = 0; row < data.length; row++) {
                String cvegeo = data[row][0];
                String geometry = data[row][1];

                if (isMultiPolygonOrLineStringOrMultiPoint(geometry)) {
                    // No hacemos nada ya que este caso no es un error
                } else {
                    
                    Row errorRow = sheet.createRow(++lastRow);
                    errorRow.createCell(0).setCellValue("ERROR");
                    errorRow.getCell(0).setCellStyle(styleDefault);
                   /*  errorRow.createCell(2).setCellValue("ROW: " + row); */
                    errorRow.createCell(1).setCellValue(cvegeo);
                    errorRow.getCell(1).setCellStyle(cveoperStyle);
                    errorRow.createCell(2).setCellValue(geometry);
                    errorRow.getCell(2).setCellStyle(styleGeometryFail);
                    errorRow.createCell(3).setCellValue(filePath);
                    errorRow.getCell(3).setCellStyle(styleDefault);
                }

                if (manzanasListCvegeo != null && frentesListCvegeo != null) {
                    if (!manzanaHasFrente(manzanasListCvegeo, frentesListCvegeo, cvegeo)) {
                        Row errorRow = sheet.createRow(++lastRow);
                        errorRow.createCell(0).setCellValue("ERROR");
                        errorRow.getCell(0).setCellStyle(styleDefault);
                        /* errorRow.createCell(2).setCellValue("ROW: " + row); */
                        errorRow.createCell(1).setCellValue(cvegeo);
                        errorRow.getCell(1).setCellStyle(cveoperStyle);
                        errorRow.createCell(2).setCellValue(geometry);
                        if (isMultiPolygonOrLineStringOrMultiPoint(geometry)) {
                            errorRow.getCell(2).setCellStyle(styleGeometrySuccess);
                        }else{
                        errorRow.getCell(2).setCellStyle(styleGeometryFail);
                        }
                        errorRow.createCell(3).setCellValue(filePath);
                        errorRow.getCell(3).setCellStyle(styleDefault);
                        
                    }
                }

                
            }// for 
            //auto width all Columns
            for(int i = 0; i<headers.length; i++){
                sheet.autoSizeColumn(i);
            }
            // Crear una tabla
            // Escribir el archivo Excel
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                workbook.write(fileOutputStream);
            }
        } catch (IOException e) {
            System.out.println(colors.console_rojo+"Ocurrio un error al crear el archivo excel"+colors.console_reset);
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            // TODO Auto-generated catch block
            System.out.println(colors.console_rojo+"Ocurrio un error al crear el archivo excel"+colors.console_reset);
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    System.out.println(colors.console_verde+"Excel creado correctamente!"+colors.console_reset);
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


            /**
     * Determines if the given geometry is a MultiPolygon or a MultiLineString.
     *
     * @param  geometry  the geometry to check, must not be null
     * @return           true if the geometry is a MultiPolygon or a MultiLineString, false otherwise
     */
    private static boolean isMultiPolygonOrLineStringOrMultiPoint(String geometry) {
        return geometry.equalsIgnoreCase("MultiPolygon") || geometry.equalsIgnoreCase("MultiLineString") || geometry.equalsIgnoreCase("MultiPoint");
    }
    private static boolean manzanaHasFrente(List<String> ManzanasListCvegeo, List<String> FrentesListCvegeo, String manzana) {
        return FrentesListCvegeo.contains(manzana);
    }

    private void PrintChart(String title, String chartTitle, Double FrenteOrLocalidadGeometriaError, Double FrenteOrLocalidadSinError) {
        PrintChart(title, chartTitle,null,null,null, FrenteOrLocalidadGeometriaError, FrenteOrLocalidadSinError);
    }

    private void PrintChart(String title, String chartTitle,Double ManzanaNoTieneFrente, Double ManzanaGeometriaError, Double ManzanaSinError,Double FrenteOrLocalidadGeometriaError, Double FrenteOrLocalidadSinError) {
       
        DefaultPieDataset dataset = new DefaultPieDataset();
        if(ManzanaNoTieneFrente != null && ManzanaGeometriaError != null && ManzanaSinError != null){
            dataset.setValue("Sin frentes", ManzanaNoTieneFrente);
            dataset.setValue("Error en la geometría", ManzanaGeometriaError);
            //dataset.setValue("Sin frentes y con error en la geometría", ManzanaSinError);
            dataset.setValue("Sin errores", ManzanaSinError);

        }else{
            if(FrenteOrLocalidadGeometriaError != null && FrenteOrLocalidadSinError != null){
                dataset.setValue("Error en la geometría", FrenteOrLocalidadGeometriaError);
                dataset.setValue("Sin errores", FrenteOrLocalidadSinError);
            }
        }   
        

        PieChart pieChart = new PieChart(title,chartTitle,dataset);
        pieChart.pack();
        pieChart.setVisible(true);
    }

    private void saveJsonFiles(List<File> files) {
        System.out.println("Tamaño de archivos: " + files.size());
        
        // Recorrer cada archivo de la lista
        for (File newFile : files) {
            System.out.println("Carpeta: " + newFile.getParentFile().getAbsolutePath() + " archivo: " + newFile.getName());
            
            // Verificar si el archivo es un archivo de JSON
            if (newFile.getName().endsWith(".json")) {
                System.out.println("Guardando archivo: " + colors.console_azul + newFile.getName() + colors.console_reset);
                
                // Extraer la carpeta base para agrupar los archivos
                String folderBase = newFile.getParentFile().getAbsolutePath();
                folderMap.putIfAbsent(folderBase, new ArrayList<>(Arrays.asList(null, null, null)));
                
                // Agregar el archivo a la posición correspondiente en la lista
                if (newFile.getName().equals("mza.json")) {
                    folderMap.get(folderBase).set(0, newFile);
                } else if (newFile.getName().equals("frentes.json")) {
                    folderMap.get(folderBase).set(1, newFile);
                } else if (newFile.getName().equals("locPunt.json")) {
                    folderMap.get(folderBase).set(2, newFile);
                }
            }
        }
    
        // Limpiar la lista indexedFiles antes de agregar nuevos elementos para evitar duplicados
        indexedFiles.clear();
        
        // Filtrar las listas no nulas y agregar a indexedFiles
        indexedFiles.addAll(folderMap.values().stream()
            .filter(list -> list.stream().anyMatch(Objects::nonNull))
            .collect(Collectors.toList()));
    }
    
    
}
