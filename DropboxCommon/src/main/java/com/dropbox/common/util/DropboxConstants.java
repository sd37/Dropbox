package com.dropbox.common.util;

public interface DropboxConstants {
  final String DROPBOX_DEFAULT_SERVER_HOST = "localhost";
  final String DROPBOX_DEFAULT_SERVER_PORT = "8945";

  final int TIME_MSEC = 1000;
  final int TIME_MIN = 60 * TIME_MSEC;

  String DEFAULT_DROPBOX_SERVER_DIR = "/tmp/server";
  final int BUF_SIZE_BYTES = 100;
  final String SERVER_SANITY_STRING = "SERVER";
  final String CLIENT_SANITY_STRING = "CLIENT";
  final String CLIENT_METADATA_FILENAME = "/METADATA";
}