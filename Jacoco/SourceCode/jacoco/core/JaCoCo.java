/*    */ package org.jacoco.core;
/*    */ 
/*    */ import java.util.ResourceBundle;
/*    */ 
/*    */ public final class JaCoCo
/*    */ {
/*    */   public static final String VERSION;
/*    */   public static final String HOMEURL;
/*    */   public static final String RUNTIMEPACKAGE;
/*    */ 
/*    */   static
/*    */   {
/* 31 */     ResourceBundle bundle = ResourceBundle.getBundle("org.jacoco.core.jacoco");
/*    */ 
/* 33 */     VERSION = bundle.getString("VERSION");
/* 34 */     HOMEURL = bundle.getString("HOMEURL");
/* 35 */     RUNTIMEPACKAGE = bundle.getString("RUNTIMEPACKAGE");
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.JaCoCo
 * JD-Core Version:    0.5.4
 */