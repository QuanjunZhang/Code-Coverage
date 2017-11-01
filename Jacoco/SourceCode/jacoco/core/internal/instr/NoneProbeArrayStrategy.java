/*    */ package org.jacoco.core.internal.instr;
/*    */ 
/*    */ import org.jacoco.asm.ClassVisitor;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ class NoneProbeArrayStrategy
/*    */   implements IProbeArrayStrategy
/*    */ {
/*    */   public int storeInstance(MethodVisitor mv, boolean clinit, int variable)
/*    */   {
/* 25 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   public void addMembers(ClassVisitor delegate, int probeCount)
/*    */   {
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.instr.NoneProbeArrayStrategy
 * JD-Core Version:    0.5.4
 */