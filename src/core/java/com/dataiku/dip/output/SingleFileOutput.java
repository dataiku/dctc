package com.dataiku.dip.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;

public class SingleFileOutput implements OutputDescriptor {
	public SingleFileOutput(String path, OutputFormatter formatter) {
		this.path = path;
		this.formatter = formatter;
	}
	private String path;
	private OutputFormatter formatter;

	public class SingleFileOutputStream extends StreamOutput{
		private OutputStream finalOutputStream;
		private FileOutputStream fos;
		private BufferedWriter bwr;

		private boolean headerEmitted;
		private ColumnFactory cf;

		@Override
		public void emitRow(Row row) throws Exception {
			if (!headerEmitted) {
				formatter.header(cf, bwr);
				headerEmitted = true;
			}
			formatter.format(row, cf, bwr);
		}

		@Override
		public void lastRowEmitted() throws Exception {
			formatter.footer(cf, bwr);
			bwr.flush();
			finalOutputStream.close();
			fos.close();
		}

		@Override
		public void init(ColumnFactory cf) throws IOException {
			this.cf = cf;
			fos = new FileOutputStream(new File(path));
			if (path.contains(".gz")) {
				finalOutputStream = new GZIPOutputStream(fos);
			} else {
			    finalOutputStream = fos;
			}
			bwr = new BufferedWriter(new OutputStreamWriter(finalOutputStream, "utf8"));
		}
	}

	@Override
	public StreamOutput getWriter() throws IOException {
		return new SingleFileOutputStream();
	}
}
