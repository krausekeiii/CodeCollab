package server;

import java.io.*;
import java.net.*;

public class server {

    public static void main(String[] argv) throws Exception{

        //socket to wait for incoming connections
        ServerSocket welcomeSocket = new ServerSocket(8546);
        System.out.println("SERVER: CodeCollab server process initiated");

        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        String terminate;

        //runs indefinitely to maintain a persistent server
        while(true) {
            //establish connection with client
            System.out.println("SERVER: Waiting to establish connection with client...");
            Socket connectionSocket = welcomeSocket.accept();
            DataOutputStream toClient = new DataOutputStream(connectionSocket.getOutputStream());
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            if (connectionSocket.isConnected()) {
                System.out.println("SERVER: Connection with client established");
            }

            //instruction read in from client
            String instruction;
            //wait for instructions
            while((instruction = fromClient.readLine()) != null) {
                System.out.println("CLIENT: " + instruction);
                if(instruction.equals("s")) {
                    System.out.println("SERVER: Incoming file from client");
                    //this means the client is sending a file
                    readFile(fromClient);
                }
                else if(instruction.equals("r")) {
                    //this means the client is requesting a file
                    System.out.println("SERVER: File requested from client");
                    //send message to client to prepare it to read a file
                    toClient.writeBytes("r\n");
                    sendFile(fromClient, toClient);
                    System.out.println("SERVER: Done sending file");
                }
                else {
                    System.out.println("SERVER: Invalid command received from client: " + instruction);
                }
            }

            //send message to ask if server should be closed
            System.out.println("SERVER: Would you like to terminate the server process? (y/n)");
            if((terminate = userIn.readLine()).equals("y")) { 
                connectionSocket.close();
                toClient.close();
                fromClient.close();
                break; 
            }
        }

        //close all connections
        welcomeSocket.close();
        userIn.close();
    }

    public static void readFile(BufferedReader clientMessage) throws Exception{
        //read filename from client
        String filename = clientMessage.readLine();
        System.out.println("SERVER: Reading file " + filename);
        FileOutputStream fileWriter = new FileOutputStream(filename, false);

        //buffer to read bytes 1 bit at a time
        char[] buffer = new char[1];
        int bytesRead = 0;

        //loop through message until reaching ~ delimiter, writing contents to file
        while((bytesRead = clientMessage.read(buffer)) != -1) {
            if(buffer[bytesRead-1] == '~') {break;}
            fileWriter.write(new String(buffer, 0, bytesRead).getBytes());
        }

        fileWriter.close();
        System.out.println("SERVER: Done reading file");
    }

    public static void sendFile(BufferedReader fromClient, DataOutputStream toClient) throws Exception{
        String filename = fromClient.readLine();
        File file = new File(filename);
        String serverStatus;
        //if file does not exist on server side, error will occur
        //ensures file is in server
        if(file.exists()) {
            serverStatus = "OK";
            //wriet OK server status to prepare client for file receiving
            toClient.writeBytes(serverStatus + '\n');
            System.out.println("SERVER: Sending file " + filename);
            FileInputStream fileReader = new FileInputStream(file);
            
            //send file byte by byte
            byte[] buffer = new byte[1];
            int bytesRead = 0;

            while((bytesRead = fileReader.read(buffer)) != -1) {
                if(bytesRead == 0 && buffer[bytesRead-1] == 'r') { continue; }
                toClient.write(buffer, 0, bytesRead);
            }

            //sends delimiter so client knows file transmission is done
            toClient.write('~');
            fileReader.close();
            System.out.println("SERVER: Done sending file");
        }
        else {
            //if file not found in server
            System.out.println("SERVER: Invalid file request received");
            System.out.println("    File requested: " + filename);
            serverStatus = "ERROR";
            toClient.writeBytes(serverStatus + '\n');
        }
        
    }
}
