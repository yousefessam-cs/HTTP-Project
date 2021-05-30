package http;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class client {
	public static void GET_file(String host,String path)
	{
		try {
			host=host.substring(0, host.length()-4);    //remove \r\n from the end of the host string
			InetAddress ip1=InetAddress.getByName(host);  //get host IP address
			Socket Client=new Socket(ip1,80);            //create client socket on port 80 for http, with hpst IP address destination
			DataInputStream input = new DataInputStream(Client.getInputStream());
			DataOutputStream output = new DataOutputStream(Client.getOutputStream());
			Scanner scanner = new Scanner(System.in);
			String httpversion=path.split(" ")[2];      //get http version from the previous input
			httpversion=httpversion.substring(0, httpversion.length()-4); //remove \r\n from the end of http version
			path=path.split("/")[1].split(" ")[0];          //gets URL(filename) 
			
			while(true)    //for signin and signup
			{
				System.out.println("1- sign in");
				System.out.println("2- sign up");
				String client_choice;
				client_choice= scanner.nextLine();   //take choice input from client
				output.writeUTF(client_choice);      //and sends it to the server
				if(client_choice.equalsIgnoreCase("1"))   // signin
			
				{
					while (true)
					{
						String serverASK = input.readUTF();
						System.out.println( serverASK);							//enter your user name message
						String username="",password="";
						username =scanner.nextLine();				
						output.writeUTF(username);
						serverASK = input.readUTF();							//enter your password message
						System.out.println( serverASK);
						password=scanner.nextLine();
						output.writeUTF(password);
						serverASK = input.readUTF();
						if(serverASK.equalsIgnoreCase("connected"))
						{
							break;
						}
						System.out.println("You have entered an invalid username and password, Please try again");
			
					}
					break;
				}
				else if(client_choice.equalsIgnoreCase("2"))	// signup
				{
					String serverASK = input.readUTF();
					System.out.println( serverASK);
					String username="",password="";
					username =scanner.nextLine();
					output.writeUTF(username);
					serverASK = input.readUTF();
					System.out.println( serverASK);
					password=scanner.nextLine();
					output.writeUTF(password);
					serverASK = input.readUTF();
					break;
				}
				else
				{
				}
			}
			System.out.println("______________________________________");
			System.out.println("Connected");
			System.out.println("______________________________________");
			BufferedWriter file_request=new BufferedWriter(new OutputStreamWriter(Client.getOutputStream())); 							 //to send GET request to server
			BufferedReader file_response=new BufferedReader(new InputStreamReader(Client.getInputStream())); //to recieve the response to the GET from server
			String Wfile ="C:\\test\\"+path;       															 //directory for openning the file
			BufferedWriter writer = new BufferedWriter(new FileWriter(Wfile));      						 //to write to new file
			file_request.write("GET /" + path + " "+httpversion+"\n");
			file_request.write("Host: " + host + "\r\n");
			file_request.flush();		 //flushes the output stream to send the request to the server
			System.out.println("Request Sent!");
			System.out.println("______________________________________");
			String responseLine,file_path="";
			file_path=file_response.readLine();											//get file path from server
			int x=0;
			while ((responseLine = file_response.readLine()) != null) {    				//get next line from the socket input stream
				if(x>6) // to skip the response header
					writer.write(responseLine+"\n");  								    //writes to file from server
				System.out.println(responseLine);
				x++;
			}
			writer.close();
			if(!file_path.equalsIgnoreCase("not found"))                                // checks the server for the file 
			{
				File file =new File(Wfile);
				try {
				Desktop.getDesktop().open( file );                                   //opens the file window
				}
				catch(IOException e)
				{
					System.out.println("failed to open");
				}
			}
			System.out.println("______________________________________");
			System.out.println("Response Recieved!!");
			System.out.println("______________________________________");
			file_request.close();
			file_response.close();
			Client.close();
			scanner.close();
		}
		catch(Exception e)
		{
			System.err.println("error");
		}

	}
	public static void main (String args[])
	{
		Scanner scanner = new Scanner(System.in);
		
		String request="",host="";
		request=scanner.nextLine();				//take request from the user
		host=scanner.nextLine();                //take host from the user
		host=host.split(" ")[1];
		//Checks the request type V V V V
		if (request.split("\n")[0].contains("GET")||request.split("\n")[0].contains("get")) 
		{
			GET_file(host,request);
			
		}
		else if( request.split("\n")[0].contains("POST")||request.split("\n")[0].contains("post"))
		{
			POST_file(host,request);
		}
		else
		{
			System.out.println("wrong request, please try again later");
		}
		
			
		scanner.close();
	}
	public static void POST_file(String host,String path)
	{
		try {
			host=host.substring(0, host.length()-4);    //remove \r\n from the end of the host string
			InetAddress ip1=InetAddress.getByName(host);  //get host IP address
			Socket Client=new Socket(ip1,80);            //create client socket on port 80 for http, with hpst IP address destination
			DataInputStream input = new DataInputStream(Client.getInputStream());
			DataOutputStream output = new DataOutputStream(Client.getOutputStream());
			Scanner scanner = new Scanner(System.in);
			String httpversion=path.split(" ")[2];      //get http version from the previous input
			httpversion=httpversion.substring(0, httpversion.length()-4); //remove \r\n from the end of http version
			System.out.println(httpversion);
			path=path.split("/")[1].split(" ")[0];          //gets URL(filename)
			
			while(true)		//for signin and signup
			{
				System.out.println("1- sign in");
				System.out.println("2- sign up");
				String client_choice;
				client_choice= scanner.nextLine();
				output.writeUTF(client_choice);
				if(client_choice.equalsIgnoreCase("1")) 		//signin
			
				{
					while (true)
					{
						String serverASK = input.readUTF();
						System.out.println( serverASK);                   //enter username message
						String username="",password="";
						username =scanner.nextLine();
						output.writeUTF(username);
						serverASK = input.readUTF();
						System.out.println( serverASK);                   //enter password message
						password=scanner.nextLine();
						output.writeUTF(password);
						serverASK = input.readUTF();
						if(serverASK.equalsIgnoreCase("connected"))
						{
							break;
						}
						System.out.println("You have entered an invalid username and password, Please try again");
			
					}
					break;
				}
				else if(client_choice.equalsIgnoreCase("2"))		//signup
				{
					String serverASK = input.readUTF();
					System.out.println( serverASK);
					String username="",password="";
					username =scanner.nextLine();
					output.writeUTF(username);
					serverASK = input.readUTF();
					System.out.println( serverASK);
					password=scanner.nextLine();
					output.writeUTF(password);
					serverASK = input.readUTF();
					System.out.println(serverASK);
					break;
				}
				else
				{
					System.out.println("you have entered an invalid input please try again");
				}
			}
			System.out.println("______________________________________");
			System.out.println("Connected");
			System.out.println("______________________________________");
			BufferedWriter file_request=new BufferedWriter(new OutputStreamWriter(Client.getOutputStream()));								//to send POST request to server
			BufferedReader file_response=new BufferedReader(new InputStreamReader(Client.getInputStream()));//to get the response of the POST from the server
			String Wfile ="C:\\java\\"+path;																//directory for openning the file
			BufferedWriter writer = new BufferedWriter(new FileWriter(Wfile));								//to write to new file
			file_request.write("POST /" + path + " "+httpversion+"\n");
			file_request.write("Host: " + host + "\r\n");
			file_request.flush();    //flushes the output stream to send the request to the server
			System.out.println("Request Sent!");
			System.out.println("______________________________________");
			String responseLine,file_path="";
			file_path=file_response.readLine();
			int x=0;
			System.out.println(file_path);
			while ((responseLine = file_response.readLine()) != null) {
				if(x>3)   // to skip the response header
					writer.write(responseLine+"\n");		//to write to the file
				System.out.println(responseLine);
				x++;
			}
			writer.close();
			
			if(!file_path.equalsIgnoreCase("not found"))                                // checks the server for the file
			{
				File file =new File(Wfile);
				try {
				Desktop.getDesktop().open( file );								//to open file window
				}
				catch(IOException e)
				{
					System.out.println("failed to open");
				}
			}
			System.out.println("______________________________________");
			System.out.println("Response Recieved!!");
			System.out.println("______________________________________");
			file_request.close();
			file_response.close();
			Client.close();
			scanner.close();
		}
		catch(Exception e)
		{
			System.err.println("error");
		}

	}


}
