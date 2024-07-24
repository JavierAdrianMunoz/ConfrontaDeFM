package Aplicacion.zip;

import java.io.File;
import java.util.List;
import java.util.zip.*;

import javax.swing.JProgressBar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
public class ZipUnzip {

    public boolean unZipFile(File fileZip, File destination) {
        boolean result = false;
        byte[] buffer = new byte[1024];
        try {
            FileInputStream fis = new FileInputStream(fileZip);
            ZipInputStream in = new ZipInputStream(fis);
            ZipEntry entry = in.getNextEntry();
            while (entry != null) {
                File f = new File(destination, entry.getName());
                if (!entry.isDirectory()) {
                    f.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(f);
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                } else {
                    f.mkdirs();
                }
                entry = in.getNextEntry();
            }
            in.closeEntry();
            in.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    

    public boolean unzipFiles(List<File> files, File destination,JProgressBar progressBar){
        boolean result = false;
        byte[] buffer = new byte[1024];
        double current = 0.0;
        double total = 0.0;
        try {
            for (int i = 0; i < files.size(); i++) {
                ZipInputStream zipIn = new ZipInputStream(new FileInputStream(files.get(i)));
                ZipEntry entry = zipIn.getNextEntry();
                while (entry != null) {
                    String filePath = destination.getPath() + File.separator + entry.getName();
                    File newFile = new File(filePath);
                    newFile.getParentFile().mkdirs();
                    if (!entry.isDirectory()) {
                        FileOutputStream fileOut = new FileOutputStream(newFile);
                        int bytesRead;
                        while ((bytesRead = zipIn.read(buffer)) != -1) {
                            fileOut.write(buffer, 0, bytesRead);
                            current += bytesRead;
                            total = files.size() * (entry.getSize() + 1024);
                            int progress = (int) (current * 100 / total);
                            progressBar.setValue(progress);
                            progressBar.repaint();
                        }
                        fileOut.close();
                    } else {
                        newFile.mkdirs();
                    }
                    entry = zipIn.getNextEntry();
                }
                zipIn.close();
            }
            
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
/* 
 public boolean unzipFiles(List<File> files, File destination, JProgressBar progressBar) {
    boolean result = false;
    byte[] buffer = new byte[1024];
    double current = 0.0;
    double total = 0.0;
    try {
        for (int i = 0; i < files.size(); i++) {
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(files.get(i)));
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = destination.getPath() + File.separator + entry.getName();
                File newFile = new File(filePath);
                newFile.getParentFile().mkdirs();
                if (!entry.isDirectory()) {
                    FileOutputStream fileOut = new FileOutputStream(newFile);
                    int bytesRead;
                    while ((bytesRead = zipIn.read(buffer)) != -1) {
                        fileOut.write(buffer, 0, bytesRead);
                        current += bytesRead;
                        total = files.size() * (entry.getSize() + 1024);
                        int progress = (int) (current * 100 / total);
                        progressBar.setValue(progress);
                        progressBar.repaint(); // Actualiza la barra de progreso en tiempo real
                    }
                    fileOut.close();
                } else {
                    newFile.mkdirs();
                }
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
        }
        
        result = true;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return result;
}
 
 
 */
    
}
