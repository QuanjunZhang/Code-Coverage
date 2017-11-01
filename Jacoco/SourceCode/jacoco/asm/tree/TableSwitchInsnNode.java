/*     */ package org.jacoco.asm.tree;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.jacoco.asm.Label;
/*     */ import org.jacoco.asm.MethodVisitor;
/*     */ 
/*     */ public class TableSwitchInsnNode extends AbstractInsnNode
/*     */ {
/*     */   public int min;
/*     */   public int max;
/*     */   public LabelNode dflt;
/*     */   public List<LabelNode> labels;
/*     */ 
/*     */   public TableSwitchInsnNode(int min, int max, LabelNode dflt, LabelNode[] labels)
/*     */   {
/*  84 */     super(170);
/*  85 */     this.min = min;
/*  86 */     this.max = max;
/*  87 */     this.dflt = dflt;
/*  88 */     this.labels = new ArrayList();
/*  89 */     if (labels != null)
/*  90 */       this.labels.addAll(Arrays.asList(labels));
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/*  96 */     return 11;
/*     */   }
/*     */ 
/*     */   public void accept(MethodVisitor mv)
/*     */   {
/* 101 */     Label[] labels = new Label[this.labels.size()];
/* 102 */     for (int i = 0; i < labels.length; ++i) {
/* 103 */       labels[i] = ((LabelNode)this.labels.get(i)).getLabel();
/*     */     }
/* 105 */     mv.visitTableSwitchInsn(this.min, this.max, this.dflt.getLabel(), labels);
/* 106 */     acceptAnnotations(mv);
/*     */   }
/*     */ 
/*     */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*     */   {
/* 111 */     return new TableSwitchInsnNode(this.min, this.max, clone(this.dflt, labels), clone(this.labels, labels))
/* 112 */       .cloneAnnotations(this);
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.TableSwitchInsnNode
 * JD-Core Version:    0.5.4
 */