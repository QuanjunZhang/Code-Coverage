/*     */ package org.jacoco.core.tools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import org.jacoco.core.runtime.RemoteControlReader;
/*     */ import org.jacoco.core.runtime.RemoteControlWriter;
/*     */ 
/*     */ public class ExecDumpClient
/*     */ {
/*     */   private boolean dump;
/*     */   private boolean reset;
/*     */   private int retryCount;
/*     */   private long retryDelay;
/*     */ 
/*     */   public ExecDumpClient()
/*     */   {
/*  38 */     this.dump = true;
/*  39 */     this.reset = false;
/*  40 */     this.retryCount = 0;
/*  41 */     setRetryDelay(1000L);
/*     */   }
/*     */ 
/*     */   public void setDump(boolean dump)
/*     */   {
/*  51 */     this.dump = dump;
/*     */   }
/*     */ 
/*     */   public void setReset(boolean reset)
/*     */   {
/*  61 */     this.reset = reset;
/*     */   }
/*     */ 
/*     */   public void setRetryCount(int retryCount)
/*     */   {
/*  72 */     this.retryCount = retryCount;
/*     */   }
/*     */ 
/*     */   public void setRetryDelay(long retryDelay)
/*     */   {
/*  82 */     this.retryDelay = retryDelay;
/*     */   }
/*     */ 
/*     */   public ExecFileLoader dump(String address, int port)
/*     */     throws IOException
/*     */   {
/*  98 */     return dump(InetAddress.getByName(address), port);
/*     */   }
/*     */ 
/*     */   public ExecFileLoader dump(InetAddress address, int port)
/*     */     throws IOException
/*     */   {
/* 114 */     ExecFileLoader loader = new ExecFileLoader();
/* 115 */     Socket socket = tryConnect(address, port);
/*     */     try {
/* 117 */       RemoteControlWriter remoteWriter = new RemoteControlWriter(socket.getOutputStream());
/*     */ 
/* 119 */       RemoteControlReader remoteReader = new RemoteControlReader(socket.getInputStream());
/*     */ 
/* 121 */       remoteReader.setSessionInfoVisitor(loader.getSessionInfoStore());
/* 122 */       remoteReader.setExecutionDataVisitor(loader.getExecutionDataStore());
/*     */ 
/* 125 */       remoteWriter.visitDumpCommand(this.dump, this.reset);
/* 126 */       remoteReader.read();
/*     */     }
/*     */     finally {
/* 129 */       socket.close();
/*     */     }
/* 131 */     return loader;
/*     */   }
/*     */ 
/*     */   private Socket tryConnect(InetAddress address, int port) throws IOException
/*     */   {
/* 136 */     int count = 0;
/*     */     while (true)
/*     */       try {
/* 139 */         onConnecting(address, port);
/* 140 */         return new Socket(address, port);
/*     */       } catch (IOException e) {
/* 142 */         if (++count > this.retryCount) {
/* 143 */           throw e;
/*     */         }
/* 145 */         onConnectionFailure(e);
/* 146 */         sleep();
/*     */       }
/*     */   }
/*     */ 
/*     */   private void sleep() throws InterruptedIOException
/*     */   {
/*     */     try {
/* 153 */       Thread.sleep(this.retryDelay);
/*     */     } catch (InterruptedException e) {
/* 155 */       throw new InterruptedIOException();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void onConnecting(InetAddress address, int port)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void onConnectionFailure(IOException exception)
/*     */   {
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.tools.ExecDumpClient
 * JD-Core Version:    0.5.4
 */