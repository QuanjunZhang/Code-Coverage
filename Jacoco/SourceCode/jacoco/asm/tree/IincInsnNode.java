/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ public class IincInsnNode extends AbstractInsnNode
/*    */ {
/*    */   public int var;
/*    */   public int incr;
/*    */ 
/*    */   public IincInsnNode(int var, int incr)
/*    */   {
/* 63 */     super(132);
/* 64 */     this.var = var;
/* 65 */     this.incr = incr;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 70 */     return 10;
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 75 */     mv.visitIincInsn(this.var, this.incr);
/* 76 */     acceptAnnotations(mv);
/*    */   }
/*    */ 
/*    */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*    */   {
/* 81 */     return new IincInsnNode(this.var, this.incr).cloneAnnotations(this);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.IincInsnNode
 * JD-Core Version:    0.5.4
 */