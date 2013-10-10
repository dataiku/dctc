package com.dataiku.dip.output;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datasets.Schema;
import com.dataiku.dip.input.formats.BasicCSVFormatConfig;
import com.dataiku.dip.utils.ErrorContext;
import com.dataiku.dip.utils.WithParams;

public class CSVOutputFormatter extends StringOutputFormatter {


    BasicCSVFormatConfig config;

    public CSVOutputFormatter(WithParams p) {
        super(p.getParam("charset", "utf8"));
        config = new BasicCSVFormatConfig(p);
        this.delimiter  = config.separator;
        this.printHeaderLine = config.parseHeaderRow;
        this.prefixHeaderLineWithSharp = p.getBoolParam("prefixHeaderLineWithSharp", false);
    }

    @Override
    public void setOutputSchema(Schema schema) {
        this.outputSchema = schema;
    }

    @Override
    public void header(ColumnFactory cf, Writer sb) throws IOException {
        if (printHeaderLine && prefixHeaderLineWithSharp) {
            sb.append("# ");
        }
        if (outputSchema != null) {
            for (int i = 0; i < outputSchema.getColumns().size(); i++) {
                String name = outputSchema.getColumns().get(i).getName();
                if (i > 0 && printHeaderLine) sb.append(delimiter);
                headerColumns.add(cf.column(name));
                Schema.Type type = Schema.Type.forName(outputSchema.getColumns().get(i).getType());
                columnTypes.add(type);
                if (printHeaderLine) sb.append(name);
            }
        } else {
            int i = 0;
            for (Column c : cf.columns()) {
                headerColumns.add(c);
                columnTypes.add(Schema.Type.STRING);
                if (i++ > 0 && printHeaderLine) sb.append(delimiter);
                if (printHeaderLine) sb.append(c.getName());
            }
        }
        if (printHeaderLine) sb.append('\n');
    }

    @Override
    public void format(Row row, ColumnFactory cf, Writer sb) throws IOException {
        int i = 0;
        for (Column c : headerColumns) {
            String v = row.get(c);
            Schema.Type type = columnTypes.get(i);
            if (i++ > 0) sb.append(delimiter);
            if (!type.isPrimitive()) {
                if (config.arrayMapFormat.equals("json")) {
                    appendEscapedAndQuoted(sb, v, delimiter, config.quoteChar, config.escapeChar);
                } else if (type == Schema.Type.MAP) {
                    appendMapDelimited(sb, v, delimiter, config.quoteChar, config.escapeChar, config.arraySeparator, config.mapKeySeparator);
                } else if (type == Schema.Type.ARRAY) {
                    appendArrayDelimited(sb, v, delimiter, config.quoteChar, config.escapeChar, config.arraySeparator, config.mapKeySeparator);
                } else {
                    throw ErrorContext.iae("Unknown non-primitive type " + type);
                }
            } else {
                appendEscapedAndQuoted(sb, v, delimiter, config.quoteChar, config.escapeChar);
            }
        }
        sb.append('\n');
    }

    @Override
    public void footer(ColumnFactory cf, Writer sb) throws IOException {
    }

    // Append a JSON object as a delimited object.
    public static void appendMapDelimited(Writer wr, String v, char sep, char quote, Character escape, char arraySeparator, char mapKeySeparator) throws IOException {
        try {
            JSONObject obj = new JSONObject(v);
            Iterator<?> it = obj.keys();
            int i = 0;
            StringBuilder sb = new StringBuilder();
            boolean shouldQuote = false;
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = obj.getString(key);
                if (i++ > 0) sb.append(arraySeparator);
                shouldQuote |= appendEscaped(sb, key, sep, quote, escape);
                sb.append(mapKeySeparator);
                shouldQuote |= appendEscaped(sb, value, sep, quote, escape);
            }
            if (shouldQuote) {
                // TODO check the actual hive Serde implementation
                wr.write(quote);
                wr.write(sb.toString());
                wr.write(quote);
            } else {
                wr.write(sb.toString());
            }
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public static void appendArrayDelimited(Writer wr, String v, char sep, char quote, Character escape, char arraySeparator, char mapKeySeparator) throws IOException {
        try {
            JSONArray obj = new JSONArray(v);
            int length = obj.length();
            StringBuilder sb = new StringBuilder();
            boolean shouldQuote = false;
            for(int i = 0; i < length; i++) {
                String value = obj.getString(i);
                if (i > 0) sb.append(arraySeparator);
                shouldQuote |= appendEscaped(sb, value, sep, quote, escape);
            }
            if (shouldQuote) {
                wr.write(quote);
                wr.write(sb.toString());
                wr.write(quote);
            } else {
                wr.write(sb.toString());
            }
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }


    public static boolean appendEscaped(StringBuilder sb, String v, char sep, char quote, Character escape) {
        if (v != null && v.length() > 0) {
            if (StringUtils.contains(v, sep)|| StringUtils.contains(v, quote) || StringUtils.contains(v, '\n') ||
                    StringUtils.contains(v, '\r')) {
                if (escape != null) {
                    sb.append(StringUtils.replace(v, "" + quote, "" + escape + "" + quote));
                } else {
                    sb.append(v);
                }
                return true;
            } else {
                sb.append(v);
                return false;
            }
        }
        return false;
    }
    public static void appendEscapedAndQuoted(Writer wr, String v, char sep, char quote, Character escape) throws IOException {
        if (v != null && v.length() > 0)  {
            if (StringUtils.contains(v, sep)|| StringUtils.contains(v, quote) || StringUtils.contains(v, '\n') ||
                    StringUtils.contains(v, '\r')) {
                wr.append(quote);
                if (escape != null) {
                    wr.append(StringUtils.replace(v, "" + quote, "" + escape + "" + quote));
                } else {
                    wr.append(v);
                }
                wr.append(quote);
            } else {
                wr.append(v);
            }
        }
    }

    private Schema outputSchema;
    private char delimiter;
    private boolean printHeaderLine;
    private boolean prefixHeaderLineWithSharp;
    private List<Column> headerColumns = new ArrayList<Column>();
    private List<Schema.Type> columnTypes = new ArrayList<Schema.Type>();
}
