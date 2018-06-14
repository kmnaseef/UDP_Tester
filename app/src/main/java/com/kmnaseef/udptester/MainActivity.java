package com.kmnaseef.udptester;

import android.app.*;
import android.os.*;
import java.net.*;
import android.widget.*;
import android.view.*;
import java.io.*;

public class MainActivity extends Activity 
{
		EditText sendip, sendport, sendmessage, hostip, hostport, listenport, rcvdmsg;
		Button sendbutton, clearbutton;
		DatagramSocket udpsocket;
		Handler handler;
		TextView localip;
		
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
				
				sendip = findViewById(R.id.sendip);
			  sendport = findViewById(R.id.sendport);
				sendbutton = findViewById(R.id.sendbutton);
				sendmessage = findViewById(R.id.sendmsg);
				hostip = findViewById(R.id.hostip);
				hostport= findViewById(R.id.hostport);
				listenport = findViewById(R.id.listenport);
				clearbutton = findViewById(R.id.clearbutton);
				rcvdmsg = findViewById(R.id.rcvdmsg);
				localip = findViewById(R.id.localip);
				
				try{
						udpsocket    = new DatagramSocket(7500);
						localip.setText(udpsocket.getLocalAddress().getHostAddress());
						handler = new Handler();
						receivethread receivethreadobj = new receivethread();
						Thread rcvthread = new Thread(receivethreadobj);
						rcvthread.start();
				}catch(Exception e){
						rcvdmsg.setText(e.getMessage());
				}
    }
		
		public void send(View View){
				try{
						String Message = sendmessage.getText().toString();
						byte[] msg = Message.getBytes();
						DatagramPacket sendpacket = new DatagramPacket(msg,msg.length);
						sendpacket.setAddress(InetAddress.getByName(sendip.getText().toString()));
						sendpacket.setPort(Integer.parseInt(sendport.getText().toString()));
						udpsocket.send(sendpacket);
				}catch(Exception e){
						rcvdmsg.setText(e.getMessage());
				}
		}
		public class receivethread implements Runnable
		{
				@Override
				public void run()
				{
					while(true){
						byte[] receivedmessage = new byte[1024];
						final DatagramPacket receivepacket = new DatagramPacket(receivedmessage,receivedmessage.length);
					  try
						{
								udpsocket.receive(receivepacket);
								handler.post(new Runnable(){
										@Override
										public void run(){
												rcvdmsg.setText(new String(receivepacket.getData()));
												hostip.setText((receivepacket.getAddress()).getHostAddress());
												hostport.setText(String.valueOf( receivepacket.getPort()));
												sendip.setText((receivepacket.getAddress()).getHostAddress());
												sendport.setText(String.valueOf( receivepacket.getPort()));
												}
								});
						}
						catch (Exception e)
						{
								final String s = e.getMessage();
								handler.post(new Runnable(){
										@Override
										public void run(){
												rcvdmsg.setText(s);
										}
								});
						}
					}
				}
				
		}
}
