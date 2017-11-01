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
package org.jacoco.core.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.internal.analysis.BundleCoverageImpl;
import org.jacoco.core.internal.analysis.SourceFileCoverageImpl;

/**
 * Builder for hierarchical {@link ICoverageNode} structures from single
 * {@link IClassCoverage} nodes. The nodes are feed into the builder through its
 * {@link ICoverageVisitor} interface. Afterwards the aggregated data can be
 * obtained with {@link #getClasses()}, {@link #getSourceFiles()} or
 * {@link #getBundle(String)} in the following hierarchy:
 * 
 *builder（建造者）是为了有等级的structure元素，
 * <pre>
 * {@link IBundleCoverage}
 * +-- {@link IPackageCoverage}*
 *     +-- {@link IClassCoverage}*
 *     +-- {@link ISourceFileCoverage}*
 * </pre>
 */
public class CoverageBuilder implements ICoverageVisitor {

	private final Map<String, IClassCoverage> classes;

	private final Map<String, ISourceFileCoverage> sourcefiles;

	/**
	 * Create a new builder.
	 * 
	 */
	public CoverageBuilder() {
		this.classes = new HashMap<String, IClassCoverage>();
		this.sourcefiles = new HashMap<String, ISourceFileCoverage>();
	}

	/**
	 * Returns all class nodes currently contained in this builder.
	 * 
	 * @return all class nodes
	 */
	public Collection<IClassCoverage> getClasses() {
		return Collections.unmodifiableCollection(classes.values());
	}

	/**
	 * Returns all source file nodes currently contained in this builder.
	 * 
	 * @return all source file nodes
	 */
	public Collection<ISourceFileCoverage> getSourceFiles() {
		return Collections.unmodifiableCollection(sourcefiles.values());
	}

	/**
	 * Creates a bundle from all nodes currently contained in this bundle.
	 * 
	 * @param name
	 *            Name of the bundle
	 * @return bundle containing all classes and source files
	 */
	public IBundleCoverage getBundle(final String name) {
		return new BundleCoverageImpl(name, classes.values(),
				sourcefiles.values());
	}

	/**
	 * Returns all classes for which execution data does not match.
	 * 
	 * @see IClassCoverage#isNoMatch()
	 * @return collection of classes with non-matching execution data
	 */
	public Collection<IClassCoverage> getNoMatchClasses() {
		final Collection<IClassCoverage> result = new ArrayList<IClassCoverage>();
		for (final IClassCoverage c : classes.values()) {
			if (c.isNoMatch()) {
				result.add(c);
			}
		}
		return result;
	}

	// === IStructureVisitor ===
//在Analyzer传递过来,covergae是ClassCoverageImpl实例对象
	public void visitCoverage(final IClassCoverage coverage) {
		// Only consider classes that actually contain code:
		if (coverage.getInstructionCounter().getTotalCount() > 0) {//判断class中是否存在代码
//getName()来自CoverageNodeImpl
			//coverage具体是ClassCoverageImpl实例对象，
			//ClassCoverageImpl extends SourceNodeImpl implements IClassCoverage 
			//SourceNodeImpl extends CoverageNodeImpl implements ISourceNode 
			final String name = coverage.getName();

//创建一个对象，put()是hashMap函数，
			//IClassCoverage中name以及coverage被赋值了
			final IClassCoverage dup = classes.put(name, coverage);

			if (dup != null) {
				if (dup.getId() != coverage.getId()) {
					throw new IllegalStateException(
							"Can't add different class with same name: "
									+ name);
				}
			} else {
				final String source = coverage.getSourceFileName();//得到sourcefileName
				if (source != null) {
					final SourceFileCoverageImpl sourceFile = getSourceFile(
							source, coverage.getPackageName());
					//此处调用的应该是SourceNodeImpl，但是SourceNodeImpl还继承了CovergeNodeImpl类
					sourceFile.increment(coverage);
				}
			}
		}
	}

	private SourceFileCoverageImpl getSourceFile(final String filename,
			final String packagename) {
		final String key = packagename + '/' + filename;
		SourceFileCoverageImpl sourcefile = (SourceFileCoverageImpl) sourcefiles
				.get(key);//get是Hashmap函数
		if (sourcefile == null) {
			sourcefile = new SourceFileCoverageImpl(filename, packagename);
			sourcefiles.put(key, sourcefile);
		}
		return sourcefile;
	}

}
