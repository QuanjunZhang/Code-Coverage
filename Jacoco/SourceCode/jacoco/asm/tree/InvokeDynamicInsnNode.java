/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.jacoco.asm.Handle;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ public class InvokeDynamicInsnNode extends AbstractInsnNode
/*    */ {
/*    */   public String name;
/*    */   public String desc;
/*    */   public Handle bsm;
/*    */   public Object[] bsmArgs;
/*    */ 
/*    */   public InvokeDynamicInsnNode(String name, String desc, Handle bsm, Object[] bsmArgs)
/*    */   {
/* 79 */     super(186);
/* 80 */     this.name = name;
/* 81 */     this.desc = desc;
/* 82 */     this.bsm = bsm;
/* 83 */     this.bsmArgs = bsmArgs;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 88 */     return 6;
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 93 */     mv.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
/* 94 */     acceptAnnotations(mv);
/*    */   }
/*    */ 
/*    */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*    */   {
/* 99 */     return new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs)
/* 100 */       .cloneAnnotations(this);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.InvokeDynamicInsnNode
 * JD-Core Version:    0.5.4
 */