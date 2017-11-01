/*     */ package org.jacoco.asm.tree;
/*     */ 
/*     */ import java.util.Map;
/*     */ import org.jacoco.asm.MethodVisitor;
/*     */ 
/*     */ public class FieldInsnNode extends AbstractInsnNode
/*     */ {
/*     */   public String owner;
/*     */   public String name;
/*     */   public String desc;
/*     */ 
/*     */   public FieldInsnNode(int opcode, String owner, String name, String desc)
/*     */   {
/*  77 */     super(opcode);
/*  78 */     this.owner = owner;
/*  79 */     this.name = name;
/*  80 */     this.desc = desc;
/*     */   }
/*     */ 
/*     */   public void setOpcode(int opcode)
/*     */   {
/*  91 */     this.opcode = opcode;
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/*  96 */     return 4;
/*     */   }
/*     */ 
/*     */   public void accept(MethodVisitor mv)
/*     */   {
/* 101 */     mv.visitFieldInsn(this.opcode, this.owner, this.name, this.desc);
/* 102 */     acceptAnnotations(mv);
/*     */   }
/*     */ 
/*     */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*     */   {
/* 107 */     return new FieldInsnNode(this.opcode, this.owner, this.name, this.desc)
/* 108 */       .cloneAnnotations(this);
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.FieldInsnNode
 * JD-Core Version:    0.5.4
 */