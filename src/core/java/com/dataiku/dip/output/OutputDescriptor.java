package com.dataiku.dip.output;

import java.io.IOException;

public interface OutputDescriptor {
	public StreamOutput getWriter() throws IOException;
}
