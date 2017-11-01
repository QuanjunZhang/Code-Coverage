/*    */ package org.jacoco.core.runtime;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.jacoco.core.data.ExecutionDataReader;
/*    */ import org.jacoco.core.internal.data.CompactDataInput;
/*    */ 
/*    */ public class RemoteControlReader extends ExecutionDataReader
/*    */ {
/*    */   private IRemoteCommandVisitor remoteCommandVisitor;
/*    */ 
/*    */   public RemoteControlReader(InputStream input)
/*    */     throws IOException
/*    */   {
/* 35 */     super(input);
/*    */   }
/*    */ 
/*    */   protected boolean readBlock(byte blockid) throws IOException
/*    */   {
/* 40 */     switch (blockid)
/*    */     {
/*    */     case 64:
/* 42 */       readDumpCommand();
/* 43 */       return true;
/*    */     case 32:
/* 45 */       return false;
/*    */     }
/* 47 */     return super.readBlock(blockid);
/*    */   }
/*    */ 
/*    */   public void setRemoteCommandVisitor(IRemoteCommandVisitor visitor)
/*    */   {
/* 58 */     this.remoteCommandVisitor = visitor;
/*    */   }
/*    */ 
/*    */   private void readDumpCommand() throws IOException {
/* 62 */     if (this.remoteCommandVisitor == null) {
/* 63 */       throw new IOException("No remote command visitor.");
/*    */     }
/* 65 */     boolean dump = this.in.readBoolean();
/* 66 */     boolean reset = this.in.readBoolean();
/* 67 */     this.remoteCommandVisitor.visitDumpCommand(dump, reset);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.runtime.RemoteControlReader
 * JD-Core Version:    0.5.4
 */