import Control.MessageManager;
import Control.MessageType;
import Model.Music;
import Model.MusicCatalog;
import Model.Stream;
import Model.StreamCatalog;

import java.io.*;
import java.net.*;


public class Server {
    //Catálogo de músicas disponíveis no servidor
    public static MusicCatalog musicCatalog = new MusicCatalog("audio/");
    //Catálogo de streams conhecidos pelo servidor
    public static StreamCatalog streamCatalog = new StreamCatalog();

    public static void main(String[] args) throws IOException {
        //Inicio do servidor
        ServerSocket server = new ServerSocket(33333);
        System.out.println("Servidor iniciado - Endereco: "+server.getInetAddress()+" - Porta: "+server.getLocalPort());

        while (true) {
            Socket client = server.accept();
            System.out.println("Nova conexão: Endereco: "+ client.getInetAddress());
            //Após nova conexão aceita, uma thread é disparada para tratamento específico da mesma
            forkConnection(server, client);

        }
    }

    //Tratamento de novas conexões
    public static void forkConnection(final ServerSocket server, final Socket client){

        new Thread() {
            @Override
            public void run() {

                try {
                    //Leitura da primeira mensagem vinda do cliente
                    BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String message = input.readLine();

                    //Enquanto a mensagem do cliente não for para fechar a conexão, continue escutando
                    while(MessageManager.getMessageType(message) != MessageType.CLOSE) {

                        //Tratamento de mensagem do tipo Requisicão da lista de músicas
                        //Esse bloco envia ao cliente a lista de músicas conhecidas, com seus respectivos nomes e códigos
                        if (MessageManager.getMessageType(message) == MessageType.REQUESTMUSICLIST) {
                            PrintStream saida = new PrintStream(client.getOutputStream());
                            String catalog = musicCatalog.generateCatalogToClient();

                            if(!musicCatalog.equals("empty")) {
                                saida.println(MessageManager.generateMessage(MessageType.CONFIRMATION, musicCatalog.generateCatalogToClient()));
                            }else{
                                saida.println(MessageManager.generateMessage(MessageType.DENY,""));
                            }

                            //Tratamento de mensagem do tipo Requisicão da lista de streams
                            //Esse bloco de código envia ao cliente uma lista de streams conhecidos, com seus respecticos nomes e códigos
                        } else if (MessageManager.getMessageType(message) == MessageType.REQUESTSTREAMLIST) {
                            PrintStream saida = new PrintStream(client.getOutputStream());
                            String catalog = streamCatalog.generateCatalogToClient();

                            if(!catalog.equals("empty")) {
                                saida.println(MessageManager.generateMessage(MessageType.CONFIRMATION, catalog));
                            }else{
                                saida.println(MessageManager.generateMessage(MessageType.DENY,""));
                            }

                            //Tratamento de mensagem do tipo Requisicão de música
                            //Esse bloco de código envia ao cliente a música requisitada, através do seu código
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

                            //Tratamento de mensagem do tipo Requisicão de stream
                            //Esse bloco de código envia ao cliente os dados de um stream requisitado através do seu código
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

                            //Tratamento de mensagem do tipo Requisicão de cadastro de novo stream
                            //Esse bloco de código cadastra um novo stream na lista de streams conhecido
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

                        try {
                            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            message = input.readLine();
                        }catch (Exception e){
                            System.out.println("Conexão encerrada de forma inesperada com: "+client.getInetAddress());
                        }
                    }

                    System.out.println("Encerrando conexão: "+client.getInetAddress());
                    client.close();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }

        }.start();
    }


}
