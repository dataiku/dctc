package com.dataiku.dctc.command;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.dataiku.dctc.Settings;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.configuration.StructuredConf;
import com.dataiku.dctc.utils.ExitCode;

public class CmpTest {
    private void checkErr(String str) throws Exception {
        assertTrue(str.equals(Settings.getErr()));
    }
    private void checkOut(String str) throws Exception {
        assertTrue(str.equals(Settings.getOut()));
    }

    @org.junit.Test
    public void cmp() throws IOException, Exception {
        Settings.setOutputs();

        Cmp cmp = new Cmp();
        String[] args = new String[] { "/tmp/does not exits", "same as the first argument" };
        StructuredConf conf = new StructuredConf();
        conf.parse(GlobalConf.confPath());
        conf.parseSsh(GlobalConf.sshConfigFile());
        cmp.setFileBuilder(conf.getFileBuilder());
        cmp.setExitCode(new ExitCode());
        cmp.perform(args);
        assertTrue(cmp.getExitCode().getExitCode() == 2);
        checkOut("");

        Settings.setOutputs();
        args[0] = "/etc/bash.bashrc";
        args[1] = "/etc/bash.bashrc";
        cmp.perform(args);
        assertTrue(cmp.getExitCode().getExitCode() == 0);
        checkOut("");
        checkErr("");
    }
}
