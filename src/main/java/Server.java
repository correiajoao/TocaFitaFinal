import Control.MessageManager;
import Control.MessageType;
import Model.Music;
import Model.MusicCatalog;
import Model.Stream;
import Model.StreamCatalog;

import java.io.*;
import java.net.*;

public class Server {
    public static MusicCatalog musicCatalog = new MusicCatalog("audio/");
    public static StreamCatalog streamCatalog = new StreamCatalog();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(33333);
        System.out.println("Servidor iniciado - Endereco: "+server.getInetAddress()+" - Porta: "+server.getLocalPort());

        while (true) {
            Socket client = server.accept();
            System.out.println("Nova conexão: Endereco: "+client.getInetAddress());
            forkConnection(server, client);
        }
    }

    public static void forkConnection(final ServerSocket server, final Socket client){

        new Thread() {
            @Override
            public void run() {

                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String message = input.readLine();

                        while(MessageManager.getMessageType(message) != MessageType.CLOSE) {

                            if (MessageManager.getMessageType(message) == MessageType.REQUESTMUSICLIST) {
                                PrintStream saida = new PrintStream(client.getOutputStream());
                                String catalog = musicCatalog.generateCatalogToClient();

                                if(!musicCatalog.equals("empty")) {
                                    saida.println(MessageManager.generateMessage(MessageType.CONFIRMATION, musicCatalog.generateCatalogToClient()));
                                }else{
                                    saida.println(MessageManager.generateMessage(MessageType.DENY,""));
                                }

                            } else if (MessageManager.getMessageType(message) == MessageType.REQUESTSTREAMLIST) {
                                PrintStream saida = new PrintStream(client.getOutputStream());
                                String catalog = streamCatalog.generateCatalogToClient();

                                if(!catalog.equals("empty")) {
                                    saida.println(MessageManager.generateMessage(MessageType.CONFIRMATION, catalog));
                                }else{
                                    saida.println(MessageManager.generateMessage(MessageType.DENY,""));
                                }

                            } else if (MessageManager.getMessageType(message) == MessageType.REQUESTMUSIC) {
                                String[] parameters = MessageManager.getMessageParameters(message);

                                if (parameters != null) {
                                    String code = parameters[1];

                                    Music music = musicCatalog.getMusic(Integer.parseInt(code));

                                    if(music != null) {

                                        PrintStream saida = new PrintStream(client.getOutputStream());
                                        saida.println(MessageManager.generateMessage(MessageType.CONFIRMATION,""));

                                        File file = new File(music.getMusicPath());
                                        long length = file.length();
                                        InputStream in = new FileInputStream(file);
                                        OutputStream out = client.getOutputStream();

                                        int count;
                                        byte[] buffer = new byte[8192];
                                        while ((count = in.read(buffer)) > 0) {
                                            out.write(buffer, 0, count);
                                        }
                                        in.close();
                                    }else{
                                        PrintStream saida = new PrintStream(client.getOutputStream());
                                        saida.println(MessageManager.generateMessage(MessageType.DENY, ""));
                                    }
                                }

                            } else if (MessageManager.getMessageType(message) == MessageType.REQUESTSTREAM) {
                                String[] parameters = MessageManager.getMessageParameters(message);

                                if (parameters != null) {
                                    String code = parameters[1];
                                    Stream streamer = streamCatalog.getStreamer(Integer.parseInt(code));

                                    if(streamer != null) {
                                        PrintStream saida = new PrintStream(client.getOutputStream());
                                        saida.println(MessageManager.generateMessage(MessageType.CONFIRMATION, streamer.getAdressSource(), streamer.getPortSource()));
                                    }else{
                                        PrintStream saida = new PrintStream(client.getOutputStream());
                                        saida.println(MessageManager.generateMessage(MessageType.DENY, ""));
                                    }
                                }

                            } else if (MessageManager.getMessageType(message) == MessageType.NEWSTREAM) {
                                String[] parameters = MessageManager.getMessageParameters(message);

                                if (parameters != null) {
                                    String adress = parameters[1];
                                    String port = parameters[2];
                                    String title = parameters[3];

                                    Stream newStream = new Stream(adress, port, title);
                                    streamCatalog.addStreamer(newStream);

                                    PrintStream saida = new PrintStream(client.getOutputStream());
                                    saida.println(MessageManager.generateMessage(MessageType.CONFIRMATION));
                                }
                            }
                             input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                             message = input.readLine();
                        }

                        client.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

            }

        }.start();
    }


}
