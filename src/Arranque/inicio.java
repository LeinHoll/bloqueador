package Arranque;

import Visual.frameBloqueador;
import java.awt.Dimension;
import java.awt.Toolkit;

public class inicio{
    public static void main(String [] args){
        frameBloqueador Cyber = new frameBloqueador();
        Dimension tamanyoPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        Cyber.setLocation(tamanyoPantalla.width - Cyber.getWidth(), 0);
        Cyber.setVisible(true);
    }
}
