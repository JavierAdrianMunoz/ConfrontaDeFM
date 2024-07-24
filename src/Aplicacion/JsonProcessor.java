package Aplicacion;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Utility.ObjetoFrentes;
import Utility.ObjetoManzanas;


public class JsonProcessor extends JPanel {
    String PathFrente;
    String PathManzana;
    String PathLocalidad;
    ObjetoManzanas objManzana;
    ObjetoFrentes objFrentes;

    public JsonProcessor(String PathFrente, String PathManzana, ObjetoManzanas objManzana, ObjetoFrentes objFrentes) {
        this.PathFrente = PathFrente;
        this.PathManzana = PathManzana;
        this.objFrentes = objFrentes;
        this.objManzana = objManzana;
        
    }
    public JsonProcessor(String PathLocalidad) {
        this.PathLocalidad = PathLocalidad;
    }
	private void SETUP() {
        ReadPaths();
        try{
            Object jsonManzana = ReadJsonFromFile(PathManzana);
            Object jsonFrente = ReadJsonFromFile(PathFrente);
            if (jsonManzana instanceof JSONObject) {
                System.out.println(((JSONObject) jsonManzana).toString(2)); // Imprime el JSONObject con una indentación de 2 espacios
            } else if (jsonManzana instanceof JSONArray) {
                System.out.println(((JSONArray) jsonManzana).toString(2)); // Imprime el JSONArray con una indentación de 2 espacios
            }
            if (jsonFrente instanceof JSONObject) {
                System.out.println(((JSONObject) jsonFrente).toString(2)); // Imprime el JSONObject con una indentación de 2 espacios
            } else if (jsonFrente instanceof JSONArray) {
                System.out.println(((JSONArray) jsonFrente).toString(2)); // Imprime el JSONArray con una indentación de 2 espacios
            }
            printCVEGEO(PathManzana, getFileName(PathManzana));
			printCVEGEO(PathFrente , getFileName(PathFrente));
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	
	public List<String> getCvgeoFrente() {
		return printCVEGEO(PathFrente , getFileName(PathFrente));
	}
	public List<String> getCvgeoManzana() {
		return printCVEGEO(PathManzana, getFileName(PathManzana));
	}
	
	public List<List<String>> getCGgeoFrente(){
		return getCVEGEOandGeometry(PathFrente , getFileName(PathFrente));
	}
	
	public List<List<String>> getCGgeoManzana(){
		return getCVEGEOandGeometry(PathManzana , getFileName(PathFrente));
	}

    public List<List<String>> getCGgeoLocalidades(){

        return getCVEGEOandGeometry(PathLocalidad , getFileName(PathLocalidad));
    }
	
    private void ReadPaths() {
        System.out.println("Frente: " + PathFrente);
        System.out.println("Manzana: " + PathManzana);
    }
public Object readJsonFromFile(String filePath) throws JSONException, IOException {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        }
        String content = jsonContent.toString();
        //System.out.println(content);
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            return new JSONArray(content);
        }
    }

public List<String> printCVEGEO(String filePath, String FROM) {
    try {
        Object json = readJsonFromFile(filePath);
        List<String> ListCvegeo = new ArrayList<>();
        List<String> ListGeometry = new ArrayList<>();
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            JSONArray features = jsonObject.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                ListCvegeo.add(properties.getString("CVEGEO"));
               
                
            }
            return ListCvegeo;
        } else {
            System.out.println("El archivo no contiene un JSONObject en la raíz.");
            return null;
        }
    } catch (IOException | JSONException e) {
        e.printStackTrace();
        return null;
    }
}
public List<List<String>> getCVEGEOandGeometry(String filePath, String FROM) {
    try {
        Object json = readJsonFromFile(filePath);
        List<String> listCvegeo = new ArrayList<>();
        List<String> listGeometry = new ArrayList<>();
        if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            JSONArray features = jsonObject.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                JSONObject geometry = feature.getJSONObject("geometry");
                listCvegeo.add(properties.getString("CVEGEO"));
                listGeometry.add(geometry.getString("type")); // Añadir el tipo de geometría al listado
            }
            // Devolver ambas listas dentro de una lista
            List<List<String>> result = new ArrayList<>();
            result.add(listCvegeo);
            result.add(listGeometry);
            return result;
        } else {
            System.out.println("El archivo no contiene un JSONObject en la raíz.");
            return null;
        }
    } catch (IOException | JSONException e) {
        e.printStackTrace();
        return null;
    }
}
    public Object ReadJsonFromFile(String filePath) throws IOException {
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        }
        String content = jsonContent.toString().trim();
        //System.out.println("Contenido del archivo JSON:");
        //System.out.println(content);

        try {
            if (content.startsWith("{")) {
                return new JSONObject(content);
            } else if (content.startsWith("[")) {
                return new JSONArray(content);
            } else {
                throw new JSONException("El contenido del archivo no es un JSON válido.");
            }
        } catch (JSONException e) {
            throw new IOException("Error al analizar el archivo JSON: " + e.getMessage(), e);
        }
    }

    public String getFileName(String filePath) {
        try {
            int lastIndex = filePath.lastIndexOf('\\');
            if (lastIndex != -1) {
                return filePath.substring(lastIndex + 1);
            } else {
                return filePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}



    private void ReadJsonManzana() {
        System.out.println("Manzana: " + PathManzana);
    }
}
