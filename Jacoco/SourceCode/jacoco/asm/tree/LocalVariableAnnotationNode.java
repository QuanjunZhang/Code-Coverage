/*     */ package org.jacoco.asm.tree;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.jacoco.asm.Label;
/*     */ import org.jacoco.asm.MethodVisitor;
/*     */ import org.jacoco.asm.TypePath;
/*     */ 
/*     */ public class LocalVariableAnnotationNode extends TypeAnnotationNode
/*     */ {
/*     */   public List<LabelNode> start;
/*     */   public List<LabelNode> end;
/*     */   public List<Integer> index;
/*     */ 
/*     */   public LocalVariableAnnotationNode(int typeRef, TypePath typePath, LabelNode[] start, LabelNode[] end, int[] index, String desc)
/*     */   {
/*  96 */     this(327680, typeRef, typePath, start, end, index, desc);
/*     */   }
/*     */ 
/*     */   public LocalVariableAnnotationNode(int api, int typeRef, TypePath typePath, LabelNode[] start, LabelNode[] end, int[] index, String desc)
/*     */   {
/* 126 */     super(api, typeRef, typePath, desc);
/* 127 */     this.start = new ArrayList(start.length);
/* 128 */     this.start.addAll(Arrays.asList(start));
/* 129 */     this.end = new ArrayList(end.length);
/* 130 */     this.end.addAll(Arrays.asList(end));
/* 131 */     this.index = new ArrayList(index.length);
/* 132 */     for (int i : index)
/* 133 */       this.index.add(Integer.valueOf(i));
/*     */   }
/*     */ 
/*     */   public void accept(MethodVisitor mv, boolean visible)
/*     */   {
/* 146 */     Label[] start = new Label[this.start.size()];
/* 147 */     Label[] end = new Label[this.end.size()];
/* 148 */     int[] index = new int[this.index.size()];
/* 149 */     for (int i = 0; i < start.length; ++i) {
/* 150 */       start[i] = ((LabelNode)this.start.get(i)).getLabel();
/* 151 */       end[i] = ((LabelNode)this.end.get(i)).getLabel();
/* 152 */       index[i] = ((Integer)this.index.get(i)).intValue();
/*     */     }
/* 154 */     accept(mv.visitLocalVariableAnnotation(this.typeRef, this.typePath, start, end, index, this.desc, true));
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.LocalVariableAnnotationNode
 * JD-Core Version:    0.5.4
 */