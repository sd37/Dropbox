package com.dropbox.common.coder;

public interface DropboxMsgCoder {
  byte[] toWire(String path);
  void fromWire(byte[] msg);
}
