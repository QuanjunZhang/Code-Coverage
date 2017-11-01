/*     */ package org.jacoco.asm.tree;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.jacoco.asm.MethodVisitor;
/*     */ 
/*     */ public class TryCatchBlockNode
/*     */ {
/*     */   public LabelNode start;
/*     */   public LabelNode end;
/*     */   public LabelNode handler;
/*     */   public String type;
/*     */   public List<TypeAnnotationNode> visibleTypeAnnotations;
/*     */   public List<TypeAnnotationNode> invisibleTypeAnnotations;
/*     */ 
/*     */   public TryCatchBlockNode(LabelNode start, LabelNode end, LabelNode handler, String type)
/*     */   {
/* 100 */     this.start = start;
/* 101 */     this.end = end;
/* 102 */     this.handler = handler;
/* 103 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public void updateIndex(int index)
/*     */   {
/* 116 */     int newTypeRef = 0x42000000 | index << 8;
/* 117 */     if (this.visibleTypeAnnotations != null) {
/* 118 */       for (TypeAnnotationNode tan : this.visibleTypeAnnotations) {
/* 119 */         tan.typeRef = newTypeRef;
/*     */       }
/*     */     }
/* 122 */     if (this.invisibleTypeAnnotations != null)
/* 123 */       for (TypeAnnotationNode tan : this.invisibleTypeAnnotations)
/* 124 */         tan.typeRef = newTypeRef;
/*     */   }
/*     */ 
/*     */   public void accept(MethodVisitor mv)
/*     */   {
/* 136 */     mv.visitTryCatchBlock(this.start.getLabel(), this.end.getLabel(), (this.handler == null) ? null : this.handler
/* 137 */       .getLabel(), this.type);
/*     */ 
/* 139 */     int n = (this.visibleTypeAnnotations == null) ? 0 : this.visibleTypeAnnotations
/* 139 */       .size();
/* 140 */     for (int i = 0; i < n; ++i) {
/* 141 */       TypeAnnotationNode an = (TypeAnnotationNode)this.visibleTypeAnnotations.get(i);
/* 142 */       an.accept(mv.visitTryCatchAnnotation(an.typeRef, an.typePath, an.desc, true));
/*     */     }
/*     */ 
/* 146 */     n = (this.invisibleTypeAnnotations == null) ? 0 : this.invisibleTypeAnnotations
/* 146 */       .size();
/* 147 */     for (int i = 0; i < n; ++i) {
/* 148 */       TypeAnnotationNode an = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(i);
/* 149 */       an.accept(mv.visitTryCatchAnnotation(an.typeRef, an.typePath, an.desc, false));
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.TryCatchBlockNode
 * JD-Core Version:    0.5.4
 */