package com.dataiku.dctc.command;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dataiku.dctc.Main;
import com.dataiku.dctc.command.Cat;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.configuration.CredentialProviderBank;
import com.dataiku.dctc.file.FileBuilder;
import com.dataiku.dctc.Settings;
import com.dataiku.dip.utils.IndentedWriter;

public class CatTest {

    private void checkErr(String str) throws Exception {
        assertTrue(str.equals(Settings.getErr()));
    }
    private void checkOut(String str) throws Exception {
        assertTrue(str.equals(Settings.getOut()));
    }
    private void checkOutputs(String out, String err) throws Exception {
        checkOut(out);
        checkErr(err);
    }
    @org.junit.Test
    public void commandParameters()throws IOException, Exception {
        Settings.setOutputs();
        Cat c = new Cat();
        String[] s = { "-foo" };
        try {
            c.perform(s);
        } catch (Command.EndOfCommand e) {
            // ignore
        }

        // Check the output.
        checkOut("");
        assertTrue(Settings.getErr().startsWith("dctc cat: ERROR: Unrecognized option: -foo" + System.getProperty("line.separator")));
    }
    @org.junit.Test
    public void help() throws IOException, Exception {
        // reset context
        Settings.setOutputs();
        Cat c = new Cat();
        Main.commandHelp(c,new IndentedWriter());
        String helpMessage = Settings.getOut();
        Settings.setOutputs();

        String[] s = { "--help" };
        try
        {
            c.perform(s);
        } catch (Command.EndOfCommand e) {
            // ignore
        }

        checkOutputs(helpMessage,"");
    }
    @org.junit.Test
    public void commandOutput() throws IOException, Exception {
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
        if (!file.delete())
            System.err.println("Warning: unable to delete test file: " + file.getCanonicalFile());
    }
}
