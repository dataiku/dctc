package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpFieldsBuilder {
	private List<String> regexpChunks = new ArrayList<String>();
	private Pattern compiled;
	private List<String> columnNames = new ArrayList<String>();
	
	public static String HOSTNAME = "[A-z0-9\\.\\-_]*";
	public static String IP = "[0-9abcdef:\\.\\[\\]]*";
	public static String ESCAPED_URL = "[A-z0-9_\\-\\[\\]&@#%+()]*";
	public static String QUOTTED_NO_ESCAPE = "\"([^\"]*)\"";
	public static String INTEGER = "[-0-9]+";
	public static String DOUBLE = "[-0-9\\.]+";
	
	private boolean autoAddSpace;
	
	public RegexpFieldsBuilder(boolean autoAddSpace) {
		this.autoAddSpace = autoAddSpace;
	}
	
	private String buildRegexp(int nbChunks) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nbChunks; i++) {
			if (autoAddSpace && i > 0) sb.append(' ');
			sb.append(regexpChunks.get(i));
		}
		return sb.toString();
	}
	
	public String regexp() {
		return buildRegexp(regexpChunks.size());
	}
	
	public void captureInt(String column) {
		capture(column, "[0-9]*");
	}
	
	public void capture(String column, String pattern) {
		regexpChunks.add("(" +pattern + ")");
		columnNames.add(column);
	}
	
	public void customCapture(String column, String patternWithCapture) {
		regexpChunks.add(patternWithCapture);
		columnNames.add(column);
	}
	
	public void space() {
		match(" ");
	}
	
	public void spaces() {
		match("\\s+");
	}
	
	public void match(String pattern) {
		regexpChunks.add(pattern);
	}
	
	public void captureQuottedWithoutEscape(String column) {
		regexpChunks.add(column);
		match("\"([^\"]*)\"");
	}
	
	public Pattern compile() {
		String regexp = buildRegexp(regexpChunks.size());
		System.out.println(regexp);
		compiled = Pattern.compile(regexp);
		return compiled;
	}
	
	public List<String> exec(String line) {
		Matcher m = compiled.matcher(line);
		boolean matches = m.matches();
		if (!matches) {
			System.out.println("M=false F=" + m.find()); 
			return null;
		}
		List<String> out = new ArrayList<String>();
		System.out.println(org.apache.commons.lang.StringUtils.join(getColumnNames(), "--"));
		System.out.println("Groups= " + m.groupCount() + " cols= " + getColumnNames().size());
		for (int i = 1; i <= m.groupCount(); i++) {
			System.out.println("GROUP " + i + " " + m.group(i));
			out.add(m.group(i));
		}
		return out;
	}
	
	public static class PartialMatch {
		int totalChars;
		int totalChunks;
		public int matchedChars;
		int matchedChunks;
	}
	
	public PartialMatch partialExec(String line) {
		PartialMatch pm = new PartialMatch();
		pm.totalChars = line.length();
		pm.totalChunks = regexpChunks.size(); 
		for (int i = regexpChunks.size() - 1; i >= 0; --i) {
			String regexp = buildRegexp(i +1 );
			Matcher m = Pattern.compile(regexp).matcher(line);
			boolean found = m.find();
			if (found) {
				System.out.println("MATCHED " + m.group() + " WITH " + regexp);
				
				pm.matchedChars = m.end();
				pm.matchedChunks = i + 1 ;
				return pm;
			}
		}
		
		return pm;
	}
	
	public List<String> getColumnNames() {
		return columnNames;
	}
}
