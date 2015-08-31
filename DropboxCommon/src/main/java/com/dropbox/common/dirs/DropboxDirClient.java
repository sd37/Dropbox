package com.dropbox.common.dirs;

import com.dropbox.common.files.DropboxFileClient;
import com.dropbox.common.util.DropboxUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DropboxDirClient implements Serializable {
  private static final long serialVersionUID = 62L;
  private String clientDirPath;
  private long size;
  private int numberOfFiles;

  public DropboxDirClient(String clientDirPath) throws IOException {
    Assert.assertNotNull(clientDirPath);
    this.clientDirPath = clientDirPath;
    Path dirPath = Paths.get(this.clientDirPath);
    this.size = FileUtils.sizeOfDirectory(dirPath.toFile());
    this.numberOfFiles = getClientFiles().size();
  }

  public long getSize() {
    return this.size;
  }

  public int getNumberOfFiles() {
    return this.numberOfFiles;
  }

  public String getFullPath() {
    return this.clientDirPath;
  }

  public List<DropboxFileClient> getClientFiles() throws IOException {
    List<DropboxFileClient> clientFiles = new ArrayList<>();

    DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(this.clientDirPath));
    for (Path f : stream) {
      clientFiles.add(new DropboxFileClient(f));
    }
    return clientFiles;
  }

  private void writeObject(ObjectOutputStream out)
      throws IOException {
    Assert.assertNotNull(out);
    DropboxUtil.writeString(getFullPath(), out);
    out.writeLong(getSize());
    out.writeInt(numberOfFiles);
  }

  private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    Assert.assertNotNull(in);
    this.clientDirPath = DropboxUtil.readString(in);
    this.size = in.readLong();
    this.numberOfFiles = in.readInt();
  }

  private void readObjectNoData()
      throws ObjectStreamException {
    throw new RuntimeException("Unsupported operation : Unsupported operation.");
  }
}
