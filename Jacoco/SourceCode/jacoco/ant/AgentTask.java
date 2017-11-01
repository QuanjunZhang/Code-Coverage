/*    */ package org.jacoco.ant;
/*    */ 
/*    */ import org.apache.tools.ant.BuildException;
/*    */ import org.apache.tools.ant.Project;
/*    */ 
/*    */ public class AgentTask extends AbstractCoverageTask
/*    */ {
/*    */   private String property;
/*    */ 
/*    */   public void setProperty(String property)
/*    */   {
/* 31 */     this.property = property;
/*    */   }
/*    */ 
/*    */   public void execute()
/*    */     throws BuildException
/*    */   {
/* 42 */     if ((this.property == null) || (this.property.length() == 0)) {
/* 43 */       throw new BuildException("Property is mandatory", getLocation());
/*    */     }
/* 45 */     String jvmArg = (isEnabled()) ? getLaunchingArgument() : "";
/*    */ 
/* 47 */     getProject().setNewProperty(this.property, jvmArg);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.ant.AgentTask
 * JD-Core Version:    0.5.4
 */