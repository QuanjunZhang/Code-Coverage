/*     */ package org.jacoco.ant;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.tools.ant.BuildException;
/*     */ import org.apache.tools.ant.Project;
/*     */ import org.apache.tools.ant.RuntimeConfigurable;
/*     */ import org.apache.tools.ant.Task;
/*     */ import org.apache.tools.ant.TaskContainer;
/*     */ import org.apache.tools.ant.UnknownElement;
/*     */ 
/*     */ public class CoverageTask extends AbstractCoverageTask
/*     */   implements TaskContainer
/*     */ {
/*  38 */   private final Collection<TaskEnhancer> taskEnhancers = new ArrayList();
/*     */   private Task childTask;
/*     */ 
/*     */   public CoverageTask()
/*     */   {
/*  46 */     this.taskEnhancers.add(new JavaLikeTaskEnhancer("java"));
/*  47 */     this.taskEnhancers.add(new JavaLikeTaskEnhancer("junit"));
/*  48 */     this.taskEnhancers.add(new TestNGTaskEnhancer("testng"));
/*     */   }
/*     */ 
/*     */   public void addTask(Task task)
/*     */   {
/*  56 */     if (this.childTask != null) {
/*  57 */       throw new BuildException("Only one child task can be supplied to the coverge task", getLocation());
/*     */     }
/*     */ 
/*  62 */     this.childTask = task;
/*     */ 
/*  64 */     String subTaskTypeName = task.getTaskType();
/*     */ 
/*  66 */     TaskEnhancer enhancer = findEnhancerForTask(subTaskTypeName);
/*  67 */     if (enhancer == null) {
/*  68 */       throw new BuildException(String.format("%s is not a valid child of the coverage task", new Object[] { subTaskTypeName }), getLocation());
/*     */     }
/*     */ 
/*  73 */     if (isEnabled()) {
/*  74 */       log(String.format("Enhancing %s with coverage", new Object[] { this.childTask.getTaskName() }));
/*  75 */       enhancer.enhanceTask(task);
/*     */     }
/*     */ 
/*  78 */     task.maybeConfigure();
/*     */   }
/*     */ 
/*     */   private TaskEnhancer findEnhancerForTask(String taskName) {
/*  82 */     for (TaskEnhancer enhancer : this.taskEnhancers) {
/*  83 */       if (enhancer.supportsTask(taskName)) {
/*  84 */         return enhancer;
/*     */       }
/*     */     }
/*     */ 
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */   public void execute()
/*     */     throws BuildException
/*     */   {
/*  96 */     if (this.childTask == null) {
/*  97 */       throw new BuildException("A child task must be supplied for the coverage task", getLocation());
/*     */     }
/*     */ 
/* 102 */     this.childTask.execute();
/*     */   }
/*     */ 
/*     */   private static abstract interface TaskEnhancer
/*     */   {
/*     */     public abstract boolean supportsTask(String paramString);
/*     */ 
/*     */     public abstract void enhanceTask(Task paramTask)
/*     */       throws BuildException;
/*     */   }
/*     */ 
/*     */   private class JavaLikeTaskEnhancer
/*     */     implements CoverageTask.TaskEnhancer
/*     */   {
/*     */     private final String supportedTaskName;
/*     */ 
/*     */     public JavaLikeTaskEnhancer(String supportedTaskName)
/*     */     {
/* 131 */       this.supportedTaskName = supportedTaskName;
/*     */     }
/*     */ 
/*     */     public boolean supportsTask(String taskname) {
/* 135 */       return taskname.equals(this.supportedTaskName);
/*     */     }
/*     */ 
/*     */     public void enhanceTask(Task task) {
/* 139 */       RuntimeConfigurable configurableWrapper = task.getRuntimeConfigurableWrapper();
/*     */ 
/* 142 */       String forkValue = CoverageTask.this.getProject().replaceProperties((String)configurableWrapper.getAttributeMap().get("fork"));
/*     */ 
/* 145 */       if (!Project.toBoolean(forkValue)) {
/* 146 */         throw new BuildException("Coverage can only be applied on a forked VM", CoverageTask.this.getLocation());
/*     */       }
/*     */ 
/* 151 */       addJvmArgs(task);
/*     */     }
/*     */ 
/*     */     public void addJvmArgs(Task task) {
/* 155 */       UnknownElement el = new UnknownElement("jvmarg");
/* 156 */       el.setTaskName("jvmarg");
/* 157 */       el.setQName("jvmarg");
/*     */ 
/* 159 */       RuntimeConfigurable runtimeConfigurableWrapper = el.getRuntimeConfigurableWrapper();
/*     */ 
/* 161 */       runtimeConfigurableWrapper.setAttribute("value", CoverageTask.this.getLaunchingArgument());
/*     */ 
/* 164 */       task.getRuntimeConfigurableWrapper().addChild(runtimeConfigurableWrapper);
/*     */ 
/* 167 */       ((UnknownElement)task).addChild(el);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TestNGTaskEnhancer extends CoverageTask.JavaLikeTaskEnhancer
/*     */   {
/*     */     public TestNGTaskEnhancer(String supportedTaskName)
/*     */     {
/* 112 */       super(CoverageTask.this, supportedTaskName);
/*     */     }
/*     */ 
/*     */     public void enhanceTask(Task task)
/*     */     {
/* 117 */       addJvmArgs(task);
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.ant.CoverageTask
 * JD-Core Version:    0.5.4
 */