/*    */ package org.jacoco.asm;
/*    */ 
/*    */ class CurrentFrame extends Frame
/*    */ {
/*    */   void execute(int opcode, int arg, ClassWriter cw, Item item)
/*    */   {
/* 50 */     super.execute(opcode, arg, cw, item);
/* 51 */     Frame successor = new Frame();
/* 52 */     merge(cw, successor, 0);
/* 53 */     set(successor);
/* 54 */     this.owner.inputStackTop = 0;
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.asm.CurrentFrame
 * JD-Core Version:    0.5.4
 */