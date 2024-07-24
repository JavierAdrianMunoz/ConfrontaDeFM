package Aplicacion.zip;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import Aplicacion.zip.unzipper.UnzipTask;
import Utility.Colors;
import net.miginfocom.swing.MigLayout;

public class MultipleFilesZipJson extends JFrame{
    Colors colors = new Colors();
    ImageIcon icon = new ImageIcon(".\\images\\file-zip.png");
    ImageIcon iconWait = new ImageIcon(".\\images\\wait-time.png");
    JPanel mainContainer;
    JTextArea textArea;
    public MultipleFilesZipJson(JPanel mainContainer){
        this.mainContainer = mainContainer;
        JPanel subContainer = new JPanel();
        subContainer.setLayout(new BorderLayout());
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        JButton addButton = new JButton("Seleccionar carpeta...");
        addButton.setIcon(icon);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        //textarea always follow last line

        // make textArea scrollable
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setPreferredSize(new Dimension(300,100));

        ZipUnzip utilZipUnzip = new ZipUnzip();
        File output = new File(".\\TEMP");
        List<File> filesToUncompress = new ArrayList<>();
        if (!output.exists()) {
            output.mkdirs();
        }

        JPanel secondaryLayout = new JPanel();
        addButton.addActionListener(e -> {
            textArea.setText("");
            progressBar.setValue(0);
            
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                for (File file : selectedFiles) {
                    try {
                        if(fileIsZip(file) || file.isDirectory()) {
                            textArea.append(file.getAbsolutePath() + "\n");
                            if(fileIsZip(file)){
                                filesToUncompress.add(file);
                            }else if(file.isDirectory()) {

                                addFilesFromDirectory(file, filesToUncompress);
                            }

                        }else{
                            JOptionPane.showMessageDialog(mainContainer, "" + file.getName() + " no es un archivo .zip","Error", JOptionPane.ERROR_MESSAGE);
                            textArea.setText("");
                            selectedFiles = null;
                            break;
                        }
/*                         BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        reader.close(); */
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        addButton.setBackground(colors.warningColor);
        addButton.setForeground(Color.white);
        addButton.setFont(new Font("Poppins",Font.BOLD,13));
        JButton btnProcesar = new JButton("Procesar");
        btnProcesar.setIcon(iconWait);



        btnProcesar.addActionListener(e -> {

            if(filesToUncompress.isEmpty()){
                JOptionPane.showMessageDialog(mainContainer, "No hay archivos .zip seleccionados","Error", JOptionPane.ERROR_MESSAGE);
                return;
            }else{
                UnzipTask zipTask = new UnzipTask(filesToUncompress, output, progressBar,textArea, addButton, btnProcesar);
                // ! utilZipUnzip.unzipFiles(filesToUncompress, output, progressBar);
                ParallelUnzipper unzipper = new ParallelUnzipper(filesToUncompress);
/*                 try {
                    unzipper.unzipParallel();
                } catch (InterruptedException | ExecutionException e1) {
                    e1.printStackTrace();
                } */
                zipTask.execute();
            }

        });
        //btnProcesar.setBackground(colors.accentColor);
        btnProcesar.setForeground(Color.BLACK);
        
        JPanel headerSubContainer = new JPanel();
        JPanel bodyContainer = new JPanel();

        headerSubContainer.setLayout(new GridLayout(1, 2, 10, 10));
        headerSubContainer.setBorder(new EmptyBorder(10,0,10,0));
        bodyContainer.setLayout(new BorderLayout());
        //mainContainer.add(secondaryLayout, "cell 0 5,grow");

        subContainer.add(headerSubContainer, BorderLayout.NORTH);
            headerSubContainer.add(addButton, BorderLayout.SOUTH);
            headerSubContainer.add(btnProcesar, BorderLayout.NORTH);

        bodyContainer.add(subContainer, BorderLayout.NORTH);
        bodyContainer.add(scrollPane, BorderLayout.CENTER);// TEXTAREA
        bodyContainer.add(progressBar, BorderLayout.SOUTH);
        mainContainer.add(bodyContainer, BorderLayout.CENTER);
    }

    private List<File> getAllZipFiles(File file) {
        List<File> zipFiles = new ArrayList<>();
        for(File zipFile : file.listFiles()){
            if(zipFile.getName().endsWith(".zip")){
                zipFiles.add(zipFile);
            }
        }
        return zipFiles;
    }

    private List<File> getAllDirectories(File file) {
        List<File> directories = new ArrayList<>();
        for(File directory : file.listFiles()){
            if(directory.isDirectory()){
                directories.add(directory);
            }
        }
        return directories;
    }

    private static boolean fileIsZip(File file) {
        boolean isZip = false;
        if(file.getName().endsWith(".zip")){
            isZip = true;
        }
        return isZip;
    }
    private void addFilesFromDirectory(File directory, List<File> fileListToUnCompress) {
        Colors colors = new Colors();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if(fileIsZip(file)){
                        System.out.println("fileListToUnCompress: "+colors.console_verde+file.getName() + colors.console_reset);
                        PRINT_LOG(textArea, "Archivos por descomprimir: "+ file.getAbsolutePath().replace("/./", ""));
                        fileListToUnCompress.add(file);

                    }else{
                        System.out.println("Files rejected: "+ colors.console_rojo + file.getName() + colors.console_reset);

                    }
                } else if (file.isDirectory()) {
                    addFilesFromDirectory(file, fileListToUnCompress); // Recursión para subdirectorios
                }
            }
        }
    }

    static StringBuilder logContent = new StringBuilder();
    static Queue<String> logLines = new LinkedList<>();
    // Método para agregar mensajes al log y actualizar el JTextArea
    public static void PRINT_LOG(JTextArea textArea_, String message) {
        //System.out.println("PRINT_LOG: " + message); // Imprime el mensaje en la consola
    
        logLines.add(message); // Agrega el mensaje a la cola
    
        if (logLines.size() > 200) {
            logLines.poll(); // Elimina las líneas antiguas
        }
    
        logContent.setLength(0); // reinicia el StringBuilder
        for (String line : logLines) {
            logContent.append(line).append("\n");
        }
    
        textArea_.setText(logContent.toString());
        
        textArea_.setCaretPosition(textArea_.getDocument().getLength()); // Mover el caret al final
    }
        
    
}
