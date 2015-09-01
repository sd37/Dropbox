package com.dropbox.common.protocol;


import com.dropbox.common.dirs.DropboxDirClient;
import com.dropbox.common.files.DropboxFileClient;
import com.dropbox.common.util.DropboxConstants;
import com.dropbox.common.util.DropboxUtil;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DropboxClientProtocol extends DropboxProtocol {
  public DropboxClientProtocol(Socket sk) throws IOException {
    super(sk);
  }

  public void writeFile(String filePath) throws IOException {
    Assert.assertNotNull(filePath);
    DropboxFileClient f = new DropboxFileClient(filePath);
    objectOutputStream.writeObject(f);

    InputStream in = Files.newInputStream(Paths.get(f.getFullPath()));
    BufferedInputStream bufIn = new BufferedInputStream(in);

    byte[] buf = new byte[DropboxConstants.BUF_SIZE_BYTES];
    while (true) {
      int readBytes = bufIn.read(buf, 0, buf.length);
      if (readBytes < 0) {
        break;
      }

      byte[] actualBuf = Arrays.copyOfRange(buf, 0, readBytes);
      byte[] encodedBuf = Base64.encodeBase64(actualBuf);
      DropboxUtil.writeByteBuffer(encodedBuf, outputStream);
    }
    bufIn.close();
  }

  public void writeDir(String dirPath) throws IOException {
    Assert.assertNotNull(dirPath);
    DropboxDirClient d = new DropboxDirClient(dirPath);

    objectOutputStream.writeObject(d);

    List<DropboxFileClient> clientFiles = d.getClientFiles();

    for (DropboxFileClient cf : clientFiles) {
      writeFile(cf.getFullPath());
    }
  }
}
