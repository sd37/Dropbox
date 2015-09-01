package com.dropbox.common.util;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DropboxUtil {
  public static void writeString(String s, ObjectOutputStream out) throws IOException {
    Assert.assertNotNull(s);
    Assert.assertNotNull(out);
    out.writeInt(s.length());
    out.write(s.getBytes());
  }

  public static String readString(ObjectInputStream in) throws IOException {
    Assert.assertNotNull(in);
    int len = in.readInt();
    byte[] buf = new byte[len];
    in.readFully(buf);
    return new String(buf);
  }


  public static String constructClientPath(String orgPath) {
    Assert.assertNotNull(orgPath);
    String[] parts = orgPath.split("/");
    int i = 0;
    for (String p : parts) {
      if (p.contains("client")) {
        break;
      }
      i++;
    }

    String newPath = "";
    for (int j = i; j < parts.length; j++) {
      newPath = newPath + "/" + parts[j];
    }
    return newPath;
  }

  public static Map<String, String> parseMetaDataFile(String metaPath) throws IOException {
    Assert.assertNotNull(metaPath);
    InputStream in = Files.newInputStream(Paths.get(metaPath));
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    Map<String, String> mp = new HashMap<>();
    while(true) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      else if(line.equals("\n")) {
        throw new RuntimeException("Illegal METADATA file format.. file is corrupted.");
      }
      String[] parts = line.split("|");
      mp.put(parts[0], parts[1]);
    }
    in.close();
    return mp;
  }

  public static void writeByteBuffer(byte[] buf, OutputStream out) throws
      IOException {
    Assert.assertNotNull(buf);
    Assert.assertNotNull(out);
    DataOutputStream dataOut = new DataOutputStream(out);
    dataOut.writeInt(buf.length);
    dataOut.write(buf, 0, buf.length);
  }

  public static byte[] readByteBuffer(InputStream in) throws IOException {
    Assert.assertNotNull(in);
    DataInputStream dataIn = new DataInputStream(in);
    int len = dataIn.readInt();
    byte[] buf = new byte[len];
    dataIn.readFully(buf);
    return buf;
  }
}
