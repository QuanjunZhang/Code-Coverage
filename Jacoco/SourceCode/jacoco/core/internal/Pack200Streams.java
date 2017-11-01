/*    */ package org.jacoco.core.internal;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.jar.JarInputStream;
/*    */ import java.util.jar.JarOutputStream;
/*    */ import java.util.jar.Pack200;
/*    */ import java.util.jar.Pack200.Packer;
/*    */ import java.util.jar.Pack200.Unpacker;
/*    */ 
/*    */ public final class Pack200Streams
/*    */ {
/*    */   public static InputStream unpack(InputStream input)
/*    */     throws IOException
/*    */   {
/* 40 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
/* 41 */     JarOutputStream jar = new JarOutputStream(buffer);
/* 42 */     Pack200.newUnpacker().unpack(new NoCloseInput(input), jar);
/* 43 */     jar.finish();
/* 44 */     return new ByteArrayInputStream(buffer.toByteArray());
/*    */   }
/*    */ 
/*    */   public static void pack(byte[] source, OutputStream output)
/*    */     throws IOException
/*    */   {
/* 59 */     JarInputStream jar = new JarInputStream(new ByteArrayInputStream(source));
/*    */ 
/* 61 */     Pack200.newPacker().pack(jar, output);
/*    */   }
/*    */ 
/*    */   private static class NoCloseInput extends FilterInputStream {
/*    */     protected NoCloseInput(InputStream in) {
/* 66 */       super(in);
/*    */     }
/*    */ 
/*    */     public void close()
/*    */       throws IOException
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.Pack200Streams
 * JD-Core Version:    0.5.4
 */