package com.dropbox.common.protocol;

import com.dropbox.common.dirs.DropboxDirClient;
import com.dropbox.common.dirs.DropboxDirServer;
import com.dropbox.common.files.DropboxFileClient;
import com.dropbox.common.files.DropboxFileServer;
import com.dropbox.common.util.DropboxConstants;
import com.dropbox.common.util.DropboxUtil;
import org.junit.Assert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DropboxServerProtocol extends DropboxProtocol {
  public DropboxServerProtocol(Socket sk) throws IOException {
    super(sk);
  }

  public DropboxFileServer readFile() throws IOException, ClassNotFoundException {
    DropboxFileClient clientFile = (DropboxFileClient) objectInputStream.readObject();
    String clientPath = DropboxUtil.constructClientPath(clientFile.getFullPath());
    String serverFilePath = DropboxConstants.DEFAULT_DROPBOX_SERVER_DIR
        + clientPath;

    Path pathToFile = Paths.get(serverFilePath);
    Files.createDirectories(pathToFile.getParent());
    FileOutputStream out = new FileOutputStream(pathToFile.toString());

    int totalReadBytes = 0;
    try {
      while (totalReadBytes < clientFile.getLen()) {
        int b = inputStream.read();
        if (b < 0) {
          break;
        }
        out.write(b);
        totalReadBytes++;
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Error in writing file to disk", ioe);
    }

    out.close();
    Assert.assertEquals(clientFile.getLen(), totalReadBytes);

    return new DropboxFileServer(serverFilePath, clientFile.getFullPath());
  }

  public DropboxDirServer readDir() throws IOException, ClassNotFoundException {
    DropboxDirClient clientDir = (DropboxDirClient) objectInputStream.readObject();
    String serverDirPath = DropboxConstants.DEFAULT_DROPBOX_SERVER_DIR
        + DropboxUtil.constructClientPath(clientDir.getFullPath());

    Path pathToDir = Paths.get(serverDirPath);
    Files.createDirectories(pathToDir.getParent());

    List<DropboxFileServer> serverFiles = new ArrayList<>();
    for (int i = 0; i < clientDir.getNumberOfFiles(); i++) {
      serverFiles.add(readFile());
    }

    return new DropboxDirServer(clientDir.getFullPath(), serverDirPath, serverFiles);
  }
}
