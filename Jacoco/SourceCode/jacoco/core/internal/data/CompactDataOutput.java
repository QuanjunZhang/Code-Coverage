/*    */ package org.jacoco.core.internal.data;
/*    */ 
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class CompactDataOutput extends DataOutputStream
/*    */ {
/*    */   public CompactDataOutput(OutputStream out)
/*    */   {
/* 33 */     super(out);
/*    */   }
/*    */ 
/*    */   public void writeVarInt(int value)
/*    */     throws IOException
/*    */   {
/* 47 */     if ((value & 0xFFFFFF80) == 0) {
/* 48 */       writeByte(value);
/*    */     } else {
/* 50 */       writeByte(0x80 | value & 0x7F);
/* 51 */       writeVarInt(value >>> 7);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void writeBooleanArray(boolean[] value)
/*    */     throws IOException
/*    */   {
/* 65 */     writeVarInt(value.length);
/* 66 */     int buffer = 0;
/* 67 */     int bufferSize = 0;
/* 68 */     for (boolean b : value) {
/* 69 */       if (b) {
/* 70 */         buffer |= 1 << bufferSize;
/*    */       }
/* 72 */       if (++bufferSize == 8) {
/* 73 */         writeByte(buffer);
/* 74 */         buffer = 0;
/* 75 */         bufferSize = 0;
/*    */       }
/*    */     }
/* 78 */     if (bufferSize > 0)
/* 79 */       writeByte(buffer);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.data.CompactDataOutput
 * JD-Core Version:    0.5.4
 */