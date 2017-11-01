/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ public class JumpInsnNode extends AbstractInsnNode
/*    */ {
/*    */   public LabelNode label;
/*    */ 
/*    */   public JumpInsnNode(int opcode, LabelNode label)
/*    */   {
/* 64 */     super(opcode);
/* 65 */     this.label = label;
/*    */   }
/*    */ 
/*    */   public void setOpcode(int opcode)
/*    */   {
/* 78 */     this.opcode = opcode;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 83 */     return 7;
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 88 */     mv.visitJumpInsn(this.opcode, this.label.getLabel());
/* 89 */     acceptAnnotations(mv);
/*    */   }
/*    */ 
/*    */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*    */   {
/* 94 */     return new JumpInsnNode(this.opcode, clone(this.label, labels))
/* 95 */       .cloneAnnotations(this);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.JumpInsnNode
 * JD-Core Version:    0.5.4
 */