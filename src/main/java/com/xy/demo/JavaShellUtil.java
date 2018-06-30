package com.xy.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.atomic.AtomicInteger;  
   
public class JavaShellUtil {  
    // 基本路径  
    private static final String basePath = "/home/xuyong/demo/";  
   
    // 发送文件到Kondor系统的Shell的文件名(绝对路径)  
    private static final String shellName = basePath  + "inter.sh";  
    
    private static AtomicInteger rwFlag = new AtomicInteger(1);
    
    private static volatile boolean running = true;
   
    public int executeShell(String shellCommand) throws IOException, InterruptedException {  
        System.out.println("shellCommand:"+shellCommand);  
        BufferedReader bufferedReader = null;  
        BufferedWriter bufferedWriter = null;
		
   
        Process pid = null;  
        String[] cmd = { "/bin/sh", "-c", shellCommand };  
        pid = Runtime.getRuntime().exec(cmd);  
        if (pid != null) {  
            bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024); 
            new Thread(new MyReader(bufferedReader)).start();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(pid.getOutputStream()),1024);
            new Thread(new MyWriter(bufferedWriter)).start();
            pid.waitFor();
            running = false;
        } else {  
            System.out.println("没有pid");  
            return -1;
        }
        return 1;  
    }  
   
    public static class MyReader implements Runnable{
    	BufferedReader bufferedReader = null; 
    	public MyReader(BufferedReader bufferedReader){
    		this.bufferedReader = bufferedReader;
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(running){
				while(rwFlag.get()!=0){
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
        		System.out.println("reading...");
		        String line = null;  
				 try {
					while (bufferedReader != null  
					            && (line = bufferedReader.readLine()) != null) {  
					        System.out.println(line);  
					        break;
					    }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				rwFlag.incrementAndGet();
			}
		}
    	
    }
    
    public static class MyWriter implements Runnable{
    	int i =0;
    	BufferedWriter bufferedWriter = null;
    	
    	final BufferedReader reader =  new BufferedReader (new InputStreamReader(System.in)); 
    	
    	public MyWriter(BufferedWriter bufferedWriter){
    		this.bufferedWriter = bufferedWriter;
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
	        try {
	        	while(running){
	        		while(rwFlag.get()!=1){
	        			try {
							Thread.sleep(100L);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        		}
	        		System.out.println("writing...");
	        		String input = reader.readLine();
	        		bufferedWriter.write(input + "\n");
	        		bufferedWriter.flush();
	        		rwFlag.decrementAndGet();
	        	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    
    public static void main(String[] args) throws InterruptedException {  
        try {  
            new JavaShellUtil().executeShell(shellName);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}  
