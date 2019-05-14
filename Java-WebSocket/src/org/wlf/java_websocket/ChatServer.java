package org.wlf.java_websocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;

public class ChatServer extends WebSocketServer {

	private static ConcurrentHashMap<String, ChatServer> webSocketSet = new ConcurrentHashMap<String, ChatServer>();
	private String ConnectString =null;
	
	public ChatServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public ChatServer(InetSocketAddress address) {
		super(address);
	}
	

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		
		try {
			ConnectString=URLDecoder.decode(handshake.getFieldValue("id"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendToAll(conn.getRemoteSocketAddress()
				+ConnectString+" 进入房间 ！");

		System.out.println(conn.getRemoteSocketAddress().getAddress()
				.getHostAddress()+ConnectString
				+ " 进入房间 ！");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {

		sendToAll(conn.getRemoteSocketAddress().getAddress().getHostAddress()
				+ " 离开房间 ！");

		System.out.println(conn.getRemoteSocketAddress().getAddress()
				.getHostAddress()
				+ " 离开房间 ！");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		String[] strs =message.split("//");
		
		if(strs.length==2) {
			sendToAll("["
				+ conn.getRemoteSocketAddress().getAddress().getHostAddress()
				+ "]" + strs[0]+"向所有人说"+strs[1]);

		System.out.println("["
				+ conn.getRemoteSocketAddress().getAddress().getHostAddress()
				+ "]" + message);
		}else if (strs.length==3) {
		} 
		
	}

	@Override
	public void onError(WebSocket conn, Exception e) {
		e.printStackTrace();
		if (conn != null) {
			conn.close();
		}
	}

	// 发送给所有的聊天者
	private void sendToAll(String text) {
		Collection<WebSocket> conns = connections();
		synchronized (conns) {
			for (WebSocket client : conns) {
				client.send(text);
			}
		}
	}
	
	// 发送给指定的聊天者
		private void sendTo(String name,String text) {
			Collection<WebSocket> conns = connections();
			synchronized (conns) {
				for (WebSocket client : conns) {
				}
			}
		}

	// 测试
	public static void main(String[] args) throws InterruptedException,
			IOException {

		int port = 8887;

		ChatServer server = new ChatServer(port);
		server.start();

		System.out.println("房间已开启，等待客户端接入，端口号: " + server.getPort());

		BufferedReader webSocketIn = new BufferedReader(new InputStreamReader(
				System.in));

		while (true) {
			String stringIn = webSocketIn.readLine();
			server.sendToAll(stringIn);
		}
	}
}
