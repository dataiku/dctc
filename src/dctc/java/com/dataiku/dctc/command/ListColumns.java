package com.dataiku.dctc.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Options;

import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.file.GeneralizedFileInputSplit;
import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.DevNullProcessorOutput;
import com.dataiku.dip.datalayer.streamimpl.StreamColumnFactory;
import com.dataiku.dip.datalayer.streamimpl.StreamRowFactory;
import com.dataiku.dip.input.Format;
import com.dataiku.dip.input.formats.BasicFormatExtractorFactory;
import com.dataiku.dip.input.formats.ExtractionLimit;
import com.dataiku.dip.input.formats.FormatExtractor;
import com.dataiku.dip.utils.IndentedWriter;
import com.dataiku.dip.utils.PrettyArray;

public class ListColumns extends Command {
    // Public
    public void longDescription(IndentedWriter printer) {
        printer.print("FIXME");
    }
    public String tagline() {
        return "List the name of the column.";
    }
    public Options setOptions() {
        Options opt = new Options();
        opt.addOption("m", "minimal", false, "Set to minimal the display scheme.");
        return opt;
    }
    public String cmdname() {
        return "list-columns";
    }

    @Override
    public void perform(List<GeneralizedFile> args) {
        Format csv = new Format("csv").withParam("separator", ",");
        FormatExtractor extractor = BasicFormatExtractorFactory.build(csv);
        StreamColumnFactory stream = null;
        ExtractionLimit limit = new ExtractionLimit();
        DevNullProcessorOutput out = new DevNullProcessorOutput();
        limit.maxRecords = 1;
        PrettyArray array = initArray();

        for (GeneralizedFile arg: args) {
            GeneralizedFileInputSplit inputStream;

            stream = new StreamColumnFactory();
            List<String> row = new ArrayList<String>();
            row.add(arg.givenName());

            { // Get the column names
                try {
                    inputStream = new GeneralizedFileInputSplit(arg, arg.inputStream());// FIXME: manage compress content.
                }
                catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    extractor.run(inputStream, out, null, stream, new StreamRowFactory(), null, limit);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Column c: stream.columns()) {
                row.add(c.getName());
            }
            array.add(row);
        }

        array.print();

    }
    // protected
    protected String proto() {
        return "dctc list-columns [OPT...] ARG...";
    }

    // private
    private PrettyArray initArray() {
        PrettyArray array = new PrettyArray();

        if (hasOption("m")) {
            array.setTab(" ");
            array.setBeginChar("");
            array.setBetweenChar(" ");
            array.setEndChar("");
            array.setIndentLast(false);
        }
        else {
            List<String> header = new ArrayList<String>();
            header.add("File name");
            array.add(header);
            array.addLineAt(0);
            array.addLineAt(1);
            array.setTrailingLine(true);
        }

        return array;
    }

}
