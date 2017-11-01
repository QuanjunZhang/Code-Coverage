/*    */ package org.jacoco.core.internal.instr;
/*    */ 
/*    */ import org.jacoco.core.internal.flow.ClassProbesVisitor;
/*    */ import org.jacoco.core.internal.flow.MethodProbesVisitor;
/*    */ 
/*    */ class ProbeCounter extends ClassProbesVisitor
/*    */ {
/*    */   private int count;
/*    */   private boolean methods;
/*    */ 
/*    */   ProbeCounter()
/*    */   {
/* 27 */     this.count = 0;
/* 28 */     this.methods = false;
/*    */   }
/*    */ 
/*    */   public MethodProbesVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
/*    */   {
/* 34 */     if ((!"<clinit>".equals(name)) && ((access & 0x400) == 0))
/*    */     {
/* 36 */       this.methods = true;
/*    */     }
/* 38 */     return null;
/*    */   }
/*    */ 
/*    */   public void visitTotalProbeCount(int count)
/*    */   {
/* 43 */     this.count = count;
/*    */   }
/*    */ 
/*    */   int getCount() {
/* 47 */     return this.count;
/*    */   }
/*    */ 
/*    */   boolean hasMethods()
/*    */   {
/* 55 */     return this.methods;
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.instr.ProbeCounter
 * JD-Core Version:    0.5.4
 */