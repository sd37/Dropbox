package com.dropbox.server;

import com.dropbox.common.dirs.DropboxDirServer;
import com.dropbox.common.protocol.DropboxServerProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: At the current moment if the server crashes everything is lost. The clients have
 * to sync with a new instance of the server.
 */

public class DropboxServer implements FileSynchronizationServer {
  private static final Log LOG = LogFactory.getLog(DropboxServer.class);
  private static final int SERVER_PORT = Integer.parseInt(System.getProperty("port", "8945"));
  private static final int NUM_WORKER_THREADS = Integer.parseInt(System.getProperty("threads",
      "2"));
  private ServerSocket serverSocket;
  private ExecutorService pool;
  private static final Object lockObject = new Object();

  public class ClientHandler implements Callable {
    private Socket clntSock;

    public ClientHandler(Socket clntSock) {
      Assert.assertNotNull(clntSock);
      this.clntSock = clntSock;
    }

    private void clientHandler() throws IOException {
      Assert.assertNotNull(clntSock);
      SocketAddress clientAddr = clntSock.getRemoteSocketAddress();
      LOG.info("Thread = " + Thread.currentThread() + " serving Client = " + clientAddr);
      DropboxDirServer f = null;
      try {
        DropboxServerProtocol protocol = new DropboxServerProtocol(clntSock, lockObject);
        f = protocol.readDir();
        LOG.info("File Read = " + f.toString());
      } catch (IOException | ClassNotFoundException ioe) {
        LOG.error("Could not read file from the client = " + clientAddr, ioe);
      } finally {
        clntSock.close();
      }
      LOG.info("Served client = " + clientAddr);
    }

    @Override
    public Boolean call() throws IOException {
      clientHandler();
      return new Boolean(true);
    }
  }


  private DropboxServer() throws IOException {
    serverSocket = new ServerSocket(SERVER_PORT);
    pool = Executors.newFixedThreadPool(NUM_WORKER_THREADS);
  }


  private void listenOnce() throws IOException {
    Socket clntSock = serverSocket.accept();
    pool.submit(new ClientHandler(clntSock));
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
