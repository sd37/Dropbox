package com.dropbox.client;

import com.dropbox.common.protocol.DropboxClientProtocol;
import com.dropbox.common.util.DropboxConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.Socket;

public class DropboxClient implements FileSynchronizationClient {
  private static final Log LOG = LogFactory.getLog(DropboxClient.class);
  private static final int SYNC_PERIOD_MIN = Integer.parseInt(System.getProperty("sync.period.min",
      "1"));
  private static final String DROPBOX_SERVER = System.getProperty("server",
      DropboxConstants.DROPBOX_DEFAULT_SERVER_HOST);
  private static final int DROPBOX_SERVER_PORT = Integer.parseInt(System.getProperty("port",
      DropboxConstants.DROPBOX_DEFAULT_SERVER_PORT));
  private static final String CLIENT_PATH = System.getProperty("client.home",
      "/tmp/client1/");
  private static final String CLIENT_ID = System.getProperty("client.id", "client1");
  private DropboxClientProtocol protocol;

  public static void main(String[] args) {
    DropboxClient client = null;
    client = new DropboxClient();
    client.run();
  }

  @Override
  public void run() {
    while (sync()) {
      try {
        Thread.sleep(SYNC_PERIOD_MIN * DropboxConstants.TIME_MIN);
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

    try {
      clientSock = new Socket(DROPBOX_SERVER, DROPBOX_SERVER_PORT);
      protocol = new DropboxClientProtocol(clientSock);
    } catch (IOException e) {
      throw new RuntimeException("Could not establish connection with the server", e);
    }

    try {
      protocol.writeString(DropboxConstants.CLIENT_SANITY_STRING);
      protocol.writeString(CLIENT_ID);
      protocol.writeDir(CLIENT_PATH);
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

}
