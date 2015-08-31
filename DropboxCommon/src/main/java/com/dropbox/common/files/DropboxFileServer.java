package com.dropbox.common.files;


import org.junit.Assert;

import java.io.File;

public class DropboxFileServer {
  private String clientPath;
  private String serverPath;
  private long len;

  public DropboxFileServer(String serverPath, String clientPath) {
    Assert.assertNotNull(serverPath);
    Assert.assertNotNull(clientPath);
    this.clientPath = clientPath;
    this.serverPath = serverPath;
    this.len = new File(serverPath).length();
  }

  public String getClientPath() {
    return clientPath;
  }

  public String getServerPath() {
    return serverPath;
  }

  public long getLen() {
    return len;
  }

  @Override
  public String toString() {
    return "Client Path = " + getClientPath() + ", " + "Server Path = " + getServerPath() + ", "
        + "Length = " + getLen();
  }
}
