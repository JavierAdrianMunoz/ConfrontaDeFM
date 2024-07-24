package confronta.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class ScriptOnJava {
    
        public void LoadJavaScript() {            
            try{
               
                ScriptEngineManager engineManager = new ScriptEngineManager();
                List<ScriptEngineFactory> factories = new ScriptEngineManager().getEngineFactories();

                ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine("Nashorn");

                for(ScriptEngineFactory factory : factories) {
                    System.out.println(factory.getNames());
                }

                engine.eval(new FileReader("./js/simple.js"));
                Invocable invocable = (Invocable) engine;
                List<Object> result = new ArrayList<Object>();
                for(int i = 0; i < 3; i++) {
                    result.add(invocable.invokeFunction("saludar", "Adrian"));
                }
                System.out.println(result);
                }catch (Exception e){
                    e.printStackTrace();
                }
    }
    private Reader read(String path) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        if (in == null) {
            throw new IOException("Resource not found: " + path);
        }
        return new InputStreamReader(in, StandardCharsets.UTF_8);
    }
}
