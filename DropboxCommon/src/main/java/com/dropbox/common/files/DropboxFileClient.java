package com.dropbox.common.files;

import com.dropbox.common.util.DropboxUtil;
import org.junit.Assert;

import java.io.*;
import java.nio.file.Path;

public class DropboxFileClient implements Serializable {
  private static final long serialVersionUID = 42L;
  private String clientFilePath;
  private long len;

  public DropboxFileClient(String clientFilePath) {
    Assert.assertNotNull(clientFilePath);
    this.clientFilePath = clientFilePath;
    this.len = new File(clientFilePath).length();
  }

  public DropboxFileClient(Path clientFilePath) {
    Assert.assertNotNull(clientFilePath);
    File clientFile = clientFilePath.toFile();
    this.clientFilePath = clientFile.getAbsolutePath();
    this.len = clientFile.length();
  }

  public String getFullPath() {
    return this.clientFilePath;
  }

  public long getLen() {
    return this.len;
  }

  @Override
  public String toString() {
    return getFullPath() + "\n" + "Length : " + getLen();
  }

  private void writeObject(ObjectOutputStream out)
      throws IOException {
    Assert.assertNotNull(out);
    DropboxUtil.writeString(getFullPath(), out);
    out.writeLong(getLen());
  }

  private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    Assert.assertNotNull(in);
    this.clientFilePath = DropboxUtil.readString(in);
    this.len = in.readLong();
  }

  private void readObjectNoData()
      throws ObjectStreamException {
    throw new RuntimeException("Unsupported operation : Unsupported operation.");
  }
}
