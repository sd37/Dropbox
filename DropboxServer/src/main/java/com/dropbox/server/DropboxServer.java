package com.dropbox.server;

import com.dropbox.common.dirs.DropboxDirServer;
import com.dropbox.common.protocol.DropboxServerProtocol;
import com.dropbox.common.util.DropboxConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

import java.io.File;
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
  private static final Log LOG = LogFactory.getLog(DropboxServer.class);
  private static final int SERVER_PORT = Integer.parseInt(System.getProperty("port", "8945"));
  private ServerSocket serverSocket;
  private Map<String, String> clientToPath;

  private DropboxServer() throws IOException {
    clientToPath = new HashMap<>();
    serverSocket = new ServerSocket(SERVER_PORT);
  }

  public static void main(String[] args) throws IOException {
    DropboxServer server = new DropboxServer();
    server.listen();
  }

  private void listenOnce() throws IOException {
    Socket clntSock = serverSocket.accept();
    SocketAddress clientAddr = clntSock.getRemoteSocketAddress();
    LOG.info("Serving Client = " + clientAddr);
    DropboxServerProtocol protocol = new DropboxServerProtocol(clntSock);
    DropboxDirServer f = null;
    String clientId = null;
    try {
      if (!DropboxConstants.CLIENT_SANITY_STRING.equals(protocol.readString())) {
        LOG.error("client = " + clientAddr + " Does not follow protocol." + "Closing clntSock = "
            + clntSock);
        clntSock.close();
        return;
      }
      clientId = protocol.readString();
      Assert.assertNotNull(clientId);
      clientToPath.put(clientId, DropboxConstants.DEFAULT_DROPBOX_SERVER_DIR + "/" + clientId);

      // Delete the corresponding clientId dir .. because the client is trying to sync.
      FileUtils.deleteDirectory(new File(clientToPath.get(clientId)));

      f = protocol.readDir();
      LOG.info("File Read = " + f.toString());
    } catch (IOException | ClassNotFoundException ioe) {
      LOG.error("Could not read file from the client = " + clientId, ioe);
    } finally {
      clntSock.close();
    }
    LOG.info("Served client = " + clientAddr);
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
}
