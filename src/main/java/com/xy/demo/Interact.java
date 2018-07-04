package com.xy.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Interact {

	public static class Robot {
		private static final String basePath = "/home/xuyong/demo/";
		private static final String shellName = basePath + "sa.bin -t ./";

		private Process pid;
		private BufferedReader bufferedReader = null;
		private PrintWriter outputWriter = null;

		private Robot() {
		}

		public static Robot createRobot() {
			return createRobot(shellName);
		}
		public static Robot createRobot(String command) {

			String[] cmd = { "/bin/sh", "-c", command };
			Process pid;
			try {
				pid = Runtime.getRuntime().exec(cmd);
				int result = pid.waitFor();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 128);
			PrintWriter outputWriter = new PrintWriter(pid.getOutputStream(), true);

			Robot robot = new Robot();
			robot.pid = pid;
			robot.bufferedReader = bufferedReader;
			robot.outputWriter = outputWriter;
			new Thread(new MyReader(bufferedReader)).start();
			return robot;
		}

		public void say(String said) {
			System.out.println("robot say: " + said);
			outputWriter.println(said);
			System.out.println("robot finished");
		}

		public boolean isAlive() {
			return pid.isAlive() && pid.getInputStream() != null && pid.getOutputStream() != null;
		}

		public BufferedReader getBufferedReader() {
			return bufferedReader;
		}

		public PrintWriter getOutputWriter() {
			return outputWriter;
		}

	}

	public static class MyReader implements Runnable {
		BufferedReader bufferedReader = null;

		public MyReader(BufferedReader bufferedReader) {
			this.bufferedReader = bufferedReader;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String line = null;
			try {
				while(true){
					while ((line = bufferedReader.readLine()) != null) {
						System.out.println("responsed: " + line);
					}
					Thread.sleep(500L);
		        	System.out.println("2222");
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Robot robot = null;
		if(args!=null && args.length>0){
			StringBuilder command = new StringBuilder(128);
			for(String arg: args){
				command.append(arg).append(" ");
			}
			String commandStr = command.substring(0,command.length()-1);
			System.out.println("command: "+ commandStr);
			robot = Robot.createRobot(commandStr);
		}else{
			robot = Robot.createRobot();
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in), 1024);
		
		
		while(true){
			String saying = null;
			while (robot.isAlive() && (saying = bufferedReader.readLine())!=null) {
				robot.say(saying); 
				System.out.println("consumer: " + saying);
			}
        	Thread.sleep(500L);
        	System.out.println("1111");
    	}
	}
}
