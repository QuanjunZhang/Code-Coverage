/*     */ package org.jacoco.core.runtime;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public final class AgentOptions
/*     */ {
/*     */   public static final String DESTFILE = "destfile";
/*     */   public static final String DEFAULT_DESTFILE = "jacoco.exec";
/*     */   public static final String APPEND = "append";
/*     */   public static final String INCLUDES = "includes";
/*     */   public static final String EXCLUDES = "excludes";
/*     */   public static final String EXCLCLASSLOADER = "exclclassloader";
/*     */   public static final String INCLBOOTSTRAPCLASSES = "inclbootstrapclasses";
/*     */   public static final String INCLNOLOCATIONCLASSES = "inclnolocationclasses";
/*     */   public static final String SESSIONID = "sessionid";
/*     */   public static final String DUMPONEXIT = "dumponexit";
/*     */   public static final String OUTPUT = "output";
/* 117 */   private static final Pattern OPTION_SPLIT = Pattern.compile(",(?=[a-zA-Z0-9_\\-]+=)");
/*     */   public static final String ADDRESS = "address";
/* 163 */   public static final String DEFAULT_ADDRESS = null;
/*     */   public static final String PORT = "port";
/*     */   public static final int DEFAULT_PORT = 6300;
/*     */   public static final String CLASSDUMPDIR = "classdumpdir";
/*     */   public static final String JMX = "jmx";
/* 191 */   private static final Collection<String> VALID_OPTIONS = Arrays.asList(new String[] { "destfile", "append", "includes", "excludes", "exclclassloader", "inclbootstrapclasses", "inclnolocationclasses", "sessionid", "dumponexit", "output", "address", "port", "classdumpdir", "jmx" });
/*     */   private final Map<String, String> options;
/*     */ 
/*     */   public AgentOptions()
/*     */   {
/* 202 */     this.options = new HashMap();
/*     */   }
/*     */ 
/*     */   public AgentOptions(String optionstr)
/*     */   {
/* 213 */     if ((optionstr != null) && (optionstr.length() > 0)) {
/* 214 */       for (String entry : OPTION_SPLIT.split(optionstr)) {
/* 215 */         int pos = entry.indexOf('=');
/* 216 */         if (pos == -1) {
/* 217 */           throw new IllegalArgumentException(String.format("Invalid agent option syntax \"%s\".", new Object[] { optionstr }));
/*     */         }
/*     */ 
/* 220 */         String key = entry.substring(0, pos);
/* 221 */         if (!VALID_OPTIONS.contains(key)) {
/* 222 */           throw new IllegalArgumentException(String.format("Unknown agent option \"%s\".", new Object[] { key }));
/*     */         }
/*     */ 
/* 226 */         String value = entry.substring(pos + 1);
/* 227 */         setOption(key, value);
/*     */       }
/*     */ 
/* 230 */       validateAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public AgentOptions(Properties properties)
/*     */   {
/* 242 */     for (String key : VALID_OPTIONS) {
/* 243 */       String value = properties.getProperty(key);
/* 244 */       if (value != null)
/* 245 */         setOption(key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void validateAll()
/*     */   {
/* 251 */     validatePort(getPort());
/* 252 */     getOutput();
/*     */   }
/*     */ 
/*     */   private void validatePort(int port) {
/* 256 */     if (port < 0)
/* 257 */       throw new IllegalArgumentException("port must be positive");
/*     */   }
/*     */ 
/*     */   public String getDestfile()
/*     */   {
/* 267 */     return getOption("destfile", "jacoco.exec");
/*     */   }
/*     */ 
/*     */   public void setDestfile(String destfile)
/*     */   {
/* 277 */     setOption("destfile", destfile);
/*     */   }
/*     */ 
/*     */   public boolean getAppend()
/*     */   {
/* 286 */     return getOption("append", true);
/*     */   }
/*     */ 
/*     */   public void setAppend(boolean append)
/*     */   {
/* 296 */     setOption("append", append);
/*     */   }
/*     */ 
/*     */   public String getIncludes()
/*     */   {
/* 306 */     return getOption("includes", "*");
/*     */   }
/*     */ 
/*     */   public void setIncludes(String includes)
/*     */   {
/* 317 */     setOption("includes", includes);
/*     */   }
/*     */ 
/*     */   public String getExcludes()
/*     */   {
/* 327 */     return getOption("excludes", "");
/*     */   }
/*     */ 
/*     */   public void setExcludes(String excludes)
/*     */   {
/* 338 */     setOption("excludes", excludes);
/*     */   }
/*     */ 
/*     */   public String getExclClassloader()
/*     */   {
/* 348 */     return getOption("exclclassloader", "sun.reflect.DelegatingClassLoader");
/*     */   }
/*     */ 
/*     */   public void setExclClassloader(String expression)
/*     */   {
/* 359 */     setOption("exclclassloader", expression);
/*     */   }
/*     */ 
/*     */   public boolean getInclBootstrapClasses()
/*     */   {
/* 370 */     return getOption("inclbootstrapclasses", false);
/*     */   }
/*     */ 
/*     */   public void setInclBootstrapClasses(boolean include)
/*     */   {
/* 381 */     setOption("inclbootstrapclasses", include);
/*     */   }
/*     */ 
/*     */   public boolean getInclNoLocationClasses()
/*     */   {
/* 391 */     return getOption("inclnolocationclasses", false);
/*     */   }
/*     */ 
/*     */   public void setInclNoLocationClasses(boolean include)
/*     */   {
/* 402 */     setOption("inclnolocationclasses", include);
/*     */   }
/*     */ 
/*     */   public String getSessionId()
/*     */   {
/* 411 */     return getOption("sessionid", null);
/*     */   }
/*     */ 
/*     */   public void setSessionId(String id)
/*     */   {
/* 421 */     setOption("sessionid", id);
/*     */   }
/*     */ 
/*     */   public boolean getDumpOnExit()
/*     */   {
/* 430 */     return getOption("dumponexit", true);
/*     */   }
/*     */ 
/*     */   public void setDumpOnExit(boolean dumpOnExit)
/*     */   {
/* 441 */     setOption("dumponexit", dumpOnExit);
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 452 */     return getOption("port", 6300);
/*     */   }
/*     */ 
/*     */   public void setPort(int port)
/*     */   {
/* 463 */     validatePort(port);
/* 464 */     setOption("port", port);
/*     */   }
/*     */ 
/*     */   public String getAddress()
/*     */   {
/* 475 */     return getOption("address", DEFAULT_ADDRESS);
/*     */   }
/*     */ 
/*     */   public void setAddress(String address)
/*     */   {
/* 487 */     setOption("address", address);
/*     */   }
/*     */ 
/*     */   public OutputMode getOutput()
/*     */   {
/* 496 */     String value = (String)this.options.get("output");
/* 497 */     return (value == null) ? OutputMode.file : OutputMode.valueOf(value);
/*     */   }
/*     */ 
/*     */   public void setOutput(String output)
/*     */   {
/* 507 */     setOutput(OutputMode.valueOf(output));
/*     */   }
/*     */ 
/*     */   public void setOutput(OutputMode output)
/*     */   {
/* 517 */     setOption("output", output.name());
/*     */   }
/*     */ 
/*     */   public String getClassDumpDir()
/*     */   {
/* 527 */     return getOption("classdumpdir", null);
/*     */   }
/*     */ 
/*     */   public void setClassDumpDir(String location)
/*     */   {
/* 537 */     setOption("classdumpdir", location);
/*     */   }
/*     */ 
/*     */   public boolean getJmx()
/*     */   {
/* 546 */     return getOption("jmx", false);
/*     */   }
/*     */ 
/*     */   public void setJmx(boolean jmx)
/*     */   {
/* 556 */     setOption("jmx", jmx);
/*     */   }
/*     */ 
/*     */   private void setOption(String key, int value) {
/* 560 */     setOption(key, Integer.toString(value));
/*     */   }
/*     */ 
/*     */   private void setOption(String key, boolean value) {
/* 564 */     setOption(key, Boolean.toString(value));
/*     */   }
/*     */ 
/*     */   private void setOption(String key, String value) {
/* 568 */     this.options.put(key, value);
/*     */   }
/*     */ 
/*     */   private String getOption(String key, String defaultValue) {
/* 572 */     String value = (String)this.options.get(key);
/* 573 */     return (value == null) ? defaultValue : value;
/*     */   }
/*     */ 
/*     */   private boolean getOption(String key, boolean defaultValue) {
/* 577 */     String value = (String)this.options.get(key);
/* 578 */     return (value == null) ? defaultValue : Boolean.parseBoolean(value);
/*     */   }
/*     */ 
/*     */   private int getOption(String key, int defaultValue) {
/* 582 */     String value = (String)this.options.get(key);
/* 583 */     return (value == null) ? defaultValue : Integer.parseInt(value);
/*     */   }
/*     */ 
/*     */   public String getVMArgument(File agentJarFile)
/*     */   {
/* 595 */     return String.format("-javaagent:%s=%s", new Object[] { agentJarFile, this });
/*     */   }
/*     */ 
/*     */   public String getQuotedVMArgument(File agentJarFile)
/*     */   {
/* 607 */     return CommandLineSupport.quote(getVMArgument(agentJarFile));
/*     */   }
/*     */ 
/*     */   public String prependVMArguments(String arguments, File agentJarFile)
/*     */   {
/* 624 */     List args = CommandLineSupport.split(arguments);
/* 625 */     String plainAgent = String.format("-javaagent:%s", new Object[] { agentJarFile });
/* 626 */     for (Iterator i = args.iterator(); i.hasNext(); ) {
/* 627 */       if (((String)i.next()).startsWith(plainAgent));
/* 628 */       i.remove();
/*     */     }
/*     */ 
/* 631 */     args.add(0, getVMArgument(agentJarFile));
/* 632 */     return CommandLineSupport.quote(args);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 641 */     StringBuilder sb = new StringBuilder();
/* 642 */     for (String key : VALID_OPTIONS) {
/* 643 */       String value = (String)this.options.get(key);
/* 644 */       if (value != null) {
/* 645 */         if (sb.length() > 0) {
/* 646 */           sb.append(',');
/*     */         }
/* 648 */         sb.append(key).append('=').append(value);
/*     */       }
/*     */     }
/* 651 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static enum OutputMode
/*     */   {
/* 130 */     file, 
/*     */ 
/* 137 */     tcpserver, 
/*     */ 
/* 144 */     tcpclient, 
/*     */ 
/* 150 */     none;
/*     */   }
/*     */ }

/* Location:           F:\Jacoco\lib\jacocoant.jar
 * Qualified Name:     org.jacoco.core.runtime.AgentOptions
 * JD-Core Version:    0.5.4
 */