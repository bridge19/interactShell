package com.xy.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		Process p = Runtime.getRuntime().exec("./inter.sh");   
		
		InputStream is = p.getInputStream();   
		OutputStream os = p.getOutputStream();
		
		BufferedReader br = new BufferedReader(new InputStreamReader (is));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		
		for( int i=0;i<10;i++){
			
			bw.write(String.valueOf(i));
			bw.flush();
			
			String rv = null;
			while ((rv = br.readLine()) == null) {   
				  System.out.println("waiting...");
		          Thread.sleep(500L);
		    }   
			System.out.println(rv);
		}
	}

}
