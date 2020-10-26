/*
 * Copyright (c) 2016-2020 chronicle.software
 */

package net.openhft.chronicle.core;

import net.openhft.chronicle.core.io.IOTools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/*
NOTE: RandomAccessFile doesn't clean up it's resources when GC'ed
 */
public class RandomAccessFileCleanupMain {
    public static void main(String[] args) throws IOException {
        File tempDir = IOTools.createTempFile("RandomAccessFileCleanupMain");
        tempDir.mkdir();
        for (int j = 0; j < 100; j++) {
            int files = new File("/proc/self/fd").list().length;
            System.out.println("File descriptors " + files);
            ByteBuffer bb = ByteBuffer.allocateDirect(64);
            for (int i = 0; i < 100; i++) {
//                RandomAccessFile file = new CleaningRandomAccessFile(tempDir + "/file" + i, "rw");
                RandomAccessFile file = new RandomAccessFile(tempDir + "/file" + i, "rw");
                bb.clear();
                file.getChannel().write(bb);
            }
            System.gc();
        }
    }
}
