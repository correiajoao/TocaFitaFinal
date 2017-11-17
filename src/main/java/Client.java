import Control.MessageManager;
import Control.MessageType;
import Control.SoundManager;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.*;

public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException, LineUnavailableException {
        Socket client = new Socket("127.0.0.1", 33333);
        System.out.println("Você está conectado ao Toca Fitas!");

        Scanner input = new Scanner(System.in);
        int option = 0;

        while(option != 4){

        do {
            System.out.println("1 - Lista de Streams");
            System.out.println("2 - Lista de Músicas");
            System.out.println("3 - Criar Stream");
            System.out.println("4 - Sair");

            System.out.print("Escolha sua opcão: ");
            option = input.nextInt();
            System.out.println();

        } while (option > 4 || option < 0);

        switch (option) {
            case 1: {
                PrintStream saida = new PrintStream(client.getOutputStream());
                saida.println(MessageManager.generateMessage(MessageType.REQUESTSTREAMLIST));

                BufferedReader entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String response = entrada.readLine();

                if (MessageManager.getMessageType(response) == MessageType.CONFIRMATION) {
                    String[] parameters = MessageManager.getMessageParameters(response);

                    if (parameters != null) {
                        String streamList = parameters[1];
                        System.out.println("########### Lista de Streams ###########");
                        System.out.println(streamList.replace("#","\n"));
                    }

                    System.out.println("Digite o código do stream que deseja ouvir: ");
                    Integer streamCode = input.nextInt();

                    saida = new PrintStream(client.getOutputStream());
                    saida.println(MessageManager.generateMessage(MessageType.REQUESTSTREAM, Integer.toString(streamCode)));

                    entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    response = entrada.readLine();

                    if (MessageManager.getMessageType(response) == MessageType.CONFIRMATION) {
                        parameters = MessageManager.getMessageParameters(response);

                        if (parameters != null) {
                            String addressSource = parameters[1];
                            String portSource = parameters[2];

                            Socket streamer = new Socket(addressSource, Integer.parseInt(portSource));

                            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
                            DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

                            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
                            sourceLine.open(format);
                            sourceLine.start();

                            DataInputStream dIn = new DataInputStream(streamer.getInputStream());

                            while (true) {
                                int length = dIn.readInt();
                                if (length > 0) {

                                    byte[] message = new byte[length];
                                    dIn.readFully(message, 0, length);
                                    sourceLine.write(message, 0, length);
                                }

                            }

                        }
                    }else{
                        System.out.println();
                        System.out.println("Stream não existente");
                        System.out.println();
                    }
                }else{
                    System.out.println();
                    System.out.println("Não há streams no momento, volte mais tarde");
                    System.out.println();
                }
                break;
            }case 2: {
                PrintStream saida = new PrintStream(client.getOutputStream());
                saida.println(MessageManager.generateMessage(MessageType.REQUESTMUSICLIST));

                BufferedReader entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String response = entrada.readLine();

                if (MessageManager.getMessageType(response) == MessageType.CONFIRMATION) {
                    String[] parameters = MessageManager.getMessageParameters(response);

                    if(parameters != null) {
                        String musicList = parameters[1];
                        System.out.println("########### Lista de músicas ###########");
                        System.out.println(musicList.replace("#","\n"));
                    }

                    System.out.println("Digite o código da música que deseja ouvir: ");
                    Integer musicCode = input.nextInt();

                    try {
                    //Envia a mensagem para o servidor
                    saida = new PrintStream(client.getOutputStream());
                    saida.println(MessageManager.generateMessage(MessageType.REQUESTMUSIC, Integer.toString(musicCode)));

                    entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    response = entrada.readLine();

                    if(MessageManager.getMessageType(response) == MessageType.CONFIRMATION) {
                         InputStream in = client.getInputStream();
                         OutputStream out = new FileOutputStream("audio/received/temp" + Calendar.getInstance().getTime() + ".mp3");

                         int count;
                         byte[] buffer = new byte[8192]; // or 4096, or more
                         while ((count = in.read(buffer)) >= 8192) {
                             out.write(buffer, 0, count);
                         }

                         SoundManager soundManager = new SoundManager();
                         soundManager.playMusic("audio/received/temp" + Calendar.getInstance().getTime() + ".mp3");
                    }else{
                        System.out.println();
                        System.out.println("Música não existente");
                        System.out.println();
                    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println();
                    System.out.println("Não há músicas no momento, volte mais tarde");
                    System.out.println();
                }
                break;
            }case 3: {
                try {

                    System.out.print("Digite o título do seu stream: ");
                    input.nextLine();
                    String streamTitle = input.nextLine();

                    Random rn = new Random();
                    String streamSourceAdress = client.getInetAddress().toString().replace("/", "");
                    String streamSourcePort = Integer.toString(rn.nextInt() % (65536 - 65000) + 65000 );

                    PrintStream saida = new PrintStream(client.getOutputStream());
                    saida.println(MessageManager.generateMessage(MessageType.NEWSTREAM, streamSourceAdress, streamSourcePort, streamTitle));

                    BufferedReader entrada = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String response = entrada.readLine();

                    if (MessageManager.getMessageType(response) == MessageType.CONFIRMATION) {
                        ServerSocket server = new ServerSocket(Integer.parseInt(streamSourcePort));
                        System.out.println("Informacões do servidor de stream em execucão - Endereco: "+streamSourceAdress+" - Porta: "+streamSourcePort+" - Título: "+streamTitle);

                        while (true) {
                            Socket listener = server.accept();
                            forkConnection(server, listener);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }case 4: {
                PrintStream saida = new PrintStream(client.getOutputStream());
                saida.println(MessageManager.generateMessage(MessageType.CLOSE));
                client.close();
                break;
            }
        }
        }
        client.close();
    }

    public static void forkConnection(final ServerSocket server, final Socket listener) {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);

        try {
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetLine.open(format);
            targetLine.start();

            int numBytesRead;
            byte[] targetData = new byte[targetLine.getBufferSize() / 5];

            while (true) {
                numBytesRead = targetLine.read(targetData, 0, targetData.length);

                if (numBytesRead == -1)
                    break;

                DataOutputStream dOut = new DataOutputStream(listener.getOutputStream());
                dOut.writeInt(targetData.length);
                dOut.write(targetData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

