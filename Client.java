import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import  java.io.InputStream;



public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    public Client( Socket socket, String username)
    {
        try
        {
            this.socket=socket;


            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username=username;
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void sendMessage()
    {
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner sc=new Scanner(System.in);
            while (socket.isConnected())
            {
                String messagaTosend=sc.nextLine();
                bufferedWriter.write(username +": "+messagaTosend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void listenForMessage()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupchat;
                while (socket.isConnected())
                {
                    try {
                        msgFromGroupchat=bufferedReader.readLine();
                        System.out.println(msgFromGroupchat);
                    }
                    catch (IOException e)
                    {
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter)
    {
        try {
            if(bufferedReader!=null)
            {
                bufferedReader.close();
            }
            if(bufferedWriter!=null)
            {
                bufferedReader.close();
            }
            if(socket!=null)
            {
                socket.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String[]ags) throws IOException {
        Scanner sc=new Scanner(System.in);
        System.out.println("enter your username for the group chat:");
        String username=sc.nextLine();
        Socket socket=new Socket("localhost",1234);
        Client client=new Client(socket,username);
        client.listenForMessage();
        client.sendMessage();
    }
}
