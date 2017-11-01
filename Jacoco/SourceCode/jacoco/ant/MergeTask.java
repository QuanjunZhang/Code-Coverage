/*    */ package org.jacoco.ant;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.Iterator;
/*    */ import org.apache.tools.ant.BuildException;
/*    */ import org.apache.tools.ant.Task;
/*    */ import org.apache.tools.ant.types.Resource;
/*    */ import org.apache.tools.ant.types.ResourceCollection;
/*    */ import org.apache.tools.ant.types.resources.Union;
/*    */ import org.apache.tools.ant.util.FileUtils;
/*    */ import org.jacoco.core.tools.ExecFileLoader;
/*    */ 
/*    */ public class MergeTask extends Task
/*    */ {
/*    */   private File destfile;
/*    */   private final Union files;
/*    */ 
/*    */   public MergeTask()
/*    */   {
/* 36 */     this.files = new Union();
/*    */   }
/*    */ 
/*    */   public void setDestfile(File destfile)
/*    */   {
/* 45 */     this.destfile = destfile;
/*    */   }
/*    */ 
/*    */   public void addConfigured(ResourceCollection resources)
/*    */   {
/* 55 */     this.files.add(resources);
/*    */   }
/*    */ 
/*    */   public void execute() throws BuildException
/*    */   {
/* 60 */     if (this.destfile == null) {
/* 61 */       throw new BuildException("Destination file must be supplied", getLocation());
/*    */     }
/*    */ 
/* 65 */     ExecFileLoader loader = new ExecFileLoader();
/*    */ 
/* 67 */     load(loader);
/* 68 */     save(loader);
/*    */   }
/*    */ 
/*    */   private void load(ExecFileLoader loader) {
/* 72 */     Iterator resourceIterator = this.files.iterator();
/* 73 */     while (resourceIterator.hasNext()) {
/* 74 */       Resource resource = (Resource)resourceIterator.next();
/*    */ 
/* 76 */       if (resource.isDirectory()) {
/*    */         continue;
/*    */       }
/*    */ 
/* 80 */       log(String.format("Loading execution data file %s", new Object[] { resource }));
/*    */ 
/* 82 */       InputStream resourceStream = null;
/*    */       try {
/* 84 */         resourceStream = resource.getInputStream();
/* 85 */         loader.load(resourceStream);
/*    */       } catch (IOException e) {
/* 87 */         throw new BuildException(String.format("Unable to read %s", new Object[1]), e, getLocation());
/*    */       }
/*    */       finally {
/* 90 */         FileUtils.close(resourceStream);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private void save(ExecFileLoader loader) {
/* 96 */     log(String.format("Writing merged execution data to %s", new Object[] { this.destfile.getAbsolutePath() }));
/*    */     try
/*    */     {
/* 99 */       loader.save(this.destfile, false);
/*    */     } catch (IOException e) {
/* 101 */       throw new BuildException(String.format("Unable to write merged file %s", new Object[] { this.destfile.getAbsolutePath() }), e, getLocation());
/*    */     }
/*    */   }
/*    */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.ant.MergeTask
 * JD-Core Version:    0.5.4
 */