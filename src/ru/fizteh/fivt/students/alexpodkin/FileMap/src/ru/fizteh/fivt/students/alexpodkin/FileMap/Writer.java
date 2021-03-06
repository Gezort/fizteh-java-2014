package ru.fizteh.fivt.students.alexpodkin.FileMap;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Writer {

    private String fileMapPath;

    public Writer(String path) {
        fileMapPath = path;
    }

    private void writeWord(DataOutputStream dataOutputStream, String word) throws IOException {
        dataOutputStream.writeInt(word.length());
        dataOutputStream.writeChars(word);
    }

    public void writeDataToFile(HashMap<String, String> fileMap) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(fileMapPath));
        for (HashMap.Entry<String, String> entry : fileMap.entrySet()) {
            writeWord(dataOutputStream, entry.getKey());
            writeWord(dataOutputStream, entry.getValue());
        }
    }

}
