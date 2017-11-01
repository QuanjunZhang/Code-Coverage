/*     */ package org.jacoco.asm.tree;
/*     */ 
/*     */ import org.jacoco.asm.MethodVisitor;
/*     */ 
/*     */ public class LocalVariableNode
/*     */ {
/*     */   public String name;
/*     */   public String desc;
/*     */   public String signature;
/*     */   public LabelNode start;
/*     */   public LabelNode end;
/*     */   public int index;
/*     */ 
/*     */   public LocalVariableNode(String name, String desc, String signature, LabelNode start, LabelNode end, int index)
/*     */   {
/*  94 */     this.name = name;
/*  95 */     this.desc = desc;
/*  96 */     this.signature = signature;
/*  97 */     this.start = start;
/*  98 */     this.end = end;
/*  99 */     this.index = index;
/*     */   }
/*     */ 
/*     */   public void accept(MethodVisitor mv)
/*     */   {
/* 109 */     mv.visitLocalVariable(this.name, this.desc, this.signature, this.start.getLabel(), this.end
/* 110 */       .getLabel(), this.index);
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.LocalVariableNode
 * JD-Core Version:    0.5.4
 */