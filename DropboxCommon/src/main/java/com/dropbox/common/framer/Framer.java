package com.dropbox.common.framer;

import java.io.OutputStream;

public interface Framer {
  void frameMsg(byte[] message, OutputStream out);
  byte[] nextMsg();
}
