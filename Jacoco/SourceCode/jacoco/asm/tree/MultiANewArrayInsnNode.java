/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ public class MultiANewArrayInsnNode extends AbstractInsnNode
/*    */ {
/*    */   public String desc;
/*    */   public int dims;
/*    */ 
/*    */   public MultiANewArrayInsnNode(String desc, int dims)
/*    */   {
/* 63 */     super(197);
/* 64 */     this.desc = desc;
/* 65 */     this.dims = dims;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 70 */     return 13;
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 75 */     mv.visitMultiANewArrayInsn(this.desc, this.dims);
/* 76 */     acceptAnnotations(mv);
/*    */   }
/*    */ 
/*    */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*    */   {
/* 81 */     return new MultiANewArrayInsnNode(this.desc, this.dims).cloneAnnotations(this);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.MultiANewArrayInsnNode
 * JD-Core Version:    0.5.4
 */