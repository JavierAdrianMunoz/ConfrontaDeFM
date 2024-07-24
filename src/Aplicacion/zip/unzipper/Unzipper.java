package Aplicacion.zip.unzipper;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.zip.*;
import javax.swing.*;

import Utility.Colors;

public class Unzipper {
    private static final int THREAD_COUNT = 2; // Número de hilos que quieres usar
    Colors colors = new Colors();

    public List<File> UnzipParentFiles(List<File> files, File destination, JProgressBar progressBar, byte[] buffer, double current, double total, JTextArea textArea) throws Exception {
        List<File> childFiles = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();
        for (File file : files) {
            if (fileIsZip(file)) {
                System.out.println(colors.console_amarillo + "Root ZIP: " + file.getName() + colors.console_reset);
                futures.add(executor.submit(new UnzipTask(file, destination, buffer, childFiles, progressBar, textArea, current, total)));
            } else {
                System.out.println(colors.console_amarillo + "File is not zip: " + file.getName() + colors.console_reset);
                File destFile = new File(destination, file.getName());
                copyFile(file, destFile, buffer);
                String message = "Copiando archivo no ZIP: " + destFile.getAbsolutePath();
                PRINT_LOG(textArea, message);
                childFiles.add(destFile);
            }
        }

        // Esperar a que todas las tareas terminen
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                // Manejo de excepción específico para errores de descompresión
                Throwable cause = e.getCause();
                if (cause instanceof ZipException) {
                    System.err.println("Error descomprimiendo archivo: " + cause.getMessage());
                    PRINT_LOG(textArea, "Error descomprimiendo archivo: " + cause.getMessage());
                } else {
                    throw e;
                }
            }
        }

        executor.shutdown();
        return childFiles;
    }

    private class UnzipTask implements Callable<Void> {
        private final File file;
        private final File destination;
        private final byte[] buffer;
        private final List<File> childFiles;
        private final JProgressBar progressBar;
        private final JTextArea textArea;
        private double current;
        private final double total;

        public UnzipTask(File file, File destination, byte[] buffer, List<File> childFiles, JProgressBar progressBar, JTextArea textArea, double current, double total) {
            this.file = file;
            this.destination = destination;
            this.buffer = buffer;
            this.childFiles = childFiles;
            this.progressBar = progressBar;
            this.textArea = textArea;
            this.current = current;
            this.total = total;
        }

        @Override
        public Void call() throws IOException {
            try {
                unzipFile(file, destination);
            } catch (ZipException e) {
                // Manejo de excepción específico para errores de descompresión
                System.err.println("Error descomprimiendo archivo: " + file.getName() + " - " + e.getMessage());
                PRINT_LOG(textArea, "Error descomprimiendo archivo: " + file.getName() + " - " + e.getMessage());
                throw e;
            }
            return null;
        }

        private void unzipFile(File file, File destination) throws IOException {
            try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(file))) {
                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null) {
                    String filePath = getDestinationParentFile(destination.getPath(), file.getAbsolutePath()) + File.separator + entry.getName();
                    System.out.println(colors.console_purpura + "\t\\" + entry.getName() + colors.console_reset);

                    File newFile = new File(filePath);
                    if (entry.isDirectory()) {
                        newFile.mkdirs();
                    } else {
                        if (!entry.getName().endsWith(".zip")) {
                            System.out.println(colors.console_verde + "\t\t/" + newFile.getPath() + colors.console_reset);
                        }
                        // Asegúrate de que el directorio padre exista
                        newFile.getParentFile().mkdirs();
                        try (FileOutputStream fileOut = new FileOutputStream(newFile)) {
                            int bytesRead;
                            while ((bytesRead = zipIn.read(buffer)) != -1) {
                                fileOut.write(buffer, 0, bytesRead);
                                synchronized (this) {
                                    current += bytesRead;
                                }
                                int progress = (int) (current * 100 / total);
                                //SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                            }
                        }
                        String message = "Descomprimiendo: " + filePath;
                        PRINT_LOG(textArea, message);
                        synchronized (childFiles) {
                            childFiles.add(newFile);
                        }
                    }
                    zipIn.closeEntry();

                    // Si el archivo descomprimido es otro ZIP, descomprimirlo
                    if (newFile.getName().endsWith(".zip")) {
                        unzipFile(newFile, newFile.getParentFile());
                    }
                }
            }
        }
    }

    private boolean fileIsZip(File file) {
        // Implementa la lógica para verificar si un archivo es un ZIP
        return file.getName().endsWith(".zip");
    }

    private void copyFile(File source, File dest, byte[] buffer) throws IOException {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(dest)) {
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void PRINT_LOG(JTextArea textArea, String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
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
    

}


