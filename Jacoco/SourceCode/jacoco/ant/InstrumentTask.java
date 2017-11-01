/*******************************************************************************
 * Copyright (c) 2009, 2017 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Brock Janiczak - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.ant;

import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.OfflineInstrumentationAccessGenerator;

/**
 * Task for offline instrumentation of class files.
 */
public class InstrumentTask extends Task {

    private File destdir;
    //多重嵌套的资源集合
    private final Union files = new Union();
    // boolean类型
    private boolean removesignatures = true;

    /**
     * Sets the location of the instrumented classes.
     * 
     * @param destdir
     *            destination folder for instrumented classes
     */
    //存放插入后的class目录 该参数是传入进来的，需要与build.xml一致
    public void setDestdir(final File destdir) {
        this.destdir = destdir;
    }

    /**
     * Sets whether signatures should be removed from JAR files.
     * 
     * @param removesignatures
     *            <code>true</code> if signatures should be removed
     */
    public void setRemovesignatures(final boolean removesignatures) {
        this.removesignatures = removesignatures;
    }

    /**
     * This task accepts any number of class file resources.
     * 
     * @param resources
     *            Execution data resources
     */
    public void addConfigured(final ResourceCollection resources) {
        files.add(resources);
    }

    @Override
    public void execute() throws BuildException {
        if (destdir == null) {
            throw new BuildException("Destination directory must be supplied",
                    getLocation());
        }
        int total = 0;
        final Instrumenter instrumenter = new Instrumenter(
                new OfflineInstrumentationAccessGenerator());
        //移除jar文件签名
        instrumenter.setRemoveSignatures(removesignatures);
        final Iterator<?> resourceIterator = files.iterator();
        while (resourceIterator.hasNext()) {
        	//嵌套元素
            final Resource resource = (Resource) resourceIterator.next();
            if (resource.isDirectory()) {
                continue;
            }
            //调用下面的函数
            total += instrument(instrumenter, resource);
        }
        log(format("Instrumented %s classes to %s", Integer.valueOf(total),
                destdir.getAbsolutePath()));
    }

    private int instrument(final Instrumenter instrumenter,
            final Resource resource) {
    	//执行函数，获得资源resource以及instrumenter对象
	    //根据dir路径和文件名创建一个file实例
        final File file = new File(destdir, resource.getName());
        //在dir目录下（./target/classes-instr），创建rsource.getName()文件
        file.getParentFile().mkdirs();
        try {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = resource.getInputStream();
                //内容输出到file中
                output = new FileOutputStream(file);
                //调用该函数进行插桩
                return instrumenter.instrumentAll(input, output,
                        resource.getName());
            } finally {
                FileUtils.close(input);
                FileUtils.close(output);
            }
        } catch (final Exception e) {
            file.delete();
            throw new BuildException(format("Error while instrumenting %s",
                    resource), e, getLocation());
        }
    }
}