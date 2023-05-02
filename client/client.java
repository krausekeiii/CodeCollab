package client;

import java.io.*;
import java.net.*;

public class client {
    //must put IP address when running
    public static void main(String argv[]) throws Exception {
        System.out.println("CLIENT: Designated IP address is " + argv[0]);

        String ipAddress = argv[0];

        Socket clientSocket = new Socket(ipAddress, 8546);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        System.out.println("Welcome to CodeCollab!");
        printMenu();

        BufferedReader input  = new BufferedReader(new InputStreamReader(System.in));
        String instruction;
        
        System.out.println("Please input name of file you will be working on");
        String filename = input.readLine();
        System.out.println("Will be updating file " + filename);
        
        while(!(instruction = input.readLine()).equals("q")){
            if(instruction.equals("s")) {
                //send message to server so it can prepare to receive file
                outToServer.writeBytes(instruction + '\n');
                //send filename to server
                outToServer.writeBytes(filename + '\n');
                //send entire file to server
                sendFile(filename, outToServer);
            }
            else if(instruction.equals("r")) {
                //request file from server
                outToServer.writeBytes(instruction + '\n');
                //send filename to server
                if(inFromServer.readLine().equals("r")){
                    outToServer.writeBytes(filename + '\n');
                    //rewrite file on client side with contents of this file
                    readFile(filename, inFromServer);
                }
    
                System.out.println("CLIENT: Done receiving file");
            }
            else if(instruction.equals("h")) {
                //display help menu
                printMenu();
            }
            else {
                System.out.println("Please enter valid command");
                printMenu();
            }
        }

        System.out.println("Thank you for using CodeCollab!");
        outToServer.close();
        inFromServer.close();
        clientSocket.close();
    }

    //prints menu
    public static void printMenu() {
        System.out.println("Menu Selections:");
        System.out.println("    Press \"s\" to upload changes");
        System.out.println("    Press \"r\" to receive changes");
        System.out.println("    Press \"q\" to quit");
        System.out.println("    Press \"h\" to display this menu");
    }

    //reads incoming files
    public static void readFile(String filename, BufferedReader fromServer) throws Exception{
        System.out.println("CLIENT: Reading file " + filename);
        //sends file name to server so it knows which file to send
        FileOutputStream fileWriter = new FileOutputStream(filename);
        //ensures server has file
        if(fromServer.readLine().equals("OK")) {
            char[] buffer = new char[1];
            int bytesRead = 0;

            //reads in file byte by byte until reaching ~
            while((bytesRead = fromServer.read(buffer)) > 0) {
                if(buffer[bytesRead-1] == '~') {break;}
                fileWriter.write(new String(buffer, 0, bytesRead).getBytes());
            }
        }
        //if file is not in server
        else {
            System.out.println("Invalid request for file no found in server, try sending file first. For help press h");
        }

        fileWriter.close();
        System.out.println("CLIENT: Done reading file");
    }

    public static void sendFile(String filename, DataOutputStream toServer) throws Exception{
        System.out.println("CLIENT: Sending file " + filename);
        File file = new File(filename);
        FileInputStream fileReader = new FileInputStream(file);

        //create buffer to hold file data
        byte[] buffer = new byte[1];
        int bytesRead = 0;

        //send file contents to server 1 byte at a time
        while((bytesRead = fileReader.read(buffer)) != -1) {
            toServer.write(buffer, 0, bytesRead);
        }

        //send delimter to mark file end
        toServer.write('~');

        fileReader.close();     
        System.out.println("CLIENT: Done sending file");

    }

}