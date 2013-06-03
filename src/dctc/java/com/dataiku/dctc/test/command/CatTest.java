package com.dataiku.dctc.test.command;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dataiku.dctc.configuration.SshConfig;
import com.dataiku.dctc.command.Cat;
import com.dataiku.dctc.configuration.CredentialProviderBank;
import com.dataiku.dctc.file.FileBuilder;
import com.dataiku.dctc.test.Settings;

public class CatTest {
    public void initializationError() {

    }
    private void checkErr(String str) {
        assertTrue(str.equals(Settings.getErr().toString()));
    }
    private void checkOut(String str) {
        assertTrue(str.equals(Settings.getOut().toString()));
    }
    private void checkOutputs(String out, String err) {
        checkOut(out);
        checkErr(err);
    }
    @org.junit.Test
    public void commandParameters()throws IOException  {
        Settings.setOutputs();
        Cat c = new Cat();
        String[] s = { "-foo" };
        c.perform(s);

        // Check the output.
        checkOutputs("", "dctc cat: Unrecognized option: -foo" + System.getProperty("line.separator"));
    }
    @org.junit.Test
    public void help() throws IOException {
        Settings.setOutputs();
        Cat c = new Cat();
        String[] s = { "-?" };
        c.perform(s);

        checkErr("");
    }
    @org.junit.Test
    public void commandOutput() throws IOException {
        String fileName = "cat-test.foo";
        String fileContent = "The Hitchhicker.";
        Settings.setOutputs();

        FileOutputStream f;
        try {
            f = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            return;
        }
        try {
            f.write(fileContent.getBytes());
            f.close();
        } catch (IOException e) {
            return;
        }
        Cat c = new Cat();
        CredentialProviderBank bank = new CredentialProviderBank();
        c.setFileBuilder(new FileBuilder(bank, null));
        String[] s =  { fileName };
        c.perform(s);

        checkOutputs(fileContent, "");
        File file = new File(fileName);
        file.delete();
    }
}
