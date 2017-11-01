/*     */ package org.jacoco.agent;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ 
/*     */ public final class AgentJar
/*     */ {
/*     */   private static final String RESOURCE = "/jacocoagent.jar";
/* 125 */   private static final String ERRORMSG = String.format("The resource %s has not been found. Please see /org.jacoco.agent/README.TXT for details.", new Object[] { "/jacocoagent.jar" });
/*     */ 
/*     */   public static URL getResource()
/*     */   {
/*  44 */     URL url = AgentJar.class.getResource("/jacocoagent.jar");
/*  45 */     if (url == null) {
/*  46 */       throw new AssertionError(ERRORMSG);
/*     */     }
/*  48 */     return url;
/*     */   }
/*     */ 
/*     */   public static InputStream getResourceAsStream()
/*     */   {
/*  57 */     InputStream stream = AgentJar.class.getResourceAsStream("/jacocoagent.jar");
/*  58 */     if (stream == null) {
/*  59 */       throw new AssertionError(ERRORMSG);
/*     */     }
/*  61 */     return stream;
/*     */   }
/*     */ 
/*     */   public static File extractToTempLocation()
/*     */     throws IOException
/*     */   {
/*  74 */     File agentJar = File.createTempFile("jacocoagent", ".jar");
/*  75 */     agentJar.deleteOnExit();
/*     */ 
/*  77 */     extractTo(agentJar);
/*     */ 
/*  79 */     return agentJar;
/*     */   }
/*     */ 
/*     */   public static void extractTo(File destination)
/*     */     throws IOException
/*     */   {
/*  91 */     InputStream inputJarStream = getResourceAsStream();
/*  92 */     OutputStream outputJarStream = null;
/*     */     try
/*     */     {
/*  96 */       outputJarStream = new FileOutputStream(destination);
/*     */ 
/*  98 */       byte[] buffer = new byte[8192];
/*     */ 
/* 101 */       while ((bytesRead = inputJarStream.read(buffer)) != -1)
/*     */       {
/*     */         int bytesRead;
/* 102 */         outputJarStream.write(buffer, 0, bytesRead);
/*     */       }
/*     */     } finally {
/* 105 */       safeClose(inputJarStream);
/* 106 */       safeClose(outputJarStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void safeClose(Closeable closeable)
/*     */   {
/*     */     try
/*     */     {
/* 118 */       if (closeable != null)
/* 119 */         closeable.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.agent.AgentJar
 * JD-Core Version:    0.5.4
 */