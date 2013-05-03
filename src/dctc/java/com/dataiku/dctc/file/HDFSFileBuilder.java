package com.dataiku.dctc.file;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.hadoop.HadoopUtils;
import com.dataiku.dip.utils.ErrorContext;
import com.dataiku.dip.utils.Params;

public class HDFSFileBuilder extends ProtocolFileBuilder {

    @Override
    public Protocol getProtocol() {
        return Protocol.HDFS;
    }

    @Override
    public void validateAccountParams(String accountData, Params p) {
        checkAllowedOnly(accountData, p, new String[]{"hadoopConfDir"});
        checkMandatory(accountData, p, "hadoopConfDir");
    }

    @Override
    public GeneralizedFile buildFile(String accountData, String protocolData) {
        Params p = null;
        if (accountData != null) {
            p = bank.getAccountParams("hdfs", accountData);
        } else {
            p = bank.getAccountParamsIfExists("hdfs", accountData);

            if (p == null) {
                /* I can still try to build using HADOOP_HOME */
                if (System.getenv("HADOOP_HOME") == null && System.getenv("HADOOP_PREFIX") == null) {
                    throw ErrorContext.iaef("Neither configured credential nor HADOOP_HOME nor HADOOP_PREFIX variable found, can't configure HDFS access");
                }
            }
        }
        Configuration conf = new Configuration();
        if (p == null) {
            conf.addResource(new Path(HadoopUtils.getCoreSiteLocation()));
        } else {
            validateAccountParams(accountData, p);
            conf.addResource(new Path(p.getNonEmptyMandParam("hadoopConfDir")));
        }

        return new HdfsFile(protocolData, accountData, conf);
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
