/*******************************************************************************
 * Copyright (c) 2009, 2017 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.ant;

import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IMultiReportOutput;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.MultiReportVisitor;
import org.jacoco.report.ZipMultiReportOutput;
import org.jacoco.report.check.IViolationsOutput;
import org.jacoco.report.check.Limit;
import org.jacoco.report.check.Rule;
import org.jacoco.report.check.RulesChecker;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;

/**
 * Task for coverage report generation.
 * 生成覆盖报告的Task
 */
public class ReportTask extends Task {

	/**
	 * The source files are specified in a resource collection with additional
	 * attributes.
	 */
	//源文件格式：定义了encoding和tabwidth属性
	public static class SourceFilesElement extends Union {

		String encoding = null;

		int tabWidth = 4;

		/**
		 * Defines the optional source file encoding. If not set the platform
		 * default is used.
		 * 
		 * @param encoding
		 *            source file encoding
		 */
		public void setEncoding(final String encoding) {
			this.encoding = encoding;
		}

		/**
		 * Sets the tab stop width for the source pages. Default value is 4.
		 * 
		 * @param tabWidth
		 *            number of characters per tab stop
		 */
		public void setTabwidth(final int tabWidth) {
			if (tabWidth <= 0) {
				throw new BuildException("Tab width must be greater than 0");
			}
			this.tabWidth = tabWidth;
		}

	}

	/**
	 * Container element for class file groups.
	 */
	//定义group元素，group里面有classfiles和sourcefiles，当然还有name属性
	//其中sourcefiles是自定义的数据类型,里面包含了encoding和tabwidth两种属性
	//classfiles是Union类型
	public static class GroupElement {
        //List是接口；ArrayList是它的继承类
		private final List<GroupElement> children = new ArrayList<GroupElement>();

		private final Union classfiles = new Union();

		private final SourceFilesElement sourcefiles = new SourceFilesElement();

		private String name;

		/**
		 * Sets the name of the group.
		 * 
		 * @param name
		 *            name of the group
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * Creates a new child group.
		 * 创建一个新的子group，将它添加到children中
		 * 
		 * @return new child group
		 */
		public GroupElement createGroup() {
			final GroupElement group = new GroupElement();
			//group是GroupElement类型
			//children也是list方式存储GroupElement类型
			//children是list方式存放group
			//比如children.[0]就是一个group
			children.add(group);
			return group;
		}

		/**
		 * Returns the nested resource collection for class files.
		 * 
		 * @return resource collection for class files
		 */
		public Union createClassfiles() {
			return classfiles;
		}

		/**
		 * Returns the nested resource collection for source files.
		 * 
		 * @return resource collection for source files
		 */
		public SourceFilesElement createSourcefiles() {
			return sourcefiles;
		}

	}//GroupElement


	/**
	 * Interface for child elements that define formatters.
	 */
	private abstract class FormatterElement {

		abstract IReportVisitor createVisitor() throws IOException;

		void finish() {
		}
	}

	/**
	 * Formatter element for HTML reports.
	 */
	public class HTMLFormatterElement extends FormatterElement {

		private File destdir;

		private File destfile;

		private String footer = "";

		private String encoding = "UTF-8";

		private Locale locale = Locale.getDefault();

		/**
		 * Sets the output directory for the report.
		 * 
		 * @param destdir
		 *            output directory
		 */
		public void setDestdir(final File destdir) {
			this.destdir = destdir;
		}

		/**
		 * Sets the Zip output file for the report.
		 * 
		 * @param destfile
		 *            Zip output file
		 */
		public void setDestfile(final File destfile) {
			this.destfile = destfile;
		}

		/**
		 * Sets an optional footer text that will be displayed on every report
		 * page.
		 * 
		 * @param text
		 *            footer text
		 */
		public void setFooter(final String text) {
			this.footer = text;
		}

		/**
		 * Sets the output encoding for generated HTML files. Default is UTF-8.
		 * 
		 * @param encoding
		 *            output encoding
		 */
		public void setEncoding(final String encoding) {
			this.encoding = encoding;
		}

		/**
		 * Sets the locale for generated text output. By default the platform
		 * locale is used.
		 * 
		 * @param locale
		 *            text locale
		 */
		public void setLocale(final String locale) {
			this.locale = parseLocale(locale);
		}

		@Override
		public IReportVisitor createVisitor() throws IOException {
			final IMultiReportOutput output;
			if (destfile != null) {
				if (destdir != null) {
					throw new BuildException(
							"Either destination directory or file must be supplied, not both",
							getLocation());
				}
				final FileOutputStream stream = new FileOutputStream(destfile);
				output = new ZipMultiReportOutput(stream);

			} else {
				if (destdir == null) {
					throw new BuildException(
							"Destination directory or file must be supplied for html report",
							getLocation());
				}
				output = new FileMultiReportOutput(destdir);
			}
			final HTMLFormatter formatter = new HTMLFormatter();
			formatter.setFooterText(footer);
			formatter.setOutputEncoding(encoding);
			formatter.setLocale(locale);
			return formatter.createVisitor(output);
		}

	}

	/**
	 * Formatter element for CSV reports.
	 */
	public class CSVFormatterElement extends FormatterElement {

		private File destfile;

		private String encoding = "UTF-8";

		/**
		 * Sets the output file for the report.
		 * 
		 * @param destfile
		 *            output file
		 */
		public void setDestfile(final File destfile) {
			this.destfile = destfile;
		}

		@Override
		public IReportVisitor createVisitor() throws IOException {
			if (destfile == null) {
				throw new BuildException(
						"Destination file must be supplied for csv report",
						getLocation());
			}
			final CSVFormatter formatter = new CSVFormatter();
			formatter.setOutputEncoding(encoding);
			return formatter.createVisitor(new FileOutputStream(destfile));
		}

		/**
		 * Sets the output encoding for generated XML file. Default is UTF-8.
		 * 
		 * @param encoding
		 *            output encoding
		 */
		public void setEncoding(final String encoding) {
			this.encoding = encoding;
		}

	}

	/**
	 * Formatter element for XML reports.
	 */
	public class XMLFormatterElement extends FormatterElement {

		private File destfile;//报告存放的位置

		private String encoding = "UTF-8";

		/**
		 * Sets the output file for the report.
		 * 
		 * @param destfile
		 *            output file
		 */
		public void setDestfile(final File destfile) {
			this.destfile = destfile;
		}

		/**
		 * Sets the output encoding for generated XML file. Default is UTF-8.
		 * 
		 * @param encoding
		 *            output encoding
		 */
		public void setEncoding(final String encoding) {
			this.encoding = encoding;
		}

		@Override
		public IReportVisitor createVisitor() throws IOException {
			if (destfile == null) {//如果目标文件夹不存在
				throw new BuildException(
						"Destination file must be supplied for xml report",
						getLocation());
			}
			//设置格式
			final XMLFormatter formatter = new XMLFormatter();
			//设置编码格式
			formatter.setOutputEncoding(encoding);
			return formatter.createVisitor(new FileOutputStream(destfile));
		}

	}

	/**
	 * Formatter element for coverage checks.
	 * 用于检查的元素
	 */
	public class CheckFormatterElement extends FormatterElement implements IViolationsOutput {

		private final List<Rule> rules = new ArrayList<Rule>();
		private boolean violations = false;
		private boolean failOnViolation = true;//判断build是否失败
		private String violationsPropery = null;//Propery元素名

		/**
		 * Creates and adds a new rule.
		 * 创建rule
		 * @return new rule
		 */
		public Rule createRule() {
			final Rule rule = new Rule();
			rules.add(rule);
			return rule;
		}

		/**
		 * Sets whether the build should fail in case of a violation. Default is
		 * <code>true</code>.
		 * 
		 * @param flag
		 *            if <code>true</code> the build fails on violation
		 */
		public void setFailOnViolation(final boolean flag) {
			this.failOnViolation = flag;
			//设置build是否失败
		}

		/**
		 * Sets the name of a property to append the violation messages to.
		 * 设置property元素名称
		 * @param property
		 *            name of a property
		 */
		public void setViolationsProperty(final String property) {
			this.violationsPropery = property;
		}

		@Override
		public IReportVisitor createVisitor() throws IOException {
			final RulesChecker formatter = new RulesChecker();
			formatter.setRules(rules);
			return formatter.createVisitor(this);
		}

		public void onViolation(final ICoverageNode node, final Rule rule,
				final Limit limit, final String message) {
			log(message, Project.MSG_ERR);
			violations = true;
			if (violationsPropery != null) {
				final String old = getProject().getProperty(violationsPropery);
				final String value = old == null ? message : String.format(
						"%s\n%s", old, message);
				getProject().setProperty(violationsPropery, value);
			}
		}

		@Override
		void finish() {
			if (violations && failOnViolation) {
				throw new BuildException(
						"Coverage check failed due to violated rules.",
						getLocation());
			}
		}
	}//CheckFormatterElement
    //executionfata指jacoco.exec;其中包含了两个方面
	//executed class:有1.classID；2.class VM Name；3.total probes;4.executed probe
	//Sessions:1.Session ID;start Time;Dump Time
	private final Union executiondataElement = new Union();
    //创建SessionInfoStore对象
	private SessionInfoStore sessionInfoStore;
    //存放的是执行信息
	private ExecutionDataStore executionDataStore;
   //GroupElement类定义在上面
	private final GroupElement structure = new GroupElement();
   //创建不同类型的报告文档
	private final List<FormatterElement> formatters = new ArrayList<FormatterElement>();

	/**
	 * Returns the nested resource collection for execution data files.
	 * 
	 * @return resource collection for execution files
	 */
	public Union createExecutiondata() {
		//jacoco.exec信息就存放在此处了
		return executiondataElement;
	}

	/**
	 * Returns the root group element that defines the report structure.
	 * 
	 * @return root group element
	 */
	//得到structure，它是group元素的根元素
	public GroupElement createStructure() {
		return structure;
	}

	/**
	 * Creates a new HTML report element.1
	 * 
	 * @return HTML report element
	 */
	public HTMLFormatterElement createHtml() {
		final HTMLFormatterElement element = new HTMLFormatterElement();
		formatters.add(element);
		return element;
	}

	/**
	 * Creates a new CSV report element.2
	 * 
	 * @return CSV report element
	 */
	public CSVFormatterElement createCsv() {
		final CSVFormatterElement element = new CSVFormatterElement();
		formatters.add(element);
		return element;
	}

	/**
	 * Creates a new coverage check element.3
	 * 
	 * @return coverage check element
	 */
	public CheckFormatterElement createCheck() {
		final CheckFormatterElement element = new CheckFormatterElement();
		formatters.add(element);
		return element;
	}

	/**
	 * Creates a new XML report element.
	 * 
	 * @return CSV report element
	 */
	public XMLFormatterElement createXml() {4
		final XMLFormatterElement element = new XMLFormatterElement();
		formatters.add(element);
		return element;
	}

	@Override
	public void execute() throws BuildException {
		//导入信息
		//executionDataStore存放了execution data
		//sessionInfoStore存放了sessionInfo
		loadExecutionData();
		try {
			//调用的这个createVisitor函数，不是每一个内部类中的，是该函数下方的
			//创建了4中格式的visitor
			final IReportVisitor visitor = createVisitor();
			//访问信息
			//调用各自格式的visitInfo函数
			//将sessionInfo和executionData传入过去
			visitor.visitInfo(sessionInfoStore.getInfos(),
					executionDataStore.getContents());
            //创建报告（就在该函数下方，不是每一个内部类里面的）
            //structure其实是GroupElement类型，包含了sourcefiles和classfiles
			createReport(visitor, structure);

			visitor.visitEnd();
			for (final FormatterElement f : formatters) {
				f.finish();
			}
		} catch (final IOException e) {
			throw new BuildException("Error while creating report", e,
					getLocation());
		}
	}
//将jacoco.exec
	private void loadExecutionData() {
		final ExecFileLoader loader = new ExecFileLoader();

		for (final Iterator<?> i = executiondataElement.iterator(); i.hasNext();) {
			//首先executiondataElement是集合方式存在的，这里将每一个i内容传递给resource
			final Resource resource = (Resource) i.next();
			log(format("Loading execution data file %s", resource));
			InputStream in = null;
			try {
				//将resource内容转换为inputStream类型
				in = resource.getInputStream();
				//将in传递给loader.load()函数
				//在该函数中，将获得sessionInfo和execution data传给了各自的visitor
				loader.load(in);
			} catch (final IOException e) {
				throw new BuildException(format(
						"Unable to read execution data file %s", resource), e,
						getLocation());
			} finally {
				FileUtils.close(in);
			}
		}
		//那么这里就是该函数的最终目的
		//将SessionInfo和execution data分别传给了他们
		sessionInfoStore = loader.getSessionInfoStore();
		executionDataStore = loader.getExecutionDataStore();
	}

	private IReportVisitor createVisitor() throws IOException {
		//创建了一系列的visitor对象
		final List<IReportVisitor> visitors = new ArrayList<IReportVisitor>();
		for (final FormatterElement f : formatters) {
			//f.creatVisitor()是各个格式内部的函数，返回的是IReportVisitor继承类
			//将各个格式自己创建了visitor加入到visitors中
			//一共有四种格式，html，xml，csv，check
			visitors.add(f.createVisitor());
		}
		return new MultiReportVisitor(visitors);
	}
//生成报告，核心
	private void createReport(final IReportGroupVisitor visitor,
			final GroupElement group) throws IOException {
		if (group.name == null) {
			throw new BuildException("Group name must be supplied",
					getLocation());
		}
		if (group.children.isEmpty()) {//递归

            //bundle应该是数据信息
			final IBundleCoverage bundle = createBundle(group);
           
			final SourceFilesElement sourcefiles = group.sourcefiles;

			//locator应该也是数据信息
			final AntResourcesLocator locator = new AntResourcesLocator(
					sourcefiles.encoding, sourcefiles.tabWidth);
            //将sourcefiles传入ISourceFileLocator中
			locator.addAll(sourcefiles.iterator());

			if (!locator.isEmpty()) {
				checkForMissingDebugInformation(bundle);
			}
//bundle包含counter
			visitor.visitBundle(bundle, locator);
		} else {
			final IReportGroupVisitor groupVisitor = visitor
					.visitGroup(group.name);
			for (final GroupElement child : group.children) {
				createReport(groupVisitor, child);
			}
		}
	}
//创建一个bundle
	private IBundleCoverage createBundle(final GroupElement group)
			throws IOException {

		final CoverageBuilder builder = new CoverageBuilder();

		final Analyzer analyzer = new Analyzer(executionDataStore, builder);
		//对classfiles进行遍历
		for (final Iterator<?> i = group.classfiles.iterator(); i.hasNext();) {
			//将classfiles传递给resource
			final Resource resource = (Resource) i.next();
			if (resource.isDirectory() && resource instanceof FileResource) {
				analyzer.analyzeAll(((FileResource) resource).getFile());
			} else {
				final InputStream in = resource.getInputStream();
				//将classfiles转化为inputstream，传递过去
				analyzer.analyzeAll(in, resource.getName());
				in.close();
			}
		}
		//BundleCoverageImpl(name, classes.values(),sourcefiles.values());
		final IBundleCoverage bundle = builder.getBundle(group.name);
		logBundleInfo(bundle, builder.getNoMatchClasses());
		return bundle;
	}

	private void logBundleInfo(final IBundleCoverage bundle,
			final Collection<IClassCoverage> nomatch) {
		log(format("Writing bundle '%s' with %s classes", bundle.getName(),
				Integer.valueOf(bundle.getClassCounter().getTotalCount())));
		if (!nomatch.isEmpty()) {
			log(format(
					"Classes in bundle '%s' do no match with execution data. "
							+ "For report generation the same class files must be used as at runtime.",
					bundle.getName()), Project.MSG_WARN);
			for (final IClassCoverage c : nomatch) {
				log(format("Execution data for class %s does not match.",
						c.getName()), Project.MSG_WARN);
			}
		}
	}

	private void checkForMissingDebugInformation(final ICoverageNode node) {
		if (node.getClassCounter().getTotalCount() > 0
				&& node.getLineCounter().getTotalCount() == 0) {
			log(format(
					"To enable source code annotation class files for bundle '%s' have to be compiled with debug information.",
					node.getName()), Project.MSG_WARN);
		}
	}

	/**
	 * Splits a given underscore "_" separated string and creates a Locale. This
	 * method is implemented as the method Locale.forLanguageTag() was not
	 * available in Java 5.
	 * 
	 * @param locale
	 *            String representation of a Locate
	 * @return Locale instance
	 */
	static Locale parseLocale(final String locale) {
		final StringTokenizer st = new StringTokenizer(locale, "_");
		final String language = st.hasMoreTokens() ? st.nextToken() : "";
		final String country = st.hasMoreTokens() ? st.nextToken() : "";
		final String variant = st.hasMoreTokens() ? st.nextToken() : "";
		return new Locale(language, country, variant);
	}

}
