package com.dropbox.common.protocol;

import org.junit.Assert;

import java.io.*;
import java.net.Socket;

/* The general protocol for now is :
 * The client sends the following things in order to the server in order.
 *
 * CLIENT, CLIENT_ID, DIR
 * For the DIR part. You first send an object representing meta data about the dir.
 * Then you send all the individual files in the dir.
 *
 * The Server verifies the client and reads the dir.
 */
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
