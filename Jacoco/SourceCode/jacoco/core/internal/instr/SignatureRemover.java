/*     */ package org.jacoco.core.internal.instr;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.Manifest;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class SignatureRemover
/*     */ {
/*  28 */   private static final Pattern SIGNATURE_FILES = Pattern.compile("META-INF/[^/]*\\.SF|META-INF/[^/]*\\.DSA|META-INF/[^/]*\\.RSA|META-INF/SIG-[^/]*");
/*     */   private static final String MANIFEST_MF = "META-INF/MANIFEST.MF";
/*     */   private static final String DIGEST_SUFFIX = "-Digest";
/*     */   private boolean active;
/*     */ 
/*     */   public SignatureRemover()
/*     */   {
/*  44 */     this.active = true;
/*     */   }
/*     */ 
/*     */   public void setActive(boolean active)
/*     */   {
/*  56 */     this.active = active;
/*     */   }
/*     */ 
/*     */   public boolean removeEntry(String name)
/*     */   {
/*  67 */     return (this.active) && (SIGNATURE_FILES.matcher(name).matches());
/*     */   }
/*     */ 
/*     */   public boolean filterEntry(String name, InputStream in, OutputStream out)
/*     */     throws IOException
/*     */   {
/*  85 */     if ((!this.active) || (!"META-INF/MANIFEST.MF".equals(name))) {
/*  86 */       return false;
/*     */     }
/*  88 */     Manifest mf = new Manifest(in);
/*  89 */     filterManifestEntry(mf.getEntries().values());
/*  90 */     mf.write(out);
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */   private void filterManifestEntry(Collection<Attributes> entry) {
/*  95 */     for (Iterator i = entry.iterator(); i.hasNext(); ) {
/*  96 */       Attributes attributes = (Attributes)i.next();
/*  97 */       filterManifestEntryAttributes(attributes);
/*  98 */       if (attributes.isEmpty())
/*  99 */         i.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void filterManifestEntryAttributes(Attributes attrs)
/*     */   {
/* 105 */     for (Iterator i = attrs.keySet().iterator(); i.hasNext(); ) {
/* 106 */       if (String.valueOf(i.next()).endsWith("-Digest"));
/* 107 */       i.remove();
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.internal.instr.SignatureRemover
 * JD-Core Version:    0.5.4
 */