/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ public class ParameterNode
/*    */ {
/*    */   public String name;
/*    */   public int access;
/*    */ 
/*    */   public ParameterNode(String name, int access)
/*    */   {
/* 63 */     this.name = name;
/* 64 */     this.access = access;
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 74 */     mv.visitParameter(this.name, this.access);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.ParameterNode
 * JD-Core Version:    0.5.4
 */