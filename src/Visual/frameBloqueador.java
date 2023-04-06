package Visual;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import javax.swing.JOptionPane;

public class frameBloqueador extends javax.swing.JFrame {

    public frameBloqueador() {
        initComponents();
        misComponentes();
    }

    private void misComponentes(){
        new SocketCliente().start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        jLabel1.setText("BUSCANDO POR SERVIDOR");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private class SocketCliente extends Thread{

        @Override
        public void run(){
            boolean retry = true;
            while( retry ){
                try{
                    Socket socket = new Socket("192.168.1.95",1717);

                    BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    escritor = new PrintWriter(socket.getOutputStream(), true);
                    
                    nivelC = new ServerC();
                    nivelC.start();

                    String texto;

                    while((texto = lector.readLine()) != null){
                        JOptionPane.showMessageDialog(null, texto);
                        if(texto.startsWith("INSTRUCCION>>")){
                            texto = texto.replace("INSTRUCCION>>", "");
                            new safeJump(texto).start();
                            if(texto.equals("QUIEN ERES"))
                                habla("RESPUESTA>>IP>>" + InetAddress.getLocalHost().getHostAddress());
                            if(texto.equals("BLOQUEA"))
                                diagBloq();
                            if(texto.equals("DESBLOQUEA"))
                                cierraDiagBloq();
                            if(texto.equals("NO TE CONOZCO")){
                                retry = false;
                                new safeJump("EXIT").start();
                                break;
                            }
                        }
                    }

                }
                catch(IOException IOE){
                    try{
                        Thread.sleep(1500);
                    }catch(Exception exc){}
                }
                catch(Exception exc){
                    error(exc.getMessage());
                }
                seteaVisible();
            }
            salida();
        }

        public void habla(String texto){
            try{
                escritor.println(texto);
            }catch(Exception exc){
                error(exc.getMessage());
            }
        }

        PrintWriter escritor;
    }

    private void diagBloq(){
        setVisible(false);
        diagBloq = new diagBloqueo(this,false);
        diagBloq.setLocationRelativeTo(null);
        diagBloq.setVisible(true);
    }

    private void cierraDiagBloq(){
        diagBloq.setVisible(false);
    }

    private void seteaVisible(){
        setVisible(true);
    }

    private void salida(){
        dispose();
    }

    private void error(String error){
        JOptionPane.showMessageDialog(null, error);
    }

    private class ServerC extends Thread{
        @Override
        public void run(){
            try{
                new Runner().start();
                ServerSocket receptor = new ServerSocket(7171);
                Socket cliente = receptor.accept();

                entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                salida = new PrintWriter(cliente.getOutputStream(), true);

                String texto;

                while( (texto = entrada.readLine()) != null ){
                    if( texto.equals("EXIT") )
                        break;
                }

                salida.close();
                cliente.close();
                receptor.close();
            }catch(Exception exc){
                error(exc.getMessage());
            }
        }

        public void habla(String texto){
            salida.println(texto);
        }

        PrintWriter salida;
        BufferedReader entrada;
    }

    private class Runner extends Thread{
        @Override
        public void run(){
            try{
                Thread.sleep(1000);

                String dirJar = frameBloqueador.class.getProtectionDomain()
                        .getCodeSource().getLocation().toString();
                dirJar = dirJar.replace("file:/", "");
                dirJar = dirJar.replace("%20", " ");
                int UltiDir = 0;
                for(int i = 0 ; i < dirJar.length() ; i++)
                    if(dirJar.charAt(i) == '/')
                        UltiDir = i;
                dirJar = dirJar.substring(0, UltiDir);

                Process Proceso = Runtime.getRuntime().exec("\"" + dirJar + "/Cliente.exe\"");

                BufferedReader oyente = new BufferedReader(new InputStreamReader(Proceso.getInputStream()));

                String texto;

                while( (texto = oyente.readLine()) != null){
                    JOptionPane.showMessageDialog(null, texto);
                    if(texto.equals("EXIT"))
                        break;
                }

                oyente.close();
            }catch(Exception exc){
                error(exc.getMessage());
            }
        }
    }

    private class safeJump extends Thread{

        public safeJump(String texto){
            this.texto = texto;
        }

        @Override
        public void run(){
            while( true ){
                if(nivelC.isAlive()){
                    nivelC.habla(texto);
                    break;
                }
            }
        }

        String texto;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    diagBloqueo diagBloq;
    ServerC nivelC;
}