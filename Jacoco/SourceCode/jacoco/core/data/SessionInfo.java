/*    */ package org.jacoco.core.data;
/*    */ 
/*    */ public class SessionInfo
/*    */   implements Comparable<SessionInfo>
/*    */ {
/*    */   private final String id;
/*    */   private final long start;
/*    */   private final long dump;
/*    */ 
/*    */   public SessionInfo(String id, long start, long dump)
/*    */   {
/* 39 */     if (id == null) {
/* 40 */       throw new IllegalArgumentException();
/*    */     }
/* 42 */     this.id = id;
/* 43 */     this.start = start;
/* 44 */     this.dump = dump;
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 51 */     return this.id;
/*    */   }
/*    */ 
/*    */   public long getStartTimeStamp()
/*    */   {
/* 59 */     return this.start;
/*    */   }
/*    */ 
/*    */   public long getDumpTimeStamp()
/*    */   {
/* 66 */     return this.dump;
/*    */   }
/*    */ 
/*    */   public int compareTo(SessionInfo other) {
/* 70 */     if (this.dump < other.dump) {
/* 71 */       return -1;
/*    */     }
/* 73 */     if (this.dump > other.dump) {
/* 74 */       return 1;
/*    */     }
/* 76 */     return 0;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 81 */     return "SessionInfo[" + this.id + "]";
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.data.SessionInfo
 * JD-Core Version:    0.5.4
 */