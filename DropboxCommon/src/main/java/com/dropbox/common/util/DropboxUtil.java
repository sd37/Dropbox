package com.dropbox.common.util;

import org.junit.Assert;

public class DropboxUtil {
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
}
