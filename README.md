# CodeCollab
Proof of concept web application designed to allow users to be able to edit and share code files without the need for a repository. Use cases could include in academic office hours, extracurricular coding exercises, or in professional interview settings. This is a java application which relies on TCP connections between different end systems.

## TLDR:

CodeCollab is a Java application designed to be used a means of promoting collaboration in coding projects and efforts. Inspired by GitHub, CodeCollab is meant to be a user-friendly application run by two separate hosts to be able to send and receive file updates through a central server, which would be running on the session hosts computer in the application’s completed form. This in turn would create a Peer to Peer (P2P) architecture established on separate hosts relying on a TCP connection.  The purpose for creating CodeCollab is due mostly to personal frustrations about the complexity of using GitHub. While a wonderful tool, I feel there could be an easier way to write software as a team if simplicity is the desire, as in most academic or learning settings where the projects are never that large, or even for use in Office Hours so instructors and students can work together remotely without needing to create a remote repository. 
 
CodeCollab is designed to be an application run on user’s PCs, that would allow you to edit files, specifically coding files, and send them to another user logged in to your session. When wanting to start a session, a user will run the app and select start session, where the server and client files will be run on their computer. This would allow other users to join the session, referencing the hosts user’s IP address. In this proof of concept, I have designed a server and client system, where the client can make requests to the server, allowing the server to store or send files back to the client.


## Protocol Definition

This application implements a client-server model designed to run on the hosts end-systems in an effort to form a P2P collaborative coding environment. The connections to the network rely on TCP sockets, with a persistent server set up to support incoming client requests. The server is able to store and send specified files, rewriting that file on the client end or creating the file if none exists already. The client and server are each able to send and receive files sent by one another, with the client side having a list of commands that will prompt the server depending on what is requested. The server is able to handle these commands and throw exceptions back to the client if any bad requests are made. 

## File Reading Method (readFile):

In both the server and client, there is a readFile and sendFile function. The functionality of both are nearly identical for both processes. The readFile is called when the program is expecting an incoming file. When this is called on the client for example, a message is sent to the server to prompt it to send a file, and this allows for the contents of that file to be read and added to the existing file on the client side. When the server runs readFile, it is because the client has sent a file. If this is the case, the client sends the server a message to tell it to prepare to read in the file, and the server reads in the data and either creates or updates the file on its end.

## File Sending Method (sendFile):

The file sending functionality is also somewhat self-explanatory. The client uses sendFile when the user wants to upload their code from their computer to the server. To do this, the client prompts the server that it is about to start sending, and then sends the file byte by byte (in order to accommodate for files of any size), to the server which will read it in byte by byte. When the server is sending a file, it first must check to see if that file is available. This does not need to be done on the client sendFile because the client class creates a file if the specified file does not exist. If the server does not contain the requested file, it will respond with an “ERROR” server status to the client, who will then prompt the user that the file does not exist in the server, and recommends they push first.

## Code Compilation/Execution

To compile this code on a Linux machine:

1.	Find the directory which the files are stored
2.	Open a separate command prompt for each process (1 for client 1 for server) in their respective directories (client and server directory)
3.	Start the server process by typing:	
	a. javac server.java
	b. java server.java
4.	To start the client process, type:
	a. javac client.java
	b. java client.java "IP Address of Host Machine"

To use the program, it is helpful to create your own .txt file in the client process and edit it so you can send a custom message and check its correctness on the server side. In the submission, I have included a sample file to use in the client directory. This file is named “test.txt.” Upon execution of client process, you will be prompted to enter the name of the file you wish to send, type the full name with extension (ex. test.txt); in the completed version I would like to change this to the directory your project is in in order to be able to update an entire folder automatically. After this, a menu will appear of options, you may edit the file or send it right away without making edits. You may also try to request without sending to see the exception handling. 

## Available Commands

The client end of this application allows for input from users, with functionality similar to GitHub’s push and pull. The client-side application remains idle until a command is prompted, where it will then execute that command and produce output that it has finished. If an invalid command is entered, the client will print out a message to the terminal with a menu of lists of commands. The command list contains:

### “s”
 
- The “s” command stands for send. This is what the client will prompt when wanting to push their work to the server. This command calls the sendFile function to handle sending and updating the working file to the server.

### “r”
 
- The “r” command stands for receive. This is what the client will prompt when wanting to receive updated files, presumably from a peer, and update their current working file. This command calls the readFile function to handle receiving a file from the TCP socket.

### “h”
 
- The “h” command is for help. The client can use this to bring up a menu, which has a list of all of the available commands.

### “q”
 
- The “q” command stands for quit. When wanting to finish their session, a user can send “q” to close the connection with the TCP server. The server will respond by asking if the user would like to quit the server session as well. The user can type “y” in the server terminal to do so, allowing for a graceful termination with no runaway processes.

## Description of Code Structure

This web application has been split into two separate processes, client and server. In this model, the client application handles user input, while the server handles requests made by the client in regards to file storage and retrieval.

### Client.java

The client process begins by reading the server IP address from the function call, storing the user’s input in argv[]. A TCP client socket is designated to port 8546 with this input IP address, allowing for DataOutputStream and BufferedReaders to be initialized to communicate with the server’s socket. The Output Stream is responsible for sending data back to the server in byte form, while the input stream is responsible for reading data sent from server in string form. When the process is running, “Welcome to CodeCollab” will be printed to the console followed by input prompts for the user. This includes the program asking for the name oof the file that it will be sending and retrieving. Once it knows this, the client process is ready to service requests from the user with the options listed above. If the “q” option is selected, all client processes are terminated and the connection to the server is severed. The program will print “Thank you for using CodeCollab” and terminate.

### Server.java

The server process begin by setting up a TCP server socket and designating its port number to 8546. This make available incoming connections from clients to establish a persistent server. Once established, the server will wait for a client to establish connection. If a client connection is made, the server will initialize a socket for the client allowing for a persistent line of communication for the client’s entire session. A DataOutputStrea mand BufferedReader are established for the same reason as in the client process. If a connection to a client has been established, the server will wait for input from that client until there is not input to be read or the client enters “q.” This allows the server to idle and wait for requests from the client to be made. If a request is made, the server will service it, output a corresponding message, and continue to idle. If the client message is “q,” the server will ask if it should terminate its process, if yes, it will close all sockets and streams, and terminate the program. 
