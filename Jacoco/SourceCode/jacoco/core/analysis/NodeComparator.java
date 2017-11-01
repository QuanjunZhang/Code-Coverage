/*    */ package org.jacoco.core.analysis;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ 
/*    */ public class NodeComparator
/*    */   implements Comparator<ICoverageNode>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 8550521643608826519L;
/*    */   private final Comparator<ICounter> counterComparator;
/*    */   private final ICoverageNode.CounterEntity entity;
/*    */ 
/*    */   NodeComparator(Comparator<ICounter> counterComparator, ICoverageNode.CounterEntity entity)
/*    */   {
/* 39 */     this.counterComparator = counterComparator;
/* 40 */     this.entity = entity;
/*    */   }
/*    */ 
/*    */   public NodeComparator second(Comparator<ICoverageNode> second)
/*    */   {
/* 52 */     Comparator first = this;
/* 53 */     return new NodeComparator(null, null, first, second)
/*    */     {
/*    */       private static final long serialVersionUID = -5515272752138802838L;
/*    */ 
/*    */       public int compare(ICoverageNode o1, ICoverageNode o2)
/*    */       {
/* 59 */         int result = this.val$first.compare(o1, o2);
/* 60 */         return (result == 0) ? this.val$second.compare(o1, o2) : result;
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public <T extends ICoverageNode> List<T> sort(Collection<T> summaries)
/*    */   {
/* 76 */     List result = new ArrayList(summaries);
/* 77 */     Collections.sort(result, this);
/* 78 */     return result;
/*    */   }
/*    */ 
/*    */   public int compare(ICoverageNode n1, ICoverageNode n2) {
/* 82 */     ICounter c1 = n1.getCounter(this.entity);
/* 83 */     ICounter c2 = n2.getCounter(this.entity);
/* 84 */     return this.counterComparator.compare(c1, c2);
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.analysis.NodeComparator
 * JD-Core Version:    0.5.4
 */