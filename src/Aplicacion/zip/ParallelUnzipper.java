package Aplicacion.zip;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

public class ParallelUnzipper {
    private static final int THREAD_COUNT = 4; // Número de hilos que quieres usar
    List<File> zipFiles;

    public ParallelUnzipper(List<File> zipFiles) {
        this.zipFiles = zipFiles;
    }

    public void unzipParallel() throws InterruptedException, ExecutionException {
        // List<File> zipFiles; //= List.of(new File("zip1.zip"), new File("zip2.zip")); ///* otros archivos zip */);
        
        // Divide los archivos en subgrupos
        List<List<File>> partitions = partitionList(zipFiles, THREAD_COUNT);

        // Crear un ExecutorService
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<Void>> futures = new ArrayList<>();

        for (List<File> partition : partitions) {
            futures.add(executorService.submit(new UnzipTask(partition)));
        }

        // Esperar a que todas las tareas terminen
        for (Future<Void> future : futures) {
            future.get(); // Puede lanzar ExecutionException o InterruptedException
        }

        // Apagar el ExecutorService
        executorService.shutdown();
    }

    private static List<List<File>> partitionList(List<File> list, int partitions) {
        List<List<File>> result = new ArrayList<>();
        int partitionSize = list.size() / partitions;

        for (int i = 0; i < partitions; i++) {
            int start = i * partitionSize;
            int end = (i == partitions - 1) ? list.size() : (i + 1) * partitionSize;
            System.out.println("start: " + start + " end: " + end);
            result.add(list.subList(start, end));
        }

        return result;
    }

    static class UnzipTask implements Callable<Void> {
        private final List<File> files;

        UnzipTask(List<File> files) {
            this.files = files;
        }

        @Override
        public Void call() throws Exception {

            for (File file : files) {
                String[] directories = file.getPath().split("\\\\");
                String Directory = directories[directories.length-4] + "\\" + directories[directories.length-3] + "\\" + file.getName().replace(".zip", "");
                unzip(file, new File( "./TEMP" + File.separator +Directory));
            }
            return null;
        }
        private File getFileDest(File file) {
            // get file name
            
            String nameFile = file.getAbsolutePath().replace(".zip","");
            String relativePath = file.getParent();
            // agregar como directorio el primero 2 caracteres del nombre del archivo
            String absoluteDestination = "./" + File.separator + relativePath  + File.separator + nameFile;

            return new File(absoluteDestination);
        }
        private void unzip(File zipFile, File destDir) throws IOException {
            System.out.println("zipFiles: " + zipFile.getAbsolutePath() + ", destDir: " + destDir.getAbsolutePath());
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry entry = zipIn.getNextEntry();
                while (entry != null) {
                    File filePath = new File(destDir, entry.getName());
                    if (!entry.isDirectory()) {
                        extractFile(zipIn, filePath);
                    } else {
                        filePath.mkdirs();
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
            }
        }

        private void extractFile(ZipInputStream zipIn, File filePath) throws IOException {
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                byte[] bytesIn = new byte[4096];
                int read;
                while ((read = zipIn.read(bytesIn)) != -1) {
                    bos.write(bytesIn, 0, read);
                }
            }
        }
    }
}
