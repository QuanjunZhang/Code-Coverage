/*    */ package org.jacoco.report;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jacoco.core.analysis.IBundleCoverage;
/*    */ 
/*    */ class MultiGroupVisitor
/*    */   implements IReportGroupVisitor
/*    */ {
/*    */   private final List<? extends IReportGroupVisitor> visitors;
/*    */ 
/*    */   MultiGroupVisitor(List<? extends IReportGroupVisitor> visitors)
/*    */   {
/* 63 */     this.visitors = visitors;
/*    */   }
/*    */ 
/*    */   public void visitBundle(IBundleCoverage bundle, ISourceFileLocator locator) throws IOException
/*    */   {
/* 68 */     for (IReportGroupVisitor v : this.visitors)
/* 69 */       v.visitBundle(bundle, locator);
/*    */   }
/*    */ 
/*    */   public IReportGroupVisitor visitGroup(String name) throws IOException
/*    */   {
/* 74 */     List children = new ArrayList();
/* 75 */     for (IReportGroupVisitor v : this.visitors) {
/* 76 */       children.add(v.visitGroup(name));
/*    */     }
/* 78 */     return new MultiGroupVisitor(children);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.report.MultiGroupVisitor
 * JD-Core Version:    0.5.4
 */