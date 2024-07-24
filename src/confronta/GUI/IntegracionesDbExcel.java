package confronta.GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Utility.Colors;

public class IntegracionesDbExcel extends JFrame{
    Colors colors = new Colors();
    public static void main(String[] args) {
        new IntegracionesDbExcel();
    }

    IntegracionesDbExcel(){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        this.setTitle("Integraciones");
        //this.setSize(300, 200);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        JPanel panel = new JPanel();
        panel.setBackground(colors.accentColor);
        add(panel);
        this.setBackground(colors.neutraColor);
        this.setContentPane(panel);
        this.setSize(400,300);
        this.setResizable(false);
    }
    
}
