/*    */ package org.jacoco.core.internal.flow;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ import org.jacoco.asm.Opcodes;
/*    */ import org.jacoco.asm.commons.AnalyzerAdapter;
/*    */ 
/*    */ class FrameSnapshot
/*    */   implements IFrame
/*    */ {
/* 27 */   private static final FrameSnapshot NOP = new FrameSnapshot(null, null);
/*    */   private final Object[] locals;
/*    */   private final Object[] stack;
/*    */ 
/*    */   private FrameSnapshot(Object[] locals, Object[] stack)
/*    */   {
/* 33 */     this.locals = locals;
/* 34 */     this.stack = stack;
/*    */   }
/*    */ 
/*    */   static IFrame create(AnalyzerAdapter analyzer, int popCount)
/*    */   {
/* 48 */     if ((analyzer == null) || (analyzer.locals == null)) {
/* 49 */       return NOP;
/*    */     }
/* 51 */     Object[] locals = reduce(analyzer.locals, 0);
/* 52 */     Object[] stack = reduce(analyzer.stack, popCount);
/* 53 */     return new FrameSnapshot(locals, stack);
/*    */   }
/*    */ 
/*    */   private static Object[] reduce(List<Object> source, int popCount)
/*    */   {
/* 62 */     List copy = new ArrayList(source);
/* 63 */     int size = source.size() - popCount;
/* 64 */     copy.subList(size, source.size()).clear();
/* 65 */     for (int i = size; --i >= 0; ) {
/* 66 */       Object type = source.get(i);
/* 67 */       if ((type == Opcodes.LONG) || (type == Opcodes.DOUBLE)) {
/* 68 */         copy.remove(i + 1);
/*    */       }
/*    */     }
/* 71 */     return copy.toArray();
/*    */   }
/*    */ 
/*    */   public void accept(MethodVisitor mv)
/*    */   {
/* 77 */     if (this.locals != null)
/* 78 */       mv.visitFrame(-1, this.locals.length, this.locals, this.stack.length, this.stack);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.flow.FrameSnapshot
 * JD-Core Version:    0.5.4
 */