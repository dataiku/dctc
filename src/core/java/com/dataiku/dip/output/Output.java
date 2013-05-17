package com.dataiku.dip.output;

import java.io.IOException;

public interface Output {
	public OutputWriter getWriter() throws IOException;
}
