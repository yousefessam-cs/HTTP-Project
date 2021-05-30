package http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;
public class server {
	static ServerSocket serverSocket;
	public static Vector <String> username=new Vector<String>();       // to save usernames
	
	public static Vector <String> password=new Vector<String>();       // to save passwords
	
	public static void main (String args[])
	{
		try
		{
			username.add("user");		//Default username
			password.add("user");		//Default password
			serverSocket = new ServerSocket(80);		//Start a serversocket with port number 80 for http
			System.out.println("server is booted up");
			while (true)
			{
				Socket clientSocket = serverSocket.accept();		//Get client socket
				System.out.println("A new client ["+clientSocket+"]is connected to the server");
				Thread client =new ClientConnection(clientSocket); 
				client.start(); 	   //Start a new thread	
			
			}
		}
		catch(Exception e)
		{
			System.out.println("problem with socket server");
		}
	}
	static class ClientConnection extends Thread
	{
		final private Socket clientSocket;
		public ClientConnection(Socket clientSocket)
		{
			this.clientSocket=clientSocket;
		}
		public void run()
		{
			try
			{
				DataInputStream input = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
				Scanner scanner = new Scanner(System.in);
				
				while(true)
				{
					String client_choice;		//To take from client whether they want to 1-signin 2-signup
					client_choice=input.readUTF();
					String user="";
					String pass="";
					String wrong="";
					output.writeUTF("please enter the username:");
					String request=input.readUTF();
					user=request;										//username sent by the client
					if(user.equalsIgnoreCase("close"))                  //if client wants to close the connection
						break;
					output.writeUTF("please enter the password:");
					request=input.readUTF();
					pass=request;										//password sent by the client
					if(client_choice.equalsIgnoreCase("1")) {           //signin
						for(int i=0; i<username.size();i++)		//Searches the vectors for username and password
						{
							if(username.get(i).equalsIgnoreCase(user) && password.get(i).equalsIgnoreCase(pass)) 		// compare username and password to current vector elements
							{
								output.writeUTF("connected");
								read_response(clientSocket,username.get(i),password.get(i));    //function to read the user's input (see below)
								break;
							}
							if(i==username.size()-1)											//to check if it reached the end of the vector
							{
								wrong="wrong";
							}
						}
						if(wrong=="wrong")														//to retry in case of wrong user name and password
						{
							output.writeUTF("You have entered wrong user name or password, please try again."); //server to client
							continue;
						}
						break;
					}
					else if(client_choice.equalsIgnoreCase("2")) { 		//signup
						username.add(user);				//saves new username
						password.add(pass);				//saves new password
						output.writeUTF("connected");
						read_response(clientSocket,username.get(username.size()-1),password.get(password.size()-1));		//function to read the user's input (see below)
						break;
					}
					
				}
				
				input.close();
				output.close();
				scanner.close();
				
			}
			catch(IOException e)
			{
				System.out.println("Connetion with this client ["+clientSocket+"] is terminated");
			}
		}
	}
	public static void read_response(Socket clientsocket,String user,String pass)
	{
		try {
			BufferedReader request =new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
			BufferedWriter response =new BufferedWriter(new OutputStreamWriter(clientsocket.getOutputStream()));
			String clientdata="",requestdata="",path="";
			for(int i=0;i<2;i++)
			{
				clientdata+=request.readLine()+"\n";   //to get the request from client to server
			}
			StringBuilder dataresponse= new StringBuilder();       // used later to get the responseheader depending on the request,from responseheader function (see below)
			System.out.println(clientdata);
			requestdata=clientdata.split("\n")[0].split(" ")[0];   // to find whether the request is GET or POST
			path=clientdata.split("\n")[0].split("/")[1].split(" ")[0];   //to get the requested url (filename)
			System.out.println(path);
			if(checkURL(path))        			// get the file path on the server    see below for checkURL function
			{
				File filee = new File(path);
				path = filee.getAbsolutePath();
				System.out.println(path);
				response.write(path+"\n");      //sends path to client 
			}
			else    							// didn't find the file
			{
				path="not found";
				response.write(path+"\n");

				
			}
			// Respond to the request depending on whether it is GET or POST  and checks for file (see checkURL below)
			if (requestdata.equalsIgnoreCase("GET")&&checkURL(path))
			{
				
				responseheader(200,dataresponse);
				dataresponse.append("USERNAME: "+user+"\n");
				dataresponse.append("PASSWORD: "+ pass+"\n");
				response.write(dataresponse.toString()); 	//sends response header to client
				response.write(getdata(path));              //sends response body to client (see getdata below)
				dataresponse.setLength(0);
				response.flush();    //flushes the stream to send response
			}
			else if (requestdata.equalsIgnoreCase("POST")&&checkURL(path))
			{
				responseheader(200,dataresponse);
				response.write(dataresponse.toString());	 //sends response header to client
				response.write(getdata(path));				 //sends response body to client (see getdata below)
				dataresponse.setLength(0);
				response.flush();    //flushes the stream to send response
			}
			else  // when checkURL returns false or when request type is not GET or POST
			{
				responseheader(404,dataresponse);
				response.write(dataresponse.toString()); 	 //sends response header to client
				response.flush();     //flushes the stream to send response
			}
		} catch (IOException e) {
		} 
	}
	private static boolean checkURL(String file) {       //To check that the file exists and isn't a Directory(folder)
		File myFile = new File(file);
		return myFile.exists() && !myFile.isDirectory();
	}
	private static String getdata (String file)          //To get data from requested file
	{
		File testfile =new File(file);
		BufferedReader reader ;
		String responseToClient="";
		
		try {
			reader = new BufferedReader(new FileReader(testfile));
			String line = null;
			while ((line=reader.readLine())!=null) {		//to read String data from file //stops when it reaches EOF(end of file)
				responseToClient += line+"\n";
			}

			// System.out.println(responseToClient);
			reader.close();
			
			
		} catch (Exception e) {
				System.err.println("error");
		}
		return responseToClient;
	}
	private static void responseheader (int code,StringBuilder dataresponse)  // returns the response header depending on the http code
	{
		if (code==200)
		{
			dataresponse.append("HTTP/1.1 200 OK\r\n");
			dataresponse.append("Date:" + getTime() + "\r\n");
			dataresponse.append("Server:localhost\r\n");
			dataresponse.append("Connection: Closed\r\n\r\n");
		}
		else if (code == 404) {

			dataresponse.append("HTTP/1.1 404 Not Found\r\n");
			dataresponse.append("Date:" + getTime() + "\r\n");
			dataresponse.append("Server:localhost\r\n");
			dataresponse.append("\r\n");
			
		}
	}
	private static String getTime() //To get date for response header
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		return formattedDate;
	}
}