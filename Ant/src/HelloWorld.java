
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
public class HelloWorld extends Task {

    //get message from the ant Project
    String msg;
    public void setMessage(String msg) {
        this.msg = msg;
    }

   

    public void execute() {
        if (msg==null) {
            throw new BuildException("no message received");
            
        }
        System.out.println("receive msg:"+msg);
        String projectName = getProject().getProperty("ant.project.name");
        System.out.println("The name of Project :"+projectName);
    }

}