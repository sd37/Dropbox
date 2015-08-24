package com.dropbox.common;

import com.dropbox.common.util.DropboxUtil;
import org.junit.Assert;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DropboxProtocol {
  private Socket sock;
  private DataInputStream dataInputStream;
  private DataOutputStream dataOutputStream;

  public DropboxProtocol(Socket sk) throws IOException {
    Assert.assertNotNull(sk);
    sock = sk;
    dataInputStream = new DataInputStream(sk.getInputStream());
    dataOutputStream = new DataOutputStream(sk.getOutputStream());
  }

  public String readString() throws IOException {
    int len = dataInputStream.readUnsignedShort();
    Assert.assertTrue(len > 0);
    byte[] buf = new byte[len];
    dataInputStream.readFully(buf);
    return new String(buf);
  }

  public void writeString(String s) throws IOException {
    Assert.assertNotNull(s);
    dataOutputStream.writeShort(s.length());
    byte[] data = s.getBytes("UTF-8");
    dataOutputStream.write(data);
  }

  public DropBoxFile readFileServer(String serverPath) throws IOException {
    Assert.assertNotNull(serverPath);
    String filePath = readString();
    String newPath = DropboxUtil.constructClientPath(filePath);
    long len = dataInputStream.readLong();

    // write file to disk for the client.
    String fullPathToFile = serverPath + newPath;
    Path pathToFile = Paths.get(fullPathToFile);
    Files.createDirectories(pathToFile.getParent());
    FileOutputStream out = new FileOutputStream(pathToFile.toString());
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream))) {
      while (true) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        out.write(line.getBytes());
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Error in writing file to disk", ioe);
    }
    out.close();
    return new DropBoxFile(newPath, len);
  }

  public void writeFile(DropBoxFile f) throws IOException {
    Assert.assertNotNull(f);
    writeString(f.getFullPath());
    dataOutputStream.writeLong(f.getLen());

    InputStream in = Files.newInputStream(Paths.get(f.getFullPath()));
    InputStreamReader inputStreamReader = new InputStreamReader(in);
    BufferedReader reader = new BufferedReader(inputStreamReader);
    while (true) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      dataOutputStream.write(line.getBytes());
    }
  }
}
