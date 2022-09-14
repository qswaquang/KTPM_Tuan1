package test;

import java.util.Date;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;
import data.Person;
import helper.XMLConvert;

public class QueueSender {
	public static void main(String[] args) throws Exception {
//config environment for JMS
		BasicConfigurator.configure();
//config environment for JNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
//create context
		Context ctx = new InitialContext(settings);
//lookup JMS connection factory
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
//lookup destination. (If not exist-->ActiveMQ create once)
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
//get connection using credential
		Connection con = factory.createConnection("admin", "admin");
//connect to MOM
		con.start();
//create session
		Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
//create producer
		MessageProducer producer = session.createProducer(destination);
//create text message
		
		
		JFrame a = new JFrame("Sender");

		JButton b = new JButton("send");
		
		JTextField mess = new JTextField(); 
		JLabel label = new JLabel("input name");
		label.setBounds(40,50, 85,20);
		b.setBounds(40,90,85,20);
		mess.setBounds(40,70,200,20);
		
		a.add(label);
		a.add(mess);
		a.add(b);
		a.setSize(300,300);
		a.setLayout(null);
		a.setVisible(true);
		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		b.addActionListener(e -> {
			String xml;
			try {
				Message msg = session.createTextMessage("hello mesage from ActiveMQ");
				producer.send(msg);
				Person p = new Person(1001, mess.getText(), new Date());
				xml = new XMLConvert<Person>(p).object2XML(p);
				msg = session.createTextMessage(xml);
				producer.send(msg);
		//shutdown connection
				session.close();
				con.close();
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
		
		
		System.out.println("Finished...");
	}
}
