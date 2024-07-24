package confronta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.ui.FlatColorChooserUI;
import com.formdev.flatlaf.util.ColorFunctions;

import Aplicacion.JsonProcessor;
import Aplicacion.zip.MultipleFilesZipJson;
import Utility.BorderRadiusJTextField;
import Utility.Colors;
import Utility.ObjetoFrentes;
import Utility.ObjetoManzanas;

import javax.script.*;  
import java.io.*;

import confronta.GUI.Menu;
import confronta.utils.ScriptOnJava;

import javax.naming.Binding;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.LookAndFeel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.UIManager;
import java.awt.Component;
import net.miginfocom.swing.MigLayout;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
import javax.swing.ImageIcon;

public class ConfrotaUI extends JFrame {
    private static final long serialVersionUID = 1L;
    protected static final String[][] ManzanaNoTieneFrentes = null;
    private String frentesFilePath;
    private String manzanaFilePath;
    private String localidadesFilePath;
    ObjetoManzanas objManzana = new ObjetoManzanas();
    ObjetoFrentes objFrentes = new ObjetoFrentes();
    JsonProcessor jsonProcessor;
    JButton loadButton, ButtonLocalidades;
    StyledDocument frentesDoc,manzanaDoc;
    JTextPane manzanaTextPane,frentesTextPane;
    Style styleGreen, styleRed, styleNeutral, styleBlue;
    Image icon;
    File currentFolder = new File(".");
    String[] columns = {"CVEGEO", "GEOMETRY"};
    //Test JTable
    JTable table;
    Colors colors = new Colors();
    JButton btnMenu;
    JComboBox themeSelector;
    public static void main(String[] args) {
    	//readExcel();
                FlatLightLaf.setup();
        FlatMacLightLaf.setup();
        
        //FlatMacDarkLaf.setup();
        try {
            
            UIManager.put( "Button.arc", 15 );
            UIManager.put( "Component.arc", 999 );
            UIManager.put( "ProgressBar.arc", 999 );
            UIManager.put( "TextComponent.arc", 999 );
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        Menu menu = new Menu();
        menu.CreateMenu();
        //new ConfrotaUI();
        // * opcion propuesta para utilizar js en lugar de java, descartado.
        // ? ScriptOnJava scriptOnJava = new ScriptOnJava();
        // ? scriptOnJava.LoadJavaScript();
    }

    public ConfrotaUI(JButton btnMenu, JComboBox themeSelector) {
    	this.btnMenu = btnMenu;
        this.themeSelector=themeSelector;
        cerrarVentana();//detecta si la ventana se cierra
    	getContentPane().setFont(new Font("Microsoft New Tai Lue", Font.PLAIN, 14));
    	setType(Type.POPUP);
        setTitle("Confronta");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setSize(563, 144);
        setResizable(true);
        setMaximumSize(getMaximumSize());
        setMinimumSize(new Dimension(500,400));
        // auto width and height

        icon = Toolkit.getDefaultToolkit().getImage("images/workplace.png");
        setIconImage(icon);
        
        JFrame frame = new JFrame();
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout()); // ayudara a realizar la vista re
        // Crear el JTextPane y el StyledDocument
        manzanaTextPane = new JTextPane();
		frentesTextPane = new JTextPane();
		frentesDoc = frentesTextPane.getStyledDocument();
        manzanaDoc = manzanaTextPane.getStyledDocument();
        

        // Estilos para verde y rojo
        styleNeutral = frentesDoc.addStyle("Neutral", null);
        StyleConstants.setForeground(styleNeutral, colors.neutraColor);
        styleGreen = manzanaTextPane.addStyle("Green", null);
        // custom color for green
        Color customColor = new Color(0, 128, 0); // Verde oscuro
        StyleConstants.setForeground(styleGreen, colors.successColor);
        
        styleRed = manzanaTextPane.addStyle("Red", null);
        StyleConstants.setForeground(styleRed, colors.errorColor);
        
        styleBlue = manzanaTextPane.addStyle("Blue", null);
		StyleConstants.setForeground(styleBlue, colors.infoColor);

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
        //inputPanel.setPreferredSize(new Dimension(150, 50));
      // inputPanel.setMaximumSize(new Dimension(frame.getParent().getWidth(),0));
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10)); // GridLayout para organizar los botones



        JLabel JL_SingleFiles = new JLabel("Cargar archivos JSON de manera individual");
        JL_SingleFiles.setFont(new Font("Microsoft JhengHei UI", Font.PLAIN, 24));
        JL_SingleFiles.setForeground(Color.WHITE);
        JLabel JL_MultipleFiles = new JLabel("Cargar archivos Zip por lotes.");
        JL_MultipleFiles.setFont(new Font("Microsoft JhengHei UI", Font.PLAIN, 24));
        JL_MultipleFiles.setForeground(Color.WHITE);
        JButton frentesButton = new JButton("Seleccionar Frentes JSON");
        frentesButton.setIcon(new ImageIcon(".\\images\\file-json.png"));
        frentesButton.setFont(new Font("Microsoft JhengHei UI", Font.PLAIN, 12));
        JButton manzanaButton = new JButton("Seleccionar Manzana JSON");
        manzanaButton.setIcon(new ImageIcon(".\\images\\file-json.png"));
        manzanaButton.setFont(new Font("Microsoft JhengHei UI", Font.PLAIN, 12));
        JButton ButtonLocalidades = new JButton("Selecciondar Localidades JSON");
        ButtonLocalidades.setIcon(new ImageIcon(".\\images\\file-json.png"));
        ButtonLocalidades.setFont(new Font("Microsoft JhengHei UI", Font.PLAIN, 12));
        loadButton = new JButton("Cargar JSON's");
        loadButton.setIcon(new ImageIcon(".\\images\\upload.png"));
        loadButton.setForeground(Color.BLACK);
        loadButton.setFont(new Font("Microsoft New Tai Lue", Font.PLAIN, 13));

        // get current Theme 
        if(isLightTheme()){
            JL_SingleFiles.setForeground(Color.BLACK);
            JL_MultipleFiles.setForeground(Color.BLACK);
        }
        JButton addMultiplesManzanas = new JButton("Añadir Manzanas");
        addMultiplesManzanas.setFont(new Font("Microsoft JhengHei UI", Font.PLAIN, 12));

        JButton addMultiplesFrentes = new JButton("Añadir Frentes");
        addMultiplesFrentes.setFont(new Font("Microsoft JhengHei UI", Font.PLAIN, 12));

        
        frentesButton.setBackground(colors.MaterialYellow);
        manzanaButton.setBackground(colors.MaterialYellow);
        ButtonLocalidades.setBackground(colors.MaterialYellow);

        frentesButton.setForeground(Color.BLACK);
        manzanaButton.setForeground(Color.BLACK);
        ButtonLocalidades.setForeground(Color.BLACK);


        frentesButton.addActionListener(evt -> {
        	frentesFilePath = openFileAdministrator(frentesButton);
        });

        manzanaButton.addActionListener(evt -> {
        	manzanaFilePath = openFileAdministrator(manzanaButton);
        });
        
        ButtonLocalidades.addActionListener(evt -> {
        	//TableExample();
        	localidadesFilePath = openFileAdministrator(ButtonLocalidades);
        });

        loadButton.addActionListener(e -> {
            if (frentesFilePath != null && manzanaFilePath != null) {
                jsonProcessor = new JsonProcessor(frentesFilePath, manzanaFilePath, objManzana, objFrentes);
                frame.getContentPane().add(jsonProcessor);
                frame.revalidate();
                frentesButton.setBackground(colors.warningColor);
                manzanaButton.setBackground(colors.warningColor);

                FillTableData();
                if(localidadesFilePath != null) {
                    FillTableDataLocalidades(localidadesFilePath);
                    localidadesFilePath = null;
                }
                frentesFilePath = null;
                manzanaFilePath = null;
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona ambos archivos JSON.\n(Manzanas y Frentes)", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        //getContentPane().setLayout(new BorderLayout(0, 0));
        //inputPanel.add(buttonsMultiplesPanel, BorderLayout.SOUTH);


        //mainContainer.setLayout(new MigLayout("", "", ""));
        // ! mainContainer.add(inputPanel, "cell 0 1,grow");
        //pannel.add(inputPanel, BorderLayout.CENTER);
        // ! mainContainer.add(JL_MultipleFiles, "cell 0 2,grow");
        JPanel subContainer = new JPanel();
        subContainer.setLayout(new BorderLayout());

        subContainer.add(JL_SingleFiles,BorderLayout.NORTH); // ? LABEL -> 
            inputPanel.add(frentesButton, BorderLayout.WEST);
            inputPanel.add(manzanaButton, BorderLayout.CENTER);
            inputPanel.add(ButtonLocalidades, BorderLayout.EAST);
            inputPanel.add(loadButton, BorderLayout.EAST);

        subContainer.add(inputPanel, BorderLayout.CENTER);
        subContainer.add(JL_MultipleFiles, BorderLayout.SOUTH);// ? LABEL

        // add padding to mainContainer
        mainContainer.setBorder(new EmptyBorder(10,25,10,25));
        mainContainer.add(subContainer, BorderLayout.NORTH);

        getContentPane().add(mainContainer);
        new MultipleFilesZipJson(mainContainer);
        pack();
        setVisible(true);
        
    }
    
    private String openFileAdministrator(JButton button) {
        JFileChooser chooser = new JFileChooser(currentFolder);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = chooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (accept(selectedFile)) {
            	//currentFilePath = selectedFile.getAbsolutePath();
              	String OK = "Archivo ".concat(selectedFile.getAbsolutePath()).concat(" cargado correctamente");
                //JOptionPane.showMessageDialog(this, OK, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            	button.setBackground(colors.successColor);
            	button.setForeground(Color.BLACK);
            	return selectedFile.getAbsolutePath();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un archivo JSON.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                button.setBackground(colors.errorColor);
                button.setForeground(Color.WHITE);
                return null;
            }
        }
		return null;
	}

	JFrame FrenteFrame, ManzanaFrame;
private void FillTableDataLocalidades(String FilePath) {
    List<List<String>> LocalidadesCG = jsonProcessor.getCVEGEOandGeometry(FilePath, jsonProcessor.getFileName(FilePath));
    List<String> LocalidadesListCvegeo = LocalidadesCG.get(0);
    List<String> LocalidadesListGeometry = LocalidadesCG.get(1);
    
    String[][] LocalidadesData = new String[LocalidadesListCvegeo.size()][2];
    for (int i = 0; i < LocalidadesListCvegeo.size(); i++) {
        LocalidadesData[i][0] = LocalidadesListCvegeo.get(i);
        LocalidadesData[i][1] = LocalidadesListGeometry.get(i);
    }
    TableFrame(LocalidadesData, "Localidades | ".concat(FilePath));
}
    private void FillTableData() {
        List<List<String>> ManzanasCG = jsonProcessor.getCGgeoManzana();
        List<List<String>> FrenteCG = jsonProcessor.getCGgeoFrente();
        
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
		TableFrame(ManzanasData, ManzanasListCvegeo, FrentesListCvegeo, "Manzanas | ".concat(frentesFilePath));
        TableFrame(FrentesData, "Frentes | ".concat(frentesFilePath));
    }

    private void FillTextAreas() {

        List<String> Manzanas = jsonProcessor.getCvgeoManzana();
        List<String> Frentes = jsonProcessor.getCvgeoFrente();
        
        List<List<String>> ManzanasCG = jsonProcessor.getCGgeoManzana();
        List<List<String>> FrenteCG = jsonProcessor.getCGgeoFrente();
        
        List<String> ManzanasListCvegeo = ManzanasCG.get(0);
        List<String> ManzanasListGeometry = ManzanasCG.get(1);

        List<String> FrentesListCvegeo = FrenteCG.get(0);
        List<String> FrentesListGeometry = FrenteCG.get(1);
        
        
        int i=1, j=0;
        for (String frente : FrentesListCvegeo) {
        	try {
        		frentesDoc.insertString(frentesDoc.getLength(), i + ".- " + frente + " - ", styleNeutral);
        		frentesDoc.insertString(frentesDoc.getLength(), FrentesListGeometry.get(j)+ "\n", isMultiPolygonOrLineStringGetStyle(FrentesListGeometry.get(j)));

			} catch (BadLocationException e) {
				
				e.printStackTrace();
			}            
        	//TA_Frentes.append(String.valueOf(i).concat(".- ").concat(frente.concat("\n")));
            i++;
            j++;
        }
        
       
        
        i = 1;
        j=0;
        
        // Comparación de manzanas y frentes
        for (String manzana : ManzanasListCvegeo) {
            boolean encontrado = false;
            for (String frente : FrentesListCvegeo) {
                if (manzana.equals(frente)) {
                    encontrado = true;
                    break;
                }
            }
            try {
                if (encontrado) {
                	manzanaDoc.insertString(manzanaDoc.getLength(), i + ".- " + manzana + " - ", styleGreen);
                	manzanaDoc.insertString(manzanaDoc.getLength(), ManzanasListGeometry.get(j)+ "\n", isMultiPolygonOrLineStringGetStyle(ManzanasListGeometry.get(j)));
                } else {
                	manzanaDoc.insertString(manzanaDoc.getLength(), i + ".- " + manzana + " - ", styleRed);
                	manzanaDoc.insertString(manzanaDoc.getLength(), ManzanasListGeometry.get(j)+ "\n", isMultiPolygonOrLineStringGetStyle(ManzanasListGeometry.get(j)));
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            i++;
            j++;
        }

        printFrame(manzanaTextPane, ManzanaFrame, "Manzanas".concat(" | ").concat(manzanaFilePath));
        printFrame(frentesTextPane, FrenteFrame, "Frentes" .concat(" | ").concat(frentesFilePath));

        
    }
    private void printFrame(JTextPane textPane, JFrame frame, String title) {
	    frame = new JFrame(title);
        // Establecer el icono de la ventana
        Image icon = Toolkit.getDefaultToolkit().getImage("images/workplace.png");
        frame.setIconImage(icon);
	    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    frame.getContentPane().add(new JScrollPane(textPane));
	    frame.setSize(400, 600);
	    frame.setVisible(true);
	    // Centrar el JFrame en la pantalla 
	    frame.setLocationRelativeTo(null);
        // Agregar un WindowListener al JFrame
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                // Limpiar el JTextPane cuando se cierra el JFrame
                textPane.setText("");
            }
        });
       
        
    }
    private boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".json");
    }
    /**
     * Returns a {@link Style} based on the given {@code geometry}.
     * 
     * @param geometry the geometry to check, must not be {@code null}.
     * @return a {@link Style} indicating whether the geometry is a MultiPolygon or a MultiLineString.
     */
    private Style isMultiPolygonOrLineStringGetStyle(final String geometry) {
        if (!geometry.equalsIgnoreCase("MultiPolygon")) {
            if (geometry.equalsIgnoreCase("MultiLineString")) {
                return styleBlue;
            } else {
                return styleRed;
            }
        } else {
            return styleBlue;
        }
    }
        /**
     * Determines if the given geometry is a MultiPolygon or a MultiLineString.
     *
     * @param  geometry  the geometry to check, must not be null
     * @return           true if the geometry is a MultiPolygon or a MultiLineString, false otherwise
     */
    private boolean isMultiPolygonOrLineStringOrMultiPoint(String geometry) {
        return geometry.equalsIgnoreCase("MultiPolygon") || geometry.equalsIgnoreCase("MultiLineString") || geometry.equalsIgnoreCase("MultiPoint");
    }
    
    
  
        JFrame f;    
        public void TableFrame(String [][] data, String Title) {
            TableFrame(data,null,null,Title);
        }
        double manzanasSinFrente,manzanaGeometriaError, manzanaSinError, frentesSinError, frentesGeometriaError;

        public void TableFrame(String[][] data, List<String> ManzanasListCvegeo, List<String> FrentesListCvegeo, String Title) {

            manzanasSinFrente = 0.0;
            manzanaGeometriaError= 0.0;
            manzanaSinError= 0.0;
            frentesSinError= 0.0;
            frentesGeometriaError= 0.0;
            //System.out.println("ManzanasListCvegeo: " + ManzanasListCvegeo + " | FrentesListCvegeo: " + FrentesListCvegeo + " | Title: " + Title);
            if(ManzanasListCvegeo != null)  {
            String[][] ManzanaNoTieneFrentes = new String[ManzanasListCvegeo.size()][2];
            }
            
        
        f=new JFrame();    
        JCheckBox BusquedaEstricta=new JCheckBox();
        BusquedaEstricta.setText("Busqueda Estricta");
        BusquedaEstricta.setBounds(10, 10, 150, 25);
        
        JCheckBox BusquedaNoEstricta=new JCheckBox();
        BusquedaNoEstricta.setText("Busqueda sin filtros");
        BusquedaNoEstricta.setBounds(10, 10, 150, 25);

/*      
        JCheckBox FiltrarResultadoErroneos=new JCheckBox();
        FiltrarResultadoErroneos.setText("Filtrar resultados erroneos");
		FiltrarResultadoErroneos.setBounds(10, 10, 150, 25); 
        */
        
        String column[]={"CVEGEO","GEOMETRY"};         
        JTable jt=new JTable(data,column);
        Color customGreen = new Color(29, 100, 4);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 1) {
                    String geometry = (String) value;
                    if (isMultiPolygonOrLineStringOrMultiPoint(geometry)) {
                        //System.out.println("COLOR BLUE: ["+row+"]"+ "Column: ["+column+"]"+ "Value: "+(String)value);
                        component.setForeground(colors.infoColor);
                        if(column == 0){
                            component.setForeground(colors.neutraColor);
                        }
                        frentesSinError++;
                    }else{
                        //System.out.println("COLOR RED: ["+row+"]"+ "Column: ["+column+"]"+ "Value: "+(String)value);
                        component.setForeground(colors.errorColor);
                        if(ManzanaNoTieneFrentes != null)  {
                        ManzanaNoTieneFrentes[row][0] = (String) value;
                        ManzanaNoTieneFrentes[row][1] = (String) table.getValueAt(row, 0);
                        manzanaGeometriaError++;
                        }
                        frentesGeometriaError++;

                    }
                }else{
                    if(column == 0){
                        if(ManzanasListCvegeo != null && FrentesListCvegeo != null){
                            //JOptionPane.showInternalConfirmDialog(this, "ManzanaHasFrente: " + ManzanaHasFrente(ManzanasListCvegeo, FrentesListCvegeo));
                            //JOptionPane.showInternalConfirmDialog(this, "ManzanaHasFrente: " + FrentesListCvegeo.contains(ManzanasListCvegeo.get(row)));
                            if(ManzanaHasFrente(ManzanasListCvegeo, FrentesListCvegeo, (String)value)){

                                component.setForeground(customGreen);
                                manzanaSinError++;
                            }else{
                                component.setForeground(colors.errorColor);
                                if(ManzanaNoTieneFrentes != null)  {
                                ManzanaNoTieneFrentes[row][0] = (String) value;
                                ManzanaNoTieneFrentes[row][1] = (String) table.getValueAt(row, 0);
                                manzanasSinFrente++;
                                }
                            }
                        }else{
                            component.setForeground(Color.BLACK);
                            //frentesSinError++;
                        }
                    }else{
                        component.setForeground(Color.BLACK);
                    }
                    
                }
                return component;
            }
        };
        jt.getColumnModel().getColumn(1).setCellRenderer(renderer);// Geometry
        jt.getColumnModel().getColumn(0).setCellRenderer(renderer); // Cvegeo



        TableRowSorter sorter = new TableRowSorter<>(jt.getModel());
        jt.setRowSorter(sorter);

        JTextField filterField = new BorderRadiusJTextField(15);
        
        JButton buttonFilter = new JButton("Filtrar");
        BusquedaNoEstricta.setSelected(true);
        BusquedaNoEstricta.addActionListener(e -> {
            String text = filterField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                //sorter.setRowFilter(RowFilter.regexFilter("\\b" + text+"\\b")); // \\b matches word boundaries
                if(BusquedaEstricta.isSelected()){
                    sorter.setRowFilter(RowFilter.regexFilter("\\b" + text+"\\b")); // \\b matches word boundaries
                }else{
                    if(BusquedaNoEstricta.isSelected()){
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // (?i) para ignorar mayúsculas/minúsculas
                    }
                }
            }
        });
        BusquedaEstricta.addActionListener(e -> {
            String text = filterField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                //sorter.setRowFilter(RowFilter.regexFilter("\\b" + text+"\\b")); // \\b matches word boundaries
                if(BusquedaEstricta.isSelected()){
                    sorter.setRowFilter(RowFilter.regexFilter("\\b" + text+"\\b")); // \\b matches word boundaries
                }else{
                    if(BusquedaNoEstricta.isSelected()){
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // (?i) para ignorar mayúsculas/minúsculas
                    }
                }
            }
        });



        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }

            private void applyFilter() {
                String text = filterField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    //sorter.setRowFilter(RowFilter.regexFilter("\\b" + text+"\\b")); // \\b matches word boundaries
                    if(BusquedaEstricta.isSelected()){
                        sorter.setRowFilter(RowFilter.regexFilter("\\b" + text+"\\b")); // \\b matches word boundaries
                    }else{
                        if(BusquedaNoEstricta.isSelected()){
                            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // (?i) para ignorar mayúsculas/minúsculas
                        }
                    }
                }
            }
        });
        buttonFilter.addActionListener(e -> {
            String text = filterField.getText();
            //JOptionPane.showMessageDialog(this, "Filtrar texto por: ".concat(text), "Advertencia", JOptionPane.WARNING_MESSAGE);
            if(text.trim().length() == 0){
                sorter.setRowFilter(null);
            }else{
                try {
                    sorter.setRowFilter(RowFilter.regexFilter(text));
                    } catch(PatternSyntaxException pse) {
                        System.out.println("Bad regex pattern");
                    }
            }
        });

        
        filterField.setBackground(new Color(192,192,192));
        // add margin to filterfield
        Border line = BorderFactory.createLineBorder(Color.BLUE);
        Border empty = new EmptyBorder(5,20,5,20);
        CompoundBorder border = new CompoundBorder(line, empty);
        filterField.setBorder(border);

        // Crear grupo de botones
        ButtonGroup group = new ButtonGroup();
        group.add(BusquedaEstricta);
        group.add(BusquedaNoEstricta);
        //group.add(FiltrarResultadoErroneos);
        // Crear mainContainer para los botones de búsqueda
        JPanel SearchPanel = new JPanel(new BorderLayout());
        JPanel ButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        SearchPanel.add(filterField, BorderLayout.NORTH);
        ButtonsPanel.add(BusquedaEstricta, BorderLayout.CENTER);
        ButtonsPanel.add(BusquedaNoEstricta);
        //ButtonsPanel.add(FiltrarResultadoErroneos);
        SearchPanel.add(ButtonsPanel, BorderLayout.CENTER);
        

        // Layout y añadir componentes al frame
        f.setIconImage(icon);
        f.setTitle(Title);
        f.setLayout(new BorderLayout());
        f.setMinimumSize(new Dimension( 500, 400));
        //f.add(buttonFilter, BorderLayout.SOUTH);
        
        //f.add(filterField, BorderLayout.NORTH);
        f.add(SearchPanel, BorderLayout.NORTH);
        //f.add(new JScrollPane(jt), BorderLayout.SOUTH);

        jt.setBounds(30,40,200,400);          
        JScrollPane sp=new JScrollPane(jt);    
        f.add(sp, BorderLayout.CENTER);  
        f.setSize(300,700);    
        f.setVisible(true);  
        
        Colors c = new Colors();
        if(ManzanasListCvegeo != null){
            System.out.println("Manzana sin frentes: " + c.console_rojo + manzanasSinFrente + c.console_reset + " | Manzana con error en la geometría: " + c.console_rojo +manzanaGeometriaError + c.console_reset + " | Manzana sin error: " + c.console_verde+ manzanaSinError + c.console_reset);

        }else{
            if(ManzanasListCvegeo == null){
                
                System.out.println("Frentes sin error: " + c.console_verde + frentesSinError + c.console_reset + " | Frentes con error en la geometría: " + c.console_rojo +frentesGeometriaError + c.console_reset);
            }
        }
    }
    
private void FillTableDataWithErrors(String[][] manzanaNoTieneFrentes, JTable table) {
    DefaultTableModel model = (DefaultTableModel) table.getModel();

    for (String[] row : manzanaNoTieneFrentes) {
        model.addRow(row);
    }
}

    // Comparación de manzanas que tengan un frente
    private boolean ManzanaHasFrente(List<String> ManzanasListCvegeo, List<String> FrentesListCvegeo, String manzana) {
        return FrentesListCvegeo.contains(manzana);
    }
    
    private String obtenerNombreTemaActual() {
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        if (lookAndFeel instanceof FlatLaf) {
            return lookAndFeel.getName();
        }
        return "Desconocido";
    }

    private boolean isLightTheme(){
        return obtenerNombreTemaActual().toLowerCase().contains("light") || obtenerNombreTemaActual().toLowerCase().equalsIgnoreCase("everest");
        
    }
    // metodo que se ejecuta al cerrarse la ventana
    private void cerrarVentana() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Cerrando ventana");
                btnMenu.setEnabled(true);
                themeSelector.setEnabled(true);
            }
        });    
    }
}
