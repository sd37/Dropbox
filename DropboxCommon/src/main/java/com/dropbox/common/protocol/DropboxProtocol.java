package com.dropbox.common.protocol;

import org.junit.Assert;

import java.io.*;
import java.net.Socket;

public class DropboxProtocol {
  protected Socket sock;
  protected InputStream inputStream;
  protected OutputStream outputStream;
  protected ObjectInputStream objectInputStream;
  protected ObjectOutputStream objectOutputStream;
  protected DataInputStream dataInputStream;
  protected DataOutputStream dataOutputStream;

  public DropboxProtocol(Socket sk) throws IOException {
    Assert.assertNotNull(sk);
    sock = sk;
    inputStream = sk.getInputStream();
    outputStream = sk.getOutputStream();
    objectOutputStream = new ObjectOutputStream(outputStream);
    objectOutputStream.flush();
    objectInputStream = new ObjectInputStream(inputStream);
    dataInputStream = new DataInputStream(inputStream);
    dataOutputStream = new DataOutputStream(outputStream);
  }

  public void writeString(String s) throws IOException {
    Assert.assertNotNull(s);
    dataOutputStream.writeInt(s.length());
    dataOutputStream.write(s.getBytes());
  }

  public String readString() throws IOException {
    int len = dataInputStream.readInt();
    byte[] buf = new byte[len];
    dataInputStream.readFully(buf);
    return new String(buf);
  }
}
