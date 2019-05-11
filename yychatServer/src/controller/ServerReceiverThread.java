package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.yychat.model.Message;

public class ServerReceiverThread extends Thread{//必须要有run()方法
	Socket s;
	HashMap hmSocket;
	String Sender;
	ObjectOutputStream oos;
	
	public ServerReceiverThread(Socket s,HashMap hmSocket){
		this.s=s;
		this.hmSocket=hmSocket;		
	}
	
	public void run(){		
		ObjectInputStream ois;
		while(true){
			try {
				//接收Message信息
				ois=new ObjectInputStream(s.getInputStream());
				Message mess=(Message)ois.readObject();//接收用户发送来的聊天信息，阻塞，10个用户，100毫秒
				System.out.println("等待用户发送聊天信息");
				System.out.println(mess.getSender()+"对"+mess.getReceiver()+"说:"+mess.getContent());
				Sender=mess.getSender();
				
				//转发Message信息
				if(mess.getMessageType().equals(Message.message_Common)){
					Socket s1=(Socket)hmSocket.get(mess.getReceiver());
					ObjectOutputStream oos=new ObjectOutputStream(s1.getOutputStream());
					oos.writeObject(mess);//转发Message
					System.out.println("服务器转发了信息"+mess.getSender()+"对"+mess.getReceiver()+"说:"+mess.getContent());
				}
				
				//第二步
				if(mess.getMessageType().equals(Message.message_RequestOnlineFriend)){
					//首先要拿到在线好友信息
					Set friendSet=StartServer.hmSocket.keySet();//得到好友名字集合
					Iterator it=friendSet.iterator();//迭代器，目的是对friendSet集合中的元素进行遍历
					String friendName;
					String friendString="";
					while(it.hasNext()){//判断还有没有下一个好友
						friendName=(String)it.next();//获取下一个好友的名字
						if(!friendName.equals(mess.getSender()))//排除自己
							System.out.println("有执行该代码");
							friendString=friendString+friendName+" ";
					}
					System.out.println("全部好友的名字"+friendString);
					
					//给mess赋值
					mess.setContent(friendString);
					mess.setReceiver(Sender);
					mess.setSender("Server");
					mess.setMessageType(mess.message_OnlineFriend);
					
					//发送mess
					Socket s1=(Socket)hmSocket.get(Sender);
					sendMessage(s1,mess);
					
				}
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}		
		
	}

	private void sendMessage(Socket s, Message mess) throws IOException{
		oos=new ObjectOutputStream(s.getOutputStream());
		oos.writeObject(mess);
		
	}
	
}
