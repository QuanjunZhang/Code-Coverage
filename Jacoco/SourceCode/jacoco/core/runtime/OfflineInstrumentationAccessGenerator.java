/*    */ package org.jacoco.core.runtime;
/*    */ 
/*    */ import org.jacoco.asm.MethodVisitor;
/*    */ import org.jacoco.core.JaCoCo;
/*    */ import org.jacoco.core.internal.instr.InstrSupport;
/*    */ 
/*    */ public class OfflineInstrumentationAccessGenerator
/*    */   implements IExecutionDataAccessorGenerator
/*    */ {
/*    */   private final String runtimeClassName;
/*    */ 
/*    */   public OfflineInstrumentationAccessGenerator()
/*    */   {
/* 34 */     this(JaCoCo.RUNTIMEPACKAGE.replace('.', '/') + "/Offline");
/*    */   }
/*    */ 
/*    */   OfflineInstrumentationAccessGenerator(String runtimeClassName)
/*    */   {
/* 45 */     this.runtimeClassName = runtimeClassName;
/*    */   }
/*    */ 
/*    */   public int generateDataAccessor(long classid, String classname, int probecount, MethodVisitor mv)
/*    */   {
/* 50 */     mv.visitLdcInsn(Long.valueOf(classid));
/* 51 */     mv.visitLdcInsn(classname);
/* 52 */     InstrSupport.push(mv, probecount);
/* 53 */     mv.visitMethodInsn(184, this.runtimeClassName, "getProbes", "(JLjava/lang/String;I)[Z", false);
/*    */ 
/* 55 */     return 4;
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.runtime.OfflineInstrumentationAccessGenerator
 * JD-Core Version:    0.5.4
 */