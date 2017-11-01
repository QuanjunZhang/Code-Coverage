/*     */ package org.jacoco.core.runtime;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ final class CommandLineSupport
/*     */ {
/*     */   private static final char BLANK = ' ';
/*     */   private static final char QUOTE = '"';
/*     */   private static final char SLASH = '\\';
/*     */   private static final int M_STRIPWHITESPACE = 0;
/*     */   private static final int M_PARSEARGUMENT = 1;
/*     */   private static final int M_ESCAPED = 2;
/*     */ 
/*     */   static String quote(String arg)
/*     */   {
/*  34 */     StringBuilder escaped = new StringBuilder();
/*  35 */     for (char c : arg.toCharArray()) {
/*  36 */       if ((c == '"') || (c == '\\')) {
/*  37 */         escaped.append('\\');
/*     */       }
/*  39 */       escaped.append(c);
/*     */     }
/*  41 */     if ((arg.indexOf(' ') != -1) || (arg.indexOf('"') != -1)) {
/*  42 */       escaped.insert(0, '"').append('"');
/*     */     }
/*  44 */     return escaped.toString();
/*     */   }
/*     */ 
/*     */   static String quote(List<String> args)
/*     */   {
/*  56 */     StringBuilder result = new StringBuilder();
/*  57 */     boolean seperate = false;
/*  58 */     for (String arg : args) {
/*  59 */       if (seperate) {
/*  60 */         result.append(' ');
/*     */       }
/*  62 */       result.append(quote(arg));
/*  63 */       seperate = true;
/*     */     }
/*  65 */     return result.toString();
/*     */   }
/*     */ 
/*     */   static List<String> split(String commandline)
/*     */   {
/*  77 */     if ((commandline == null) || (commandline.length() == 0)) {
/*  78 */       return new ArrayList();
/*     */     }
/*  80 */     List args = new ArrayList();
/*  81 */     StringBuilder current = new StringBuilder();
/*  82 */     int mode = 0;
/*  83 */     int endChar = 32;
/*  84 */     for (char c : commandline.toCharArray()) {
/*  85 */       switch (mode)
/*     */       {
/*     */       case 0:
/*  87 */         if (!Character.isWhitespace(c)) {
/*  88 */           if (c == '"') {
/*  89 */             endChar = 34;
/*     */           } else {
/*  91 */             current.append(c);
/*  92 */             endChar = 32;
/*     */           }
/*  94 */           mode = 1; } break;
/*     */       case 1:
/*  98 */         if (c == endChar) {
/*  99 */           addArgument(args, current);
/* 100 */           mode = 0;
/* 101 */         } else if (c == '\\') {
/* 102 */           current.append('\\');
/* 103 */           mode = 2;
/*     */         } else {
/* 105 */           current.append(c);
/*     */         }
/* 107 */         break;
/*     */       case 2:
/* 109 */         if ((c == '"') || (c == '\\')) {
/* 110 */           current.setCharAt(current.length() - 1, c);
/* 111 */         } else if (c == endChar) {
/* 112 */           addArgument(args, current);
/* 113 */           mode = 0;
/*     */         } else {
/* 115 */           current.append(c);
/*     */         }
/* 117 */         mode = 1;
/*     */       }
/*     */     }
/*     */ 
/* 121 */     addArgument(args, current);
/* 122 */     return args;
/*     */   }
/*     */ 
/*     */   private static void addArgument(List<String> args, StringBuilder current)
/*     */   {
/* 127 */     if (current.length() > 0) {
/* 128 */       args.add(current.toString());
/* 129 */       current.setLength(0);
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.runtime.CommandLineSupport
 * JD-Core Version:    0.5.4
 */