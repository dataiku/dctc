package com.dataiku.dctc.file;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import com.dataiku.dctc.HadoopDistributionClassLoader;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.dataiku.dip.hadoop.HadoopUtils;
import com.dataiku.dip.utils.ErrorContext;
import com.dataiku.dip.utils.Params;

public class HdfsFileBuilder extends ProtocolFileBuilder {

    @Override
    public Protocol getProtocol() {
        return Protocol.HDFS;
    }

    @Override
    public boolean validateAccountParams(String accountSettings, Params p) {
        return checkAllowedOnly(accountSettings, p, new String[] {"hadoopConfDir"})
            || checkMandatory(accountSettings, p, "hadoopConfDir");
    }

    @Override
    public GeneralizedFile buildFile(String accountSettings, String rawPath) {
        HadoopDistributionClassLoader.addLibraries();
        Params p;
        if (accountSettings != null) {
            p = bank.getAccountParams("hdfs", accountSettings);
        }
        else {
            p = bank.getAccountParamsIfExists("hdfs", accountSettings);

            if (p == null) {
                /* I can still try to build using HADOOP_HOME */
                if (System.getenv("HADOOP_HOME") == null
                    && System.getenv("HADOOP_PREFIX") == null
                    && HadoopDistributionClassLoader.guessHadoopHome() == null) {
                    throw ErrorContext.iaef("Neither configured credential nor HADOOP_HOME nor HADOOP_PREFIX variable found, can't configure HDFS access");
                }
            }
        }
        Configuration conf = new Configuration();
        if (p == null) {
            conf.addResource(new Path(HadoopUtils.getCoreSiteLocation()));
        }
        else {
            if (validateAccountParams(accountSettings, p)) {
                throw invalidAccountSettings(accountSettings);
            }
            conf.addResource(new Path(p.getNonEmptyMandParam("hadoopConfDir")));
        }

        return new HdfsFile(rawPath, accountSettings, conf);
    }
    @Override
    public final String fileSeparator() {
        return "/";
    }
}
