package com.yychat.view;//包名，作用管理类

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.*;

import com.yychat.controller.ClientConnetion;
import com.yychat.model.Message;
import com.yychat.model.User;
//1、在类中实现动作监听器接口
public class ClientLogin extends JFrame implements ActionListener{//类名：ClientLogin,继承
	public static HashMap hmFriendList=new HashMap<String,FriendList>();//定义保存FriejdList对象的HashMap
	//北部的组件
	JLabel jlbl1;
	
	//定义中部的组件
	JTabbedPane jtp1;//选项卡组件
	JPanel jp2,jp3,jp4;	
	JLabel jlbl2,jlbl3,jlbl4,jlbl5;
	JTextField jtf1;//文本框
	JPasswordField jpf1;//密码框
	JButton jb4;
	JCheckBox jcb1,jcb2;	
	
	//定义南部的组件
	JButton jb1,jb2,jb3;
	JPanel jp1;
	
	public  ClientLogin(){//构造方法，初始化对象
		//创建北部组件
		jlbl1=new JLabel(new ImageIcon("images/tou.gif"));//标签对象
		this.add(jlbl1,"North");//this表示对象本身
		
		//创建中部组件
		jtp1=new JTabbedPane();
		jp2=new JPanel(new GridLayout(3,3));//网格布局
		jp3=new JPanel();jp4=new JPanel();//实例化生成对像，构造方法
		jlbl2=new JLabel("YY号码",JLabel.CENTER);jlbl3=new JLabel("YY密码",JLabel.CENTER);
		jlbl4=new JLabel("忘记密码",JLabel.CENTER);
		jlbl4.setForeground(Color.BLUE);		
		jlbl5=new JLabel("申请密码保护",JLabel.CENTER);
		jtf1=new JTextField();
		jpf1=new JPasswordField();
		jb4=new JButton(new ImageIcon("images/clear.gif"));
		jcb1=new JCheckBox("隐身登录");jcb2=new JCheckBox("记住密码");		
		jp2.add(jlbl2);jp2.add(jtf1);jp2.add(jb4);
		jp2.add(jlbl3);jp2.add(jpf1);jp2.add(jlbl4);
		jp2.add(jcb1);jp2.add(jcb2);jp2.add(jlbl5);	
		
		jtp1.add(jp2,"YY号码");jtp1.add(jp3,"手机号码");jtp1.add(jp4,"电子邮箱");
		this.add(jtp1);		
		
		//创建南部组件
		jb1=new JButton(new ImageIcon("images/denglu.gif"));
		jb1.addActionListener(this);//2、添加监听器
		jb2=new JButton(new ImageIcon("images/zhuce.gif"));
		jb3=new JButton(new ImageIcon("images/quxiao.gif"));
		jp1=new JPanel();
		jp1.add(jb1);jp1.add(jb2);jp1.add(jb3);
		this.add(jp1,"South");
		
		this.setSize(350,240);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//用途？
		this.setLocationRelativeTo(null);
		this.setVisible(true);	
		
	}
	public static void main(String[] args) {		
		ClientLogin cl=new ClientLogin();//新创建对象，引用变量
		//clientLogin=null;//对象就会被垃圾回收器回收

	}
	@Override
	public void actionPerformed(ActionEvent arg0) {//3、添加事件处理代码
		if(arg0.getSource()==jb1) {
			String userName=jtf1.getText(); 
			String passWord=new String(jpf1.getPassword());
			User user=new User();
			user.setUserName(userName);
			user.setPassWord(passWord);			
			//密码验证，密码是123456验证成功，否则验证失败
			Message mess=new ClientConnetion().loginValidate(user);
			if(mess.getMessageType().equals(Message.message_LoginSuccess)){
				FriendList friendList=new FriendList(userName);
				//保存FriendList
				hmFriendList.put(userName, friendList);
				
				//第一步：
				Message mess1=new Message();
				mess1.setSender(userName);
				mess1.setReceiver("Server");
				mess1.setMessageType(Message.message_RequestOnlineFriend);
				Socket s=(Socket)ClientConnetion.hmSocket.get(userName);
				ObjectOutputStream oos;
				try{
					oos=new ObjectOutputStream(s.getOutputStream());
					oos.writeObject(mess1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				this.dispose();
			}else{
				JOptionPane.showMessageDialog(this, "密码错误");
			}			
			
		}
				
		
	}

}
