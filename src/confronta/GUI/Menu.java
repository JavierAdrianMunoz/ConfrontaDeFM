package confronta.GUI;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import Aplicacion.excel.ExportToExcel;
import Utility.Colors;
import Utility.RoundedBorder;
import confronta.ConfrotaUI;
public class Menu  extends JFrame {
    Colors colors = new Colors();

// ? RESOURCES FOLDERS
private final static File CurrentDirectory = new File(".", "resources");
private final static File DirectoryInsumos = new File(CurrentDirectory,"INSUMOS");
private final static File DirectoryTemp = new File(CurrentDirectory,"TEMP");
private final static File DirectoryOutput = new File(CurrentDirectory,"OUTPUT");
private final static File DirectoryJson = new File(CurrentDirectory,"JSON");

private final static String THEME_DIRECTORY = "./resources/themes/";

JComboBox themeSelector;

static JLabel currentThemeSelected;
    public static void main(String[] args) {
        FlatLightLaf.setup();
        FlatMacDarkLaf.setup();
        try {
            
            UIManager.put( "Button.arc", 15 );
            UIManager.put( "Component.arc", 999 );
            UIManager.put( "ProgressBar.arc", 999 );
            UIManager.put( "TextComponent.arc", 999 );
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentThemeSelected = new JLabel("Theme Selected");

        ExportToExcel exportToExcel = new ExportToExcel(new File("./newFileExcel.xlsx"));
        exportToExcel.createWorkBook();
        CrearExcelEjemplo(exportToExcel,"DATOS CHIDOS");
        CrearExcelEjemplo(exportToExcel,"DATOS CHIDOS2");

        Menu menu = new Menu();
        menu.CreateMenu();
    }

    private static void CrearExcelEjemplo(ExportToExcel exportToExcel,String pestana) {
        

        
        exportToExcel.createSheet(pestana);

        Map<String, Object[]> data = new TreeMap<String,Object[]>(); 
        data.put("A", new Object[]{"A1", "A2", "A3","A4"});
        data.put("B", new Object[]{"B1", "B2", "B3","B4"});
        data.put("C", new Object[]{"C1", "C2", "C3","C4"});
        if(pestana.equals("DATOS CHIDOS2")){
            data.put("D", new Object[]{"D1", "D2", "D3","D4"});
        }
        exportToExcel.setData(data);
        exportToExcel.CreateExcel();
        if(exportToExcel.wasCreated()){
            System.out.println("Excel creado con éxito!");
        }else{
            System.out.println("Error al crear el excel");
        }
    }

    private static void checkIfCoreFoldersExist() {
        Colors colors = new Colors();
        System.out.println(colors.console_cyan + "Checking if core folders exist..." + colors.console_reset);
        if (!DirectoryInsumos.exists()) {
            DirectoryInsumos.mkdirs();
        }
        if (!DirectoryTemp.exists()) {
            DirectoryTemp.mkdirs();
        }
        if (!DirectoryOutput.exists()) {
            DirectoryOutput.mkdirs();
        }
        if (!DirectoryJson.exists()) {
            DirectoryJson.mkdirs();
        }
    }

    public void CreateMenu() {
        
        checkIfCoreFoldersExist();
        Image icon = Toolkit.getDefaultToolkit().getImage("images/console.png");
        this.setIconImage(icon);
        // cambiar la barra de windows color
        this.setTitle("Menú de opciones");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 200);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        ThemeSelector(panel);
        
        add(panel);
        Buttons(panel);
        


        this.setContentPane(panel);
        this.setSize(400,300);
        this.setResizable(false);
    }

    InputStream fis;
    private void ThemeSelector(JPanel panel) {
        
        
        String themes[] = {"Default theme light","Gradianto dark fuchsia", "Gradianto midnight blue", "Gradianto nature green"
        , "Gradianto deep ocean","light everest", "Dracula dark", "Mac dark"};
        
        // create an object
        Menu menu = new Menu();
        // Create checkbox
        themeSelector = new JComboBox(themes);
        // add ItemListener
        themeSelector.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String itemSeleccionado = (String) e.getItem();
                    System.out.println("Seleccionaste: " + itemSeleccionado);
                //currentThemeSelected.setText(itemSeleccionado+" seleccionado");
                
                try {
                    
                    switch (itemSeleccionado) {
                        case "Gradianto dark fuchsia":
                        fis = new FileInputStream(THEME_DIRECTORY.concat("Gradianto/Gradianto_dark_fuchsia_new.theme.json"));
                        refreshTheme(fis);
                            break;
                        case "Gradianto midnight blue":
                        
                            fis = new FileInputStream(THEME_DIRECTORY.concat("Gradianto/Gradianto_midnight_blue.theme.json"));
                            refreshTheme(fis);
                            break;
                            case "Gradianto nature green":
                            fis = new FileInputStream(THEME_DIRECTORY.concat("Gradianto\\Gradianto_Nature_Green.theme.json"));
                            refreshTheme(fis);
                            break;
                            case "Gradianto deep ocean":
                            fis = new FileInputStream(THEME_DIRECTORY.concat("Gradianto\\Gradianto_deep_ocean.theme.json"));
                            refreshTheme(fis);
                            break;
                            case "light everest":
                            fis = new FileInputStream(THEME_DIRECTORY.concat("everest\\light_everest.theme.json"));
                            refreshTheme(fis);
                            break;
                            case "Dracula dark":
                            FlatDarculaLaf.setup();
                            refreshTheme(null);
                            break;
                            case "Mac dark":
                            FlatMacDarkLaf.setup();
                            refreshTheme(null);
                            break;
                        default:
                        FlatMacLightLaf.setup();
                            refreshTheme(null);
                            break;
                    }

            // Refrescar la ventana
            SwingUtilities.updateComponentTreeUI(menu);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                
                }
            }

            private void refreshTheme(InputStream fis) {
                FlatAnimatedLafChange.showSnapshot();
                if(fis != null) {
                IntelliJTheme.setup(fis);
                }
                FlatLaf.updateUI();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
                
                //fis.close();
            }
            
        });
        //themeSelector.addItemListener(menu);
        //Create labels
        
        //set color of text
        //currentThemeSelected.setForeground(colors.infoColor);

        //Create a JPanel
        JPanel themeOptionsPanel = new JPanel();
        //themeOptionsPanel.add(currentThemeSelected);
        try {
            BufferedImage myPicture = ImageIO.read(new File(".\\images\\paint-palette.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            themeOptionsPanel.add(picLabel,BorderLayout.WEST);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
       
        themeOptionsPanel.add(themeSelector,BorderLayout.EAST);
        

        panel.add(themeOptionsPanel, BorderLayout.CENTER);
    }

    private void Buttons(JPanel panel) {
        GridLayout grid = new GridLayout(4, 1);
        panel.setLayout(grid);
        // añadir espaciado entre los botones
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        JButton btnOpenJsonPanel = new JButton("Verificar Manzanas y Frentes");
        btnOpenJsonPanel.addActionListener(e -> {
            ConfrotaUI cf = new ConfrotaUI(btnOpenJsonPanel,themeSelector);
            cf.setVisible(true);
            // Comprobar si cf esta abierto:
            if (cf.isVisible()) {
                btnOpenJsonPanel.setEnabled(false);
                themeSelector.setEnabled(false);
            }
        });
        
    
        JButton btnOpenJavascriptReader = new JButton("Integraciones");

        // redondear los bordes de los botones
        //btnOpenJsonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //btnOpenJsonPanel.setBackground(colors.infoColor);
                btnOpenJsonPanel.setForeground(Color.BLACK);
        btnOpenJsonPanel.setPreferredSize(new Dimension(200, 30));
        btnOpenJsonPanel.setIcon(new ImageIcon(".\\images\\file-json.png"));
        btnOpenJsonPanel.setFont(new Font("Poppins",Font.BOLD, 15));
        //btnOpenJsonPanel.setBorder(new RoundedBorder(15));
        panel.add(btnOpenJsonPanel, BorderLayout.SOUTH);
        // ? redondear los bordes del boton btnOpenJavascriptReader
        //btnOpenJavascriptReader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //btnOpenJavascriptReader.setBackground(colors.infoColor);
        btnOpenJavascriptReader.setForeground(Color.BLACK);
        btnOpenJavascriptReader.setPreferredSize(new Dimension(200, 30));
        btnOpenJavascriptReader.setFont(new Font("Poppins",Font.BOLD, 15));
        btnOpenJavascriptReader.setIcon(new ImageIcon(".\\images\\database-fill.png"));
        //btnOpenJavascriptReader.setBorder(new RoundedBorder(15));
        //cambiar color al hacer clic en el boton
        btnOpenJavascriptReader.addActionListener(e -> {
            // Run a java app in a separate system process
            Process proc;
            try {
                proc = Runtime.getRuntime().exec("java -jar integrador.jar");
                // Then retreive the process output
                InputStream in = proc.getInputStream();
                InputStream err = proc.getErrorStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });
        panel.add(btnOpenJavascriptReader,BorderLayout.NORTH);
    }

}
