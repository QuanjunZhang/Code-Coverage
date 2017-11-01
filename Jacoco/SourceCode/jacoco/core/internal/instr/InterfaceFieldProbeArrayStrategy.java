/*     */ package org.jacoco.core.internal.instr;
/*     */ 
/*     */ import org.jacoco.asm.ClassVisitor;
/*     */ import org.jacoco.asm.Label;
/*     */ import org.jacoco.asm.MethodVisitor;
/*     */ import org.jacoco.core.runtime.IExecutionDataAccessorGenerator;
/*     */ 
/*     */ class InterfaceFieldProbeArrayStrategy
/*     */   implements IProbeArrayStrategy
/*     */ {
/*  30 */   private static final Object[] FRAME_STACK_ARRZ = { "[Z" };
/*     */ 
/*  35 */   private static final Object[] FRAME_LOCALS_EMPTY = new Object[0];
/*     */   private final String className;
/*     */   private final long classId;
/*     */   private final int probeCount;
/*     */   private final IExecutionDataAccessorGenerator accessorGenerator;
/*  42 */   private boolean seenClinit = false;
/*     */ 
/*     */   InterfaceFieldProbeArrayStrategy(String className, long classId, int probeCount, IExecutionDataAccessorGenerator accessorGenerator)
/*     */   {
/*  47 */     this.className = className;
/*  48 */     this.classId = classId;
/*  49 */     this.probeCount = probeCount;
/*  50 */     this.accessorGenerator = accessorGenerator;
/*     */   }
/*     */ 
/*     */   public int storeInstance(MethodVisitor mv, boolean clinit, int variable)
/*     */   {
/*  55 */     if (clinit) {
/*  56 */       int maxStack = this.accessorGenerator.generateDataAccessor(this.classId, this.className, this.probeCount, mv);
/*     */ 
/*  61 */       mv.visitInsn(89);
/*     */ 
/*  66 */       mv.visitFieldInsn(179, this.className, "$jacocoData", "[Z");
/*     */ 
/*  71 */       mv.visitVarInsn(58, variable);
/*     */ 
/*  73 */       this.seenClinit = true;
/*  74 */       return Math.max(maxStack, 2);
/*     */     }
/*  76 */     mv.visitMethodInsn(184, this.className, "$jacocoInit", "()[Z", true);
/*     */ 
/*  79 */     mv.visitVarInsn(58, variable);
/*  80 */     return 1;
/*     */   }
/*     */ 
/*     */   public void addMembers(ClassVisitor cv, int probeCount)
/*     */   {
/*  85 */     createDataField(cv);
/*  86 */     createInitMethod(cv, probeCount);
/*  87 */     if (!this.seenClinit)
/*  88 */       createClinitMethod(cv, probeCount);
/*     */   }
/*     */ 
/*     */   private void createDataField(ClassVisitor cv)
/*     */   {
/*  93 */     cv.visitField(4121, "$jacocoData", "[Z", null, null);
/*     */   }
/*     */ 
/*     */   private void createInitMethod(ClassVisitor cv, int probeCount)
/*     */   {
/*  99 */     MethodVisitor mv = cv.visitMethod(4106, "$jacocoInit", "()[Z", null, null);
/*     */ 
/* 102 */     mv.visitCode();
/*     */ 
/* 105 */     mv.visitFieldInsn(178, this.className, "$jacocoData", "[Z");
/*     */ 
/* 107 */     mv.visitInsn(89);
/*     */ 
/* 113 */     Label alreadyInitialized = new Label();
/* 114 */     mv.visitJumpInsn(199, alreadyInitialized);
/*     */ 
/* 118 */     mv.visitInsn(87);
/* 119 */     int size = this.accessorGenerator.generateDataAccessor(this.classId, this.className, probeCount, mv);
/*     */ 
/* 125 */     mv.visitFrame(-1, 0, FRAME_LOCALS_EMPTY, 1, FRAME_STACK_ARRZ);
/*     */ 
/* 127 */     mv.visitLabel(alreadyInitialized);
/* 128 */     mv.visitInsn(176);
/*     */ 
/* 130 */     mv.visitMaxs(Math.max(size, 2), 0);
/* 131 */     mv.visitEnd();
/*     */   }
/*     */ 
/*     */   private void createClinitMethod(ClassVisitor cv, int probeCount)
/*     */   {
/* 136 */     MethodVisitor mv = cv.visitMethod(4104, "<clinit>", "()V", null, null);
/*     */ 
/* 138 */     mv.visitCode();
/*     */ 
/* 140 */     int maxStack = this.accessorGenerator.generateDataAccessor(this.classId, this.className, probeCount, mv);
/*     */ 
/* 145 */     mv.visitFieldInsn(179, this.className, "$jacocoData", "[Z");
/*     */ 
/* 148 */     mv.visitInsn(177);
/*     */ 
/* 150 */     mv.visitMaxs(maxStack, 0);
/* 151 */     mv.visitEnd();
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.instr.InterfaceFieldProbeArrayStrategy
 * JD-Core Version:    0.5.4
 */