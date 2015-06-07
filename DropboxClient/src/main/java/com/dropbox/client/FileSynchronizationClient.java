package com.dropbox.client;

/**
 * Created by spandan on 12/21/14.
 * Description : General Interface for a file sync client.
 */

public interface FileSynchronizationClient {
    void run();

    boolean sync();
}
