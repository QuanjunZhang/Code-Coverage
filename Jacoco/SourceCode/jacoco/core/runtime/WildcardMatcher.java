/*    */ package org.jacoco.core.runtime;
/*    */ 
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ public class WildcardMatcher
/*    */ {
/*    */   private final Pattern pattern;
/*    */ 
/*    */   public WildcardMatcher(String expression)
/*    */   {
/* 32 */     String[] parts = expression.split("\\:");
/* 33 */     StringBuilder regex = new StringBuilder(expression.length() * 2);
/* 34 */     boolean next = false;
/* 35 */     for (String part : parts) {
/* 36 */       if (next) {
/* 37 */         regex.append('|');
/*    */       }
/* 39 */       regex.append('(').append(toRegex(part)).append(')');
/* 40 */       next = true;
/*    */     }
/* 42 */     this.pattern = Pattern.compile(regex.toString());
/*    */   }
/*    */ 
/*    */   private static CharSequence toRegex(String expression) {
/* 46 */     StringBuilder regex = new StringBuilder(expression.length() * 2);
/* 47 */     for (char c : expression.toCharArray()) {
/* 48 */       switch (c)
/*    */       {
/*    */       case '?':
/* 50 */         regex.append(".?");
/* 51 */         break;
/*    */       case '*':
/* 53 */         regex.append(".*");
/* 54 */         break;
/*    */       default:
/* 56 */         regex.append(Pattern.quote(String.valueOf(c)));
/*    */       }
/*    */     }
/*    */ 
/* 60 */     return regex;
/*    */   }
/*    */ 
/*    */   public boolean matches(String s)
/*    */   {
/* 71 */     return this.pattern.matcher(s).matches();
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.runtime.WildcardMatcher
 * JD-Core Version:    0.5.4
 */