/*    */ package org.jacoco.core.internal.data;
/*    */ 
/*    */ public final class CRC64
/*    */ {
/*    */   private static final long POLY64REV = -2882303761517117440L;
/* 30 */   private static final long[] LOOKUPTABLE = new long[256];
/*    */ 
/*    */   public static long checksum(byte[] data)
/*    */   {
/* 52 */     long sum = 0L;
/* 53 */     for (byte b : data) {
/* 54 */       int lookupidx = ((int)sum ^ b) & 0xFF;
/* 55 */       sum = sum >>> 8 ^ LOOKUPTABLE[lookupidx];
/*    */     }
/* 57 */     return sum;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 31 */     for (int i = 0; i < 256; ++i) {
/* 32 */       long v = i;
/* 33 */       for (int j = 0; j < 8; ++j) {
/* 34 */         if ((v & 1L) == 1L)
/* 35 */           v = v >>> 1 ^ 0x0;
/*    */         else {
/* 37 */           v >>>= 1;
/*    */         }
/*    */       }
/* 40 */       LOOKUPTABLE[i] = v;
/*    */     }
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.data.CRC64
 * JD-Core Version:    0.5.4
 */