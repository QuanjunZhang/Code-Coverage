/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import org.jacoco.asm.TypePath;
/*    */ 
/*    */ public class TypeAnnotationNode extends AnnotationNode
/*    */ {
/*    */   public int typeRef;
/*    */   public TypePath typePath;
/*    */ 
/*    */   public TypeAnnotationNode(int typeRef, TypePath typePath, String desc)
/*    */   {
/* 73 */     this(327680, typeRef, typePath, desc);
/* 74 */     if (super.getClass() != TypeAnnotationNode.class)
/* 75 */       throw new IllegalStateException();
/*    */   }
/*    */ 
/*    */   public TypeAnnotationNode(int api, int typeRef, TypePath typePath, String desc)
/*    */   {
/* 96 */     super(api, desc);
/* 97 */     this.typeRef = typeRef;
/* 98 */     this.typePath = typePath;
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.TypeAnnotationNode
 * JD-Core Version:    0.5.4
 */