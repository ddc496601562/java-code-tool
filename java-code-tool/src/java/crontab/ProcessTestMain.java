import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class ProcessTestMain {

	public static void main(String[] args) throws IOException, InterruptedException {
		String[] cmd=new String[] {"bash","-c",args[0] };
		ProcessBuilder builder = new ProcessBuilder(cmd);
		builder.environment().put("set_date", "20131123");
		Process process=builder.start();
//		int ret=process.waitFor();
//		System.out.println(ret);
		BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String nullLine=null ;
		while(true){
			try{
			process.exitValue();
			break ;
			}catch(Exception e){
			}
			nullLine = errReader.readLine();
			System.out.println("  ,err is "+nullLine);
			nullLine = outReader.readLine();
			System.out.println("  ,out is "+nullLine);		
			Thread.sleep(5000);  
		}
//		while ((nullLine = errReader.readLine()) != null) {
//			System.out.println("  ,err is "+nullLine);
//		}
//		System.out.println("*************");
//		while ((nullLine = outReader.readLine()) != null) {
//			System.out.println("  ,out is "+nullLine);
//		}
		

	}

}
