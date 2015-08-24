package com.dropbox.common;

import org.junit.Assert;

public class DropBoxFile {
  private String fullPath;
  private long len;

  public DropBoxFile(String fullPath, long len) {
    Assert.assertNotNull(fullPath);
    Assert.assertTrue(len >= 0);
    this.fullPath = fullPath;
    this.len = len;
  }

  public String getFullPath() {
    return this.fullPath;
  }

  public long getLen() {
    return this.len;
  }

  @Override
  public String toString() {
    return getFullPath() + "\n" + "Length : " + getLen();
  }
}
