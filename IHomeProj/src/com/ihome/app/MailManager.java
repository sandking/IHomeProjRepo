package com.ihome.app;

import javax.activation.*;
import javax.mail.*;
import javax.mail.event.TransportListener;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MailManager
{
	private MimeMessage message;
	private Session session;
	private Transport transport;

	private String mailHost = "";
	private String sender_username = "";
	private String sender_password = "";

	private Properties properties = new Properties();

	private TransportListener mTransportListener;

	public MailManager(TransportListener tl, boolean debug)
	{
		initMailInfor();
		mTransportListener = tl;
		session = Session.getInstance(properties);
		session.setDebug(debug);// 开启后有调试信息
		message = new MimeMessage(session);
	}

	public void doSendHtmlEmail(String subject, String sendContent, String receiveUser, File[] attachments)
	{
		try
		{
			// 发件人
			InternetAddress from = new InternetAddress(sender_username);
			message.setFrom(from);

			// 收件人
			InternetAddress to = new InternetAddress(receiveUser);
			message.setRecipient(Message.RecipientType.TO, to);

			// 邮件主题
			message.setSubject(subject);

			// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
			Multipart multipart = new MimeMultipart();

			// 添加邮件正文
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(sendContent, "text/html;charset=UTF-8");
			multipart.addBodyPart(contentPart);

			// 添加附件的内容
			if (attachments != null)
			{
				for (File file : attachments)
				{
					BodyPart attachmentBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(file);
					attachmentBodyPart.setDataHandler(new DataHandler(source));
					attachmentBodyPart.setFileName(MimeUtility.encodeWord(file.getName()));
					multipart.addBodyPart(attachmentBodyPart);
				}
			}

			// 将multipart对象放到message中
			message.setContent(multipart);

			// 保存邮件
			message.saveChanges();


			transport = session.getTransport("smtp");
			// smtp验证，就是你用来发邮件的邮箱用户名密码

			transport.addTransportListener(mTransportListener);

			transport.connect(mailHost, sender_username, sender_password);

			// 发送
			MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
			CommandMap.setDefaultCommandMap(mc);

			transport.sendMessage(message, message.getAllRecipients());

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (transport != null)
			{
				try
				{
					transport.close();
				} catch (MessagingException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void initMailInfor()
	{
		InputStream in = null;
		try
		{
			in = MailManager.class.getResourceAsStream("MailServer.properties");

			this.properties.load(in);
			this.mailHost = properties.getProperty("mail.smtp.host");
			this.sender_username = properties.getProperty("mail.sender.username");
			this.sender_password = properties.getProperty("mail.sender.password");

		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				in.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
