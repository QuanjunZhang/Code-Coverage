/*     */ package org.jacoco.ant;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import org.apache.tools.ant.BuildException;
/*     */ import org.apache.tools.ant.Task;
/*     */ import org.jacoco.core.runtime.AgentOptions;
/*     */ import org.jacoco.core.tools.ExecDumpClient;
/*     */ import org.jacoco.core.tools.ExecFileLoader;
/*     */ 
/*     */ public class DumpTask extends Task
/*     */ {
/*     */   private boolean dump;
/*     */   private boolean reset;
/*     */   private File destfile;
/*     */   private String address;
/*     */   private int port;
/*     */   private int retryCount;
/*     */   private boolean append;
/*     */ 
/*     */   public DumpTask()
/*     */   {
/*  32 */     this.dump = true;
/*  33 */     this.reset = false;
/*  34 */     this.destfile = null;
/*  35 */     this.address = AgentOptions.DEFAULT_ADDRESS;
/*  36 */     this.port = 6300;
/*  37 */     this.retryCount = 10;
/*  38 */     this.append = true;
/*     */   }
/*     */ 
/*     */   public void setDestfile(File destfile)
/*     */   {
/*  49 */     this.destfile = destfile;
/*     */   }
/*     */ 
/*     */   public void setAddress(String address)
/*     */   {
/*  59 */     this.address = address;
/*     */   }
/*     */ 
/*     */   public void setPort(int port)
/*     */   {
/*  69 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public void setRetryCount(int retryCount)
/*     */   {
/*  80 */     this.retryCount = retryCount;
/*     */   }
/*     */ 
/*     */   public void setAppend(boolean append)
/*     */   {
/*  92 */     this.append = append;
/*     */   }
/*     */ 
/*     */   public void setDump(boolean dump)
/*     */   {
/* 103 */     this.dump = dump;
/*     */   }
/*     */ 
/*     */   public void setReset(boolean reset)
/*     */   {
/* 114 */     this.reset = reset;
/*     */   }
/*     */ 
/*     */   public void execute()
/*     */     throws BuildException
/*     */   {
/* 120 */     if (this.port <= 0) {
/* 121 */       throw new BuildException("Invalid port value", getLocation());
/*     */     }
/* 123 */     if ((this.dump) && (this.destfile == null)) {
/* 124 */       throw new BuildException("Destination file is required when dumping execution data", getLocation());
/*     */     }
/*     */ 
/* 129 */     ExecDumpClient client = new ExecDumpClient()
/*     */     {
/*     */       protected void onConnecting(InetAddress address, int port)
/*     */       {
/* 133 */         DumpTask.this.log(String.format("Connecting to %s:%s", new Object[] { address, Integer.valueOf(port) }));
/*     */       }
/*     */ 
/*     */       protected void onConnectionFailure(IOException exception)
/*     */       {
/* 139 */         DumpTask.this.log(exception.getMessage());
/*     */       }
/*     */     };
/* 142 */     client.setDump(this.dump);
/* 143 */     client.setReset(this.reset);
/* 144 */     client.setRetryCount(this.retryCount);
/*     */     try
/*     */     {
/* 147 */       ExecFileLoader loader = client.dump(this.address, this.port);
/* 148 */       if (this.dump) {
/* 149 */         log(String.format("Dumping execution data to %s", new Object[] { this.destfile.getAbsolutePath() }));
/*     */ 
/* 151 */         loader.save(this.destfile, this.append);
/*     */       }
/*     */     } catch (IOException e) {
/* 154 */       throw new BuildException("Unable to dump coverage data", e, getLocation());
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.ant.DumpTask
 * JD-Core Version:    0.5.4
 */