package es.florida.aev05;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import com.sun.net.httpserver.HttpServer;


public class ServidorHTTP {
	
	//	Métode: main
	//	Descripció: Creamos el servidor
	//	Parametres d'entrada: ningún
	//	Parametres d'eixida: ningún
	public static void main(String[] args) throws Exception {
		String host = "127.0.0.1"; //127.0.0.1
		int puerto = 7777;
		InetSocketAddress direccionTCPIP = new InetSocketAddress(host,puerto);
		int backlog = 0;
		HttpServer servidor = HttpServer.create(direccionTCPIP, backlog);
		GestorHTTP gestorHTTP = new GestorHTTP();
		String rutaRespuesta = "/estufa";
		servidor.createContext(rutaRespuesta, gestorHTTP);
		//Opcion 1 de ejecucion: no multihilo
		// servidor.setExecutor(null);
		//Opcion 2 de ejecucion: multihilo con ThreadPoolExecutor
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
		servidor.setExecutor(threadPoolExecutor);
		servidor.start();
		System.out.println("Servidor HTTP arranca en el puerto " + puerto);
		}

}
