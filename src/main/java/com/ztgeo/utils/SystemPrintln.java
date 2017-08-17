package com.ztgeo.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.ztgeo.utils.StringStr;


public class SystemPrintln {
	public  Logger log = Logger.getLogger(SystemPrintln.class);
	public  SystemPrintln() {
		
		OutputStream textAreaStream = new OutputStream() {
			public void write(int b) throws IOException {
				StringStr.jta.append(String.valueOf((char) b));
			}
			public void write(byte b[]) throws IOException {
				StringStr.jta.append(new String(b));
			}
			public void write(byte b[], int off, int len) throws IOException {
				StringStr.jta.append(new String(b, off, len));
			}
		};

		PrintStream myOut = new PrintStream(textAreaStream);
		System.setOut(myOut);
	}
	
	
	
}
