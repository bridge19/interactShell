package com.xy.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;  
   
public class JavaShellUtil {  
    
    private Executor executor=Executors.newFixedThreadPool(4);
    
    private ConcurrentMap<String, Robot> robots = new ConcurrentHashMap<>();
    
   
    public String interActWith(String robotName,String said) throws IOException, InterruptedException, ExecutionException {  
    	Robot robot = robots.get(robotName);
    	
    	if(robot==null || !robot.isAlive()){
        	return null;
    	}
	     
        FutureTask<String> futureTask = new FutureTask<String>(new Interact(robot,said));
        executor.execute(futureTask);
        
        String response = futureTask.get();
        return response;  
    }  
    
    public void createRobot(String robotName){
    	Robot robot = Robot.createRobot();
        robots.put(robotName, robot);
    }
    
    public static class Interact implements Callable<String>{
         Robot robot = null; 
         String said = null;
         public Interact(Robot robot,String said){
        	 this.robot=robot;
        	 this.said = said;
         }
		@Override
		public String call() {
			// TODO Auto-generated method stub
        	StringBuilder sb = new StringBuilder();
            try {
            	PrintWriter outputWriter = robot.getOutputWriter();
            	outputWriter.println(said);
                String answered = null; 
                int i=0;
            	while ((answered=robot.getBufferedReader().readLine())!=null) {  
            		answered = robot.getBufferedReader().readLine();
            		sb.append(answered).append('\n');  
			    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            return sb.toString();
		}
    	
    }
    
    public static class Robot{
    	private static final String basePath = "/home/xuyong/demo/";  
        private static final String shellName = basePath  + "sa.bin -t ./";  
        
        private Process pid;
        private BufferedReader bufferedReader = null;  
        private PrintWriter outputWriter = null;
        
        private Robot(){}
        
        public static Robot createRobot(){

            String[] cmd = { "/bin/sh", "-c", shellName };  
            Process pid;
    		try {
    			pid = Runtime.getRuntime().exec(cmd);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			return null;
    		} 

    		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()),128); 
    		PrintWriter outputWriter = new PrintWriter(pid.getOutputStream(), true);
            
            Robot robot = new Robot();
            robot.pid=pid;
            robot.bufferedReader=bufferedReader;
            robot.outputWriter=outputWriter;
            String answer = null;
            try {
				while((answer = bufferedReader.readLine())!=null){
					System.out.println(answer);
				}
				System.out.println("init done...");
				System.out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return robot;
        }
        
        public boolean isAlive(){
        	return pid.isAlive() && pid.getInputStream()!=null && pid.getOutputStream()!=null;
        }

    	public BufferedReader getBufferedReader() {
    		return bufferedReader;
    	}

		public PrintWriter getOutputWriter() {
			return outputWriter;
		}

    }
    
    public static void main(String[] args) throws InterruptedException, ExecutionException {  
        try {  
        	JavaShellUtil util = new JavaShellUtil();
        	util.createRobot("robot");
        	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in), 1024);
        	while(true){
	        	String saying = bufferedReader.readLine();
	        	
	        	if(saying != null){
	        		System.out.println("saying: "+ saying);
		        	String answer = util.interActWith("robot",saying);  
		        	System.out.println("answer: " + answer);
					System.out.flush();
	        	}else{
	        		Thread.sleep(500L);
	        	}
        	}

        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}  
