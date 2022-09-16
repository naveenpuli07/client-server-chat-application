import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;//establish connection b/w server and client
    private BufferedReader bufferedReader;//read the msg from client
    private BufferedWriter bufferedWriter;//used to send data to client
    private String clientUsername;

    public ClientHandler(Socket socket)
    {
        try
        {
            this.socket= socket;
           this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//this stream to send things
           this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));//this stream to read things
            this.clientUsername=bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER:"+clientUsername+"has entered the chat!");
        } catch (IOException e) {
            closeEverything(this.socket,bufferedReader,bufferedWriter);
        }
    }




    @Override
    public void run() {
         String messageFromClient;
         while (socket.isConnected())
         {
             try{
                 messageFromClient=bufferedReader.readLine();
                 broadcastMessage(messageFromClient);
                 if(messageFromClient==null)
                 {
                     throw new IOException();
                 }
             }
             catch (IOException e)
             {
                 closeEverything(socket,bufferedReader,bufferedWriter);
                 break;
             }
         }
    }
    public void broadcastMessage( String messageToSend)
    {
        for(ClientHandler clientHandler:clientHandlers)
        {
            try{
                if(!clientHandler.clientUsername.equals(clientUsername))
                {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }
            catch (IOException e)
            {
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }
    public void removeClientHandler()
    {
        clientHandlers.remove(this);
        broadcastMessage("SERVER:"+clientUsername+"has left the chat!");
    }
    public void closeEverything(Socket socket,BufferedReader  bufferedReader,BufferedWriter bufferedWriter)
    {
        removeClientHandler();
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
}