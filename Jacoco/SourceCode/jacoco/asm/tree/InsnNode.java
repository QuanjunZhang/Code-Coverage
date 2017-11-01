/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ public class InsnNode extends AbstractInsnNode
/*    */ {
/*    */   public InsnNode(int opcode)
/*    */   {
/* 64 */     super(opcode);
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 69 */     return 0;
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 80 */     mv.visitInsn(this.opcode);
/* 81 */     acceptAnnotations(mv);
/*    */   }
/*    */ 
/*    */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*    */   {
/* 86 */     return new InsnNode(this.opcode).cloneAnnotations(this);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.InsnNode
 * JD-Core Version:    0.5.4
 */