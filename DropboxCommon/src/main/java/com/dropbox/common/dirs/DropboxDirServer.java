package com.dropbox.common.dirs;

import com.dropbox.common.files.DropboxFileServer;
import org.junit.Assert;

import java.io.File;
import java.util.List;

public class DropboxDirServer {
  private String clientDirPath;
  private String serverDirPath;
  private long size;
  private int numberOfFiles;
  private List<DropboxFileServer> serverFiles;

  public DropboxDirServer(String clientDirPath, String serverDirPath, List<DropboxFileServer> serverFiles) {
    Assert.assertNotNull(clientDirPath);
    Assert.assertNotNull(serverDirPath);
    Assert.assertNotNull(serverFiles);

    this.clientDirPath = clientDirPath;
    this.serverDirPath = serverDirPath;
    this.serverFiles = serverFiles;
    this.size = new File(serverDirPath).length();
    this.numberOfFiles = this.serverFiles.size();
  }

  public long getSize() {
    return this.size;
  }

  public int getNumberOfFiles() {
    return this.numberOfFiles;
  }

  public List<DropboxFileServer> getServerFiles() {
    return this.serverFiles;
  }

  @Override
  public String toString() {
    return "Read Dir = " + clientDirPath + "\n" + "Size = " + getSize() + " Number of Files = "
        + getNumberOfFiles();
  }
}
