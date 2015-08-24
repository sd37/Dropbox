package com.dropbox.server;

import com.dropbox.common.DropBoxFile;
import com.dropbox.common.DropboxProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: At the current moment if the server crashes everything is lost. The clients have
 * to sync with a new instance of the server.
 */

public class DropboxServer implements FileSynchronizationServer {
  public static final String SERVER_FOLDER = System.getProperty("server.folder", "/tmp/server");
  private static final Log LOG = LogFactory.getLog(DropboxServer.class);
  private ServerSocket serverSocket;
  private static final int SERVER_PORT = Integer.parseInt(System.getProperty("port", "8945"));
  private Map<String, String> clientToPath;

  private DropboxServer() throws IOException {
    clientToPath = new HashMap<>();
    serverSocket = new ServerSocket(SERVER_PORT);
  }

  private void listenOnce() throws IOException {
    Socket clntSock = serverSocket.accept();
    SocketAddress clientAddr = clntSock.getRemoteSocketAddress();
    LOG.info("Serving Client = " + clientAddr);
    DropboxProtocol protocol = new DropboxProtocol(clntSock);
    DropBoxFile f = null;
    String clientId = null;
    try {
      clientId = protocol.readString();
      Assert.assertNotNull(clientId);
      clientToPath.put(clientId, SERVER_FOLDER + "/" + clientId);
      f = protocol.readFileServer(SERVER_FOLDER);
    } catch (IOException ioe) {
      LOG.error("Could not read file from the client = " + clientId);
      return;
    }
    LOG.info("File Read = " + f.toString());
  }

  @Override
  public void listen() {
    while (true) {
      try {
        listenOnce();
      } catch (IOException e) {
        throw new RuntimeException("Server failed to listen", e);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    DropboxServer server = new DropboxServer();
    server.listen();
  }
}
