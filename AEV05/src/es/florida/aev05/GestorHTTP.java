package es.florida.aev05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GestorHTTP implements HttpHandler{

	private Integer temperaturaActual = 15;
	private Integer temperaturaTermostato = 15;

	@Override
	//	Métode: main
	//	Descripció: Creamos el handle
	//	Parametres d'entrada: HttpExchange httpExchange
	//	Parametres d'eixida: ningún
	public void handle(HttpExchange httpExchange) throws IOException {
		String requestParamValue=null;
		if("GET".equals(httpExchange.getRequestMethod())) {
			requestParamValue = handleGetRequest(httpExchange);
			handleGETResponse(httpExchange,requestParamValue);
		} else if ("POST".equals(httpExchange.getRequestMethod())) {
			requestParamValue = handlePostRequest(httpExchange);
			handlePOSTResponse(httpExchange,requestParamValue);
		}
	}

	//	Métode: main
	//	Descripció: Creamos el handleGetRequest
	//	Parametres d'entrada: HttpExchange httpExchange
	//	Parametres d'eixida: url separada por el interrogante
	private String handleGetRequest(HttpExchange httpExchange) {
		return httpExchange.getRequestURI().toString().split("\\?")[1];
	}
	
	//	Métode: main
	//	Descripció: Creamos el handleGETResponse
	//	Parametres d'entrada: HttpExchange httpExchange, String requestParamValue
	//	Parametres d'eixida: ningún
	private void handleGETResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
		OutputStream outputStream = httpExchange.getResponseBody();
		if (requestParamValue.equals("temperaturaActual")) {
			String htmlResponse = "<html><body><p>Temperatura: "+temperaturaActual+"</p><p>Temperatura termostato: " + temperaturaTermostato+ "</p></body></html>";
			httpExchange.sendResponseHeaders(200, htmlResponse.length());
			outputStream.write(htmlResponse.getBytes());
			outputStream.flush();
		}
		outputStream.close();
	}
	
	//	Métode: main
	//	Descripció: Creamos el handlePostRequest
	//	Parametres d'entrada: HttpExchange httpExchange
	//	Parametres d'eixida: las lineas que escriben en postman
	private String handlePostRequest(HttpExchange httpExchange) {
		InputStream inputStream = httpExchange.getRequestBody();
		//Procesar lo que hay en inputStream, por ejemplo linea a linea y guardarlo todo en un string, que sera el que devuelve el metodo
		InputStreamReader isr = new InputStreamReader(inputStream);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String linea;
		try {
			while ((linea = br.readLine()) != null) {
				sb.append(linea);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	//	Métode: main
	//	Descripció: Creamos el handlePostResponse
	//	Parametres d'entrada: HttpExchange httpExchange, String requestParamValue
	//	Parametres d'eixida: ningún
	private void handlePOSTResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
		OutputStream outputStream = httpExchange.getResponseBody();
		String htmlResponse = requestParamValue;
		String[] valor1 = htmlResponse.split("=");
		if ((valor1[0]).equals("setTemperatura")) {
			httpExchange.sendResponseHeaders(200, htmlResponse.length());
			outputStream.write(htmlResponse.getBytes());
			outputStream.flush();
			outputStream.close();
			temperaturaTermostato = Integer.parseInt(valor1[1]);
			System.out.println("Ha cambiado la temperatura de termostato a: " + temperaturaTermostato);
			regularTemperatura();
		}else if ((valor1[0]).equals("notificarAveria:email_remitente")) {
			String hostEmail = "smtp.gmail.com";
			String portEmail = "587";
			String strAsunto = "AVERIA";
			String strMensaje = "HEMOS SUFRIDO UNA AVERIA!!!";
			String rutaImagen = "descarga.jpg";
			String rutaPdf = "AE5_T5_ServiciosRed.pdf";
			String emailTecnico = "mantenimientoinvernalia@gmail.com";
			String emailLordStarck = "megustaelfresquito@gmail.com";
			String email1 = "sergiomariscalmartinez72@gmail.com";
			String email2 = "semama02@floridauniversitaria.es";
			String[] emailDestino = {email1, email2};			
			String valor2[] = valor1[1].split(";");
			
//			CREDENCIALES DEL CORREO ELECTRONICO
//			String emailRemitente = "aev07add@gmail.com";
//			String emailRemitentePass = "SMM_2002";
			
			try {
				envioMail(strMensaje, strAsunto, valor2[0].toString(), valor1[2].toString(), hostEmail, portEmail, emailDestino, rutaImagen, rutaPdf);
			}catch (UnsupportedEncodingException | MessagingException e) {
				e.printStackTrace();
			}
		}


	}
	
	//	Métode: main
	//	Descripció: Creamos el regularTemperatura
	//	Parametres d'entrada: ningún
	//	Parametres d'eixida: ningún
	private void regularTemperatura() {
		if(temperaturaActual<temperaturaTermostato) {
			while (temperaturaActual != temperaturaTermostato) {
				temperaturaActual ++;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			while (temperaturaActual != temperaturaTermostato) {
				temperaturaActual --;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	//	Métode: main
	//	Descripció: Creamos el envioMail
	//	Parametres d'entrada: String mensaje, String asunto, String email_remitente, String email_remitente_pass,String host_email, String port_email, String[] email_destino, String[] anexo
	//	Parametres d'eixida: ningún
	public static void envioMail (String mensaje, String asunto, String email_remitente, String email_remitente_pass,String host_email, String port_email, String[] email_destino, String anexo1, String anexo2) throws UnsupportedEncodingException, MessagingException{	
		Properties props = System.getProperties();
		props.put("mail.smtp.host", host_email);
		props.put("mail.smtp.user", email_remitente);
		props.put("mail.smtp.clave", email_remitente_pass);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", port_email);
		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(email_remitente));
		for (int i = 0; i < email_destino.length; i++) {
			message.addRecipients(Message.RecipientType.TO, email_destino[i]);
		}
		message.setSubject(asunto);
		BodyPart messageBodyPart1 = new MimeBodyPart();
		messageBodyPart1.setText(mensaje);
		BodyPart messageBodyPart2 = new MimeBodyPart();
		DataSource src= new FileDataSource(anexo1);	
		messageBodyPart2.setDataHandler(new DataHandler(src));
		messageBodyPart2.setFileName(anexo1);
		BodyPart messageBodyPart3 = new MimeBodyPart();
		DataSource src2= new FileDataSource(anexo2);	
		messageBodyPart3.setDataHandler(new DataHandler(src2));
		messageBodyPart3.setFileName(anexo2);
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart1);
		multipart.addBodyPart(messageBodyPart2);
		multipart.addBodyPart(messageBodyPart3);
		message.setContent(multipart);
		Transport transport = session.getTransport("smtp");
		transport.connect(host_email, email_remitente, email_remitente_pass);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
}
