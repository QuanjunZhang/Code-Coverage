/*    */ package org.jacoco.asm.tree;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ 
/*    */ public class LineNumberNode extends AbstractInsnNode
/*    */ {
/*    */   public int line;
/*    */   public LabelNode start;
/*    */ 
/*    */   public LineNumberNode(int line, LabelNode start)
/*    */   {
/* 65 */     super(-1);
/* 66 */     this.line = line;
/* 67 */     this.start = start;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 72 */     return 15;
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 77 */     mv.visitLineNumber(this.line, this.start.getLabel());
/*    */   }
/*    */ 
/*    */   public AbstractInsnNode clone(Map<LabelNode, LabelNode> labels)
/*    */   {
/* 82 */     return new LineNumberNode(this.line, clone(this.start, labels));
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.tree.LineNumberNode
 * JD-Core Version:    0.5.4
 */