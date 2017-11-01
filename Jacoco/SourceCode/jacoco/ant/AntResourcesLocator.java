/*******************************************************************************
 * Copyright (c) 2009, 2017 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    Dominik Stadler - source folder support
 *    
 *******************************************************************************/
package org.jacoco.ant;

import java.util.Iterator;

import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.MultiSourceFileLocator;

/**
 * Source file locator based on Ant resources. The locator supports files as
 * well as directories. The lookup is first performed on files (matching the
 * local file name) and afterwards on directories, by the order the directory
 * resources have been added. The directories are considered as source folders
 * that are searched for source files with the fully qualified name (package and
 * local name).
 *locator基于Ant resource，它提供文件和目录。
 *首先根据被添加的resource directory顺序来查找文件（与local file name匹配），然后查找目录。
 *目录被认为是资源文件夹source folders，通过全限定名称查找source folders依次找到source files
 */
class AntResourcesLocator extends MultiSourceFileLocator {

	private final String encoding;
	private final AntFilesLocator filesLocator;

	private boolean empty;

	AntResourcesLocator(final String encoding, final int tabWidth) {
		//调用父类的构造函数
		super(tabWidth);
		this.encoding = encoding;
		this.filesLocator = new AntFilesLocator(encoding, tabWidth);
		this.empty = true;
		super.add(filesLocator);
	}

	/**
	 * Adds the given file or directory resource to the locator.
	 * 
	 * @param resource
	 *            resource to add
	 */
	void add(final Resource resource) {
		empty = false;
		if (resource.isDirectory()) {//判断是否是文件夹
			final FileResource dir = (FileResource) resource;
			//如果是文件夹，那么在父类中add()参数是DirectorySourceFileLocator
			super.add(new DirectorySourceFileLocator(dir.getFile(), encoding,
					getTabWidth()));
		} else {
			filesLocator.add(resource);
		}
	}

	void addAll(final Iterator<?> iterator) {
		while (iterator.hasNext()) {
			add((Resource) iterator.next());
		}
	}

	/**
	 * Checks, whether resources have been added.
	 * 
	 * @return <code>true</code>, if no resources have been added
	 */
	boolean isEmpty() {
		return empty;
	}

}
