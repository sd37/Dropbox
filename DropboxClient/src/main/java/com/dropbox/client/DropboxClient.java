package com.dropbox.client;

import com.dropbox.common.DropBoxFile;
import com.dropbox.common.DropboxProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class DropboxClient implements FileSynchronizationClient {
  private static final Log LOG = LogFactory.getLog(DropboxClient.class);
  private static final String DROPBOX_SERVER = System.getProperty("server", "localhost");
  private static final int DROPBOX_SERVER_PORT = Integer.parseInt(System.getProperty("port",
      "8945"));
  private static final String CLIENT_PATH = System.getProperty("client.home",
      "/tmp/client1/README.md");
  private static final String CLIENT_ID = System.getProperty("client.id", "client1");
  private static final int TIME_MSEC = 1000;
  private static final int TIME_MIN = 60 * TIME_MSEC;
  private DropboxProtocol protocol;

  @Override
  public void run() {
    while (sync()) {
      try {
        Thread.sleep(10 * TIME_MIN);
      } catch (InterruptedException e) {
        LOG.error("Client got interrupted while sleeping..");
        Thread.currentThread().isInterrupted();
      }
    }
    throw new RuntimeException("Sync with server failed ..");
  }

  @Override
  public boolean sync() {
    LOG.info(String.format("Trying to sync files to DROPBOX_SERVER = %s.", DROPBOX_SERVER));

    Socket clientSock = null;
    long fileLength = new File(CLIENT_PATH).length();
    DropBoxFile file = new DropBoxFile(CLIENT_PATH, fileLength);

    try {
      clientSock = new Socket(DROPBOX_SERVER, DROPBOX_SERVER_PORT);
      protocol = new DropboxProtocol(clientSock);
    } catch (IOException e) {
      throw new RuntimeException("Could not establish connection with the server", e);
    }

    try {
      protocol.writeString(CLIENT_ID);
      protocol.writeFile(file);
    } catch (IOException e) {
      LOG.error("Could not write file to output stream..");
      return false;
    }

    try {
      clientSock.close();
    } catch (IOException e) {
      LOG.warn(String.format("%s close failed .. some data might not have written", clientSock));
      return false;
    }

    LOG.info(String.format("Sync with DROPBOX_SERVER = %s was successful.", DROPBOX_SERVER));
    return true;
  }

  public static void main(String[] args) {
    DropboxClient client = null;
    client = new DropboxClient();
    client.run();
  }

}
