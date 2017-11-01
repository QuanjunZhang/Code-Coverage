/*     */ package org.jacoco.core.internal.analysis;
/*     */ 
/*     */ import org.jacoco.core.analysis.ICounter;
/*     */ import org.jacoco.core.analysis.ILine;
/*     */ 
/*     */ public abstract class LineImpl
/*     */   implements ILine
/*     */ {
/*     */   private static final int SINGLETON_INS_LIMIT = 8;
/*     */   private static final int SINGLETON_BRA_LIMIT = 4;
/*  28 */   private static final LineImpl[][][][] SINGLETONS = new LineImpl[9][][][];
/*     */   public static final LineImpl EMPTY;
/*     */   protected CounterImpl instructions;
/*     */   protected CounterImpl branches;
/*     */ 
/*     */   private static LineImpl getInstance(CounterImpl instructions, CounterImpl branches)
/*     */   {
/*  52 */     int im = instructions.getMissedCount();
/*  53 */     int ic = instructions.getCoveredCount();
/*  54 */     int bm = branches.getMissedCount();
/*  55 */     int bc = branches.getCoveredCount();
/*  56 */     if ((im <= 8) && (ic <= 8) && (bm <= 4) && (bc <= 4))
/*     */     {
/*  58 */       return SINGLETONS[im][ic][bm][bc];
/*     */     }
/*  60 */     return new Var(instructions, branches);
/*     */   }
/*     */ 
/*     */   private LineImpl(CounterImpl instructions, CounterImpl branches)
/*     */   {
/* 104 */     this.instructions = instructions;
/* 105 */     this.branches = branches;
/*     */   }
/*     */ 
/*     */   public abstract LineImpl increment(ICounter paramICounter1, ICounter paramICounter2);
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 123 */     return this.instructions.getStatus() | this.branches.getStatus();
/*     */   }
/*     */ 
/*     */   public ICounter getInstructionCounter() {
/* 127 */     return this.instructions;
/*     */   }
/*     */ 
/*     */   public ICounter getBranchCounter() {
/* 131 */     return this.branches;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 136 */     return 23 * this.instructions.hashCode() ^ this.branches.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 141 */     if (obj instanceof ILine) {
/* 142 */       ILine that = (ILine)obj;
/* 143 */       return (this.instructions.equals(that.getInstructionCounter())) && (this.branches.equals(that.getBranchCounter()));
/*     */     }
/*     */ 
/* 146 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  31 */     for (int i = 0; i <= 8; ++i) {
/*  32 */       SINGLETONS[i] = new LineImpl[9][][];
/*  33 */       for (int j = 0; j <= 8; ++j) {
/*  34 */         SINGLETONS[i][j] = new LineImpl[5][];
/*  35 */         for (int k = 0; k <= 4; ++k) {
/*  36 */           SINGLETONS[i][j][k] = new LineImpl[5];
/*  37 */           for (int l = 0; l <= 4; ++l) {
/*  38 */             SINGLETONS[i][j][k][l] = new Fix(i, j, k, l);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  48 */     EMPTY = SINGLETONS[0][0][0][0];
/*     */   }
/*     */ 
/*     */   private static final class Fix extends LineImpl
/*     */   {
/*     */     public Fix(int im, int ic, int bm, int bc)
/*     */     {
/*  85 */       super(CounterImpl.getInstance(im, ic), CounterImpl.getInstance(bm, bc), null);
/*     */     }
/*     */ 
/*     */     public LineImpl increment(ICounter instructions, ICounter branches)
/*     */     {
/*  92 */       return LineImpl.access$100(this.instructions.increment(instructions), this.branches.increment(branches));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class Var extends LineImpl
/*     */   {
/*     */     Var(CounterImpl instructions, CounterImpl branches)
/*     */     {
/*  68 */       super(instructions, branches, null);
/*     */     }
/*     */ 
/*     */     public LineImpl increment(ICounter instructions, ICounter branches)
/*     */     {
/*  74 */       this.instructions = this.instructions.increment(instructions);
/*  75 */       this.branches = this.branches.increment(branches);
/*  76 */       return this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.analysis.LineImpl
 * JD-Core Version:    0.5.4
 */