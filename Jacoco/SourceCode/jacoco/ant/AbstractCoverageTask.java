/*     */ package org.jacoco.ant;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.apache.tools.ant.BuildException;
/*     */ import org.apache.tools.ant.Project;
/*     */ import org.apache.tools.ant.Task;
/*     */ import org.jacoco.agent.AgentJar;
/*     */ import org.jacoco.core.runtime.AgentOptions;
/*     */ import org.jacoco.core.runtime.AgentOptions.OutputMode;
/*     */ 
/*     */ public class AbstractCoverageTask extends Task
/*     */ {
/*     */   private final AgentOptions agentOptions;
/*     */   private File destfile;
/*     */   private boolean enabled;
/*     */ 
/*     */   protected AbstractCoverageTask()
/*     */   {
/*  39 */     this.agentOptions = new AgentOptions();
/*  40 */     this.destfile = new File("jacoco.exec");
/*  41 */     this.enabled = true;
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  48 */     return this.enabled;
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/*  58 */     this.enabled = enabled;
/*     */   }
/*     */ 
/*     */   public void setDestfile(File file)
/*     */   {
/*  69 */     this.destfile = file;
/*     */   }
/*     */ 
/*     */   public void setAppend(boolean append)
/*     */   {
/*  80 */     this.agentOptions.setAppend(append);
/*     */   }
/*     */ 
/*     */   public void setIncludes(String includes)
/*     */   {
/*  91 */     this.agentOptions.setIncludes(includes);
/*     */   }
/*     */ 
/*     */   public void setExcludes(String excludes)
/*     */   {
/* 102 */     this.agentOptions.setExcludes(excludes);
/*     */   }
/*     */ 
/*     */   public void setExclClassLoader(String exclClassLoader)
/*     */   {
/* 114 */     this.agentOptions.setExclClassloader(exclClassLoader);
/*     */   }
/*     */ 
/*     */   public void setInclBootstrapClasses(boolean include)
/*     */   {
/* 125 */     this.agentOptions.setInclBootstrapClasses(include);
/*     */   }
/*     */ 
/*     */   public void setInclNoLocationClasses(boolean include)
/*     */   {
/* 136 */     this.agentOptions.setInclNoLocationClasses(include);
/*     */   }
/*     */ 
/*     */   public void setSessionId(String id)
/*     */   {
/* 146 */     this.agentOptions.setSessionId(id);
/*     */   }
/*     */ 
/*     */   public void setDumpOnExit(boolean dumpOnExit)
/*     */   {
/* 156 */     this.agentOptions.setDumpOnExit(dumpOnExit);
/*     */   }
/*     */ 
/*     */   public void setOutput(String output)
/*     */   {
/* 166 */     this.agentOptions.setOutput(output);
/*     */   }
/*     */ 
/*     */   public void setAddress(String address)
/*     */   {
/* 178 */     this.agentOptions.setAddress(address);
/*     */   }
/*     */ 
/*     */   public void setPort(int port)
/*     */   {
/* 189 */     this.agentOptions.setPort(port);
/*     */   }
/*     */ 
/*     */   public void setClassdumpdir(File dir)
/*     */   {
/* 200 */     this.agentOptions.setClassDumpDir(dir.getAbsolutePath());
/*     */   }
/*     */ 
/*     */   public void setJmx(boolean jmx)
/*     */   {
/* 210 */     this.agentOptions.setJmx(jmx);
/*     */   }
/*     */ 
/*     */   protected String getLaunchingArgument()
/*     */   {
/* 220 */     return prepareAgentOptions().getVMArgument(getAgentFile());
/*     */   }
/*     */ 
/*     */   private AgentOptions prepareAgentOptions() {
/* 224 */     if (AgentOptions.OutputMode.file.equals(this.agentOptions.getOutput())) {
/* 225 */       this.agentOptions.setDestfile(this.destfile.getAbsolutePath());
/*     */     }
/* 227 */     return this.agentOptions;
/*     */   }
/*     */ 
/*     */   private File getAgentFile() {
/*     */     try {
/* 232 */       File agentFile = null;
/* 233 */       String agentFileLocation = getProject().getProperty("_jacoco.agentFile");
/*     */ 
/* 235 */       if (agentFileLocation != null) {
/* 236 */         agentFile = new File(agentFileLocation);
/*     */       } else {
/* 238 */         agentFile = AgentJar.extractToTempLocation();
/* 239 */         getProject().setProperty("_jacoco.agentFile", agentFile.toString());
/*     */       }
/*     */ 
/* 243 */       return agentFile;
/*     */     } catch (IOException e) {
/* 245 */       throw new BuildException("Unable to extract agent jar", e, getLocation());
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.ant.AbstractCoverageTask
 * JD-Core Version:    0.5.4
 */