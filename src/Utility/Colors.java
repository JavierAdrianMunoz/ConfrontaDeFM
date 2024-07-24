package Utility;

import java.awt.Color;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
public class Colors {
        // THEME COLORS
        public Color primaryColor = Color.decode("#0064ff");
        public Color secondaryColor = Color.decode("#de0000");
        public Color accentColor = Color.decode("#008cc6");
        
        public Color neutraColor = Color.decode("#050407");
        public Color base_100 = Color.decode("#1e2c28");
    
        public Color infoColor = Color.decode("#3182e4");
        public Color successColor = Color.decode("#008800");
        public Color warningColor = Color.decode("#987f51");
        public Color errorColor = Color.decode("#de4450");
        // MATERIAL
        public Color MaterialYellow = Color.decode("#F1C40F");

        // CONSOLA
        // Secuencias de escape ANSI para los colores
         public String console_reset = "\u001B[0m"; // Resetea el estilo
         public String console_rojo = "\u001B[31m"; // Rojo
         public String console_verde = "\u001B[32m"; // Verde
         public String console_amarillo = "\u001B[33m"; // Amarillo
         public String console_azul = "\u001B[34m"; // Azul
         public String console_purpura = "\u001B[35m"; // Púrpura
         public String console_cyan = "\u001B[36m"; // Cían
         public String console_blanco = "\u001B[37m"; // Blanco
}
