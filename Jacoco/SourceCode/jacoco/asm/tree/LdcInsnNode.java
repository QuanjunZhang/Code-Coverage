/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ public class LdcInsnNode extends AbstractInsnNode
/*    */ {
/*    */   public Object cst;
/*    */ 
/*    */   public LdcInsnNode(Object cst)
/*    */   {
/* 60 */     super(18);
/* 61 */     this.cst = cst;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 66 */     return 9;
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 71 */     mv.visitLdcInsn(this.cst);
/* 72 */     acceptAnnotations(mv);
/*    */   }
/*    */ 
/*    */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*    */   {
/* 77 */     return new LdcInsnNode(this.cst).cloneAnnotations(this);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.LdcInsnNode
 * JD-Core Version:    0.5.4
 */