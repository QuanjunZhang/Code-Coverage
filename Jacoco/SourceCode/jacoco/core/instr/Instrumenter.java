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
package org.jacoco.core.instr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jacoco.core.internal.ContentTypeDetector;
import org.jacoco.core.internal.Java9Support;
import org.jacoco.core.internal.Pack200Streams;
import org.jacoco.core.internal.flow.ClassProbesAdapter;
import org.jacoco.core.internal.instr.ClassInstrumenter;
import org.jacoco.core.internal.instr.IProbeArrayStrategy;
import org.jacoco.core.internal.instr.ProbeArrayStrategyFactory;
import org.jacoco.core.internal.instr.SignatureRemover;
import org.jacoco.core.runtime.IExecutionDataAccessorGenerator;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Several APIs to instrument Java class definitions for coverage tracing.
 */
public class Instrumenter {

    private final IExecutionDataAccessorGenerator accessorGenerator;

    private final SignatureRemover signatureRemover;

    /**
     * Creates a new instance based on the given runtime.
     * 
     * @param runtime
     *            runtime used by the instrumented classes
     */
    //根据给定的运行环境创建一个实例
    public Instrumenter(final IExecutionDataAccessorGenerator runtime) {
        this.accessorGenerator = runtime;
        this.signatureRemover = new SignatureRemover();
    }

    /**
     * Determines whether signatures should be removed from JAR files. This is
     * typically necessary as instrumentation modifies the class files and
     * therefore invalidates existing JAR signatures. Default is
     * <code>true</code>.
     * 
     * @param flag
     *            <code>true</code> if signatures should be removed
     */
    //判断signatures（签名）是否应当从jar文件中移除
    //这是必要的，因为插桩改变了jar文件，然后使得jar签名无效
    //flag默认是true；
    public void setRemoveSignatures(final boolean flag) {
        signatureRemover.setActive(flag);
    }

    /**
     * Creates a instrumented version of the given class if possible.
     * 
     * @param reader
     *            definition of the class as ASM reader
     * @return instrumented definition
     * 
     */
    //创建一个被注入的字节码文件01
    //reader:ClassReader对象
    //由02调用该函数
    public byte[] instrument(final ClassReader reader) {
        final ClassWriter writer = new ClassWriter(reader, 0) {

            @Override
            protected String getCommonSuperClass(final String type1,
                    final String type2) {
                throw new IllegalStateException();
            }
        };
//返回了一个对象， ClassFieldProbeArrayStrategy
        final IProbeArrayStrategy strategy = ProbeArrayStrategyFactory
                .createFor(reader, accessorGenerator);

        final ClassVisitor visitor = new ClassProbesAdapter(
                new ClassInstrumenter(strategy, writer), true);
//ClassReader
        //关于几个flag参数类型
        //EXOPAND_FRAMES:扩展StackMapTable数据，允许访问者获取全部本地变量类型与当前堆栈位置的信息
        //SKIP_DEBUG:用于忽略debug信息，例如，源文件，行数和变量信息
        //SKIP_FRAMES:用于忽略StackMapTable（栈图）信息。Java 6 之后JVM引入栈图概念
        reader.accept(visitor, ClassReader.EXPAND_FRAMES);

        return writer.toByteArray();
    }

    /**
     * Creates a instrumented version of the given class if possible.
     * 
     * @param buffer
     *            definition of the class
     * @param name
     *            a name used for exception messages
     * @return instrumented definition
     * @throws IOException
     *             if the class can't be instrumented
     */
    //创建一个被注入的字节码文件02
    //03调用该方法
    public byte[] instrument(final byte[] buffer, final String name)
            throws IOException {
        try {
        	//判断该class是否包含v1.9标识
            if (Java9Support.isPatchRequired(buffer)) {
            	//调用01函数，
            	//Java9Support.downgrade(buffer)，将其变成v1.8
            	//instrument(ClassReader)，调用asm操纵class文件
                final byte[] result = instrument(new ClassReader(Java9Support.downgrade(buffer)));
                //再次变成V1.8
                Java9Support.upgrade(result);
                return result;
            } else {
            	//如果if判断不是V1.9则直接调用01函数

                return instrument(new ClassReader(buffer));
            }
        } catch (final RuntimeException e) {
            throw instrumentError(name, e);
        }
    }

    /**
     * Creates a instrumented version of the given class if possible.
     * 
     * @param input
     *            stream to read class definition from
     * @param name
     *            a name used for exception messages
     * @return instrumented definition
     * @throws IOException
     *             if reading data from the stream fails or the class can't be
     *             instrumented
     */
    //创建一个被注入的字节码文件03
    //04方法调用该方法
    public byte[] instrument(final InputStream input, final String name)
            throws IOException {
        final byte[] bytes;
        try {
        	//将inputstream转换成byte array
            bytes = Java9Support.readFully(input);
        } catch (final IOException e) {
            throw instrumentError(name, e);
        }
        return instrument(bytes, name);
    }

    /**
     * Creates a instrumented version of the given class file.
     * 
     * @param input
     *            stream to read class definition from
     * @param output
     *            stream to write the instrumented version of the class to
     * @param name
     *            a name used for exception messages
     * @throws IOException
     *             if reading data from the stream fails or the class can't be
     *             instrumented
     */
    //创建一个被注入的字节码文件04
    //instrumentAll调用该方法
    public void instrument(final InputStream input, final OutputStream output,
            final String name) throws IOException {
    	//调用03函数
        output.write(instrument(input, name));
    }

    private IOException instrumentError(final String name,
            final Exception cause) {
        final IOException ex = new IOException(
                String.format("Error while instrumenting %s.", name));
        ex.initCause(cause);
        return ex;
    }

    /**
     * Creates a instrumented version of the given resource depending on its
     * type. Class files and the content of archive files are instrumented. All
     * other files are copied without modification.
     * 
     * @param input
     *            stream to contents from
     * @param output
     *            stream to write the instrumented version of the contents
     * @param name
     *            a name used for exception messages
     * @return number of instrumented classes
     * @throws IOException
     *             if reading data from the stream fails or a class can't be
     *             instrumented
     */
    //根据给定资源的类型创建一个被注入版本的文件
    //调用的就是该函数
    public int instrumentAll(final InputStream input,final OutputStream output, final String name) throws IOException {
        //关于ContentTypeDetector类：
        //Detector for content types of binary streams based on a magic headers.
        //基于magic header的检测器，用于二进制流的内容类型
        final ContentTypeDetector detector;
        try {
        	//创建对象。作用判断inputstream类型
            detector = new ContentTypeDetector(input);
        } catch (IOException e) {
            throw instrumentError(name, e);
        }
        //获得type，即inputstream类型码
        switch (detector.getType()) {
            //正常是该类型，即class文件
        case ContentTypeDetector.CLASSFILE:
        //调用方法插入
            instrument(detector.getInputStream(), output, name);
            return 1;
        case ContentTypeDetector.ZIPFILE:
            return instrumentZip(detector.getInputStream(), output, name);
        case ContentTypeDetector.GZFILE:
            return instrumentGzip(detector.getInputStream(), output, name);
        case ContentTypeDetector.PACK200FILE:
            return instrumentPack200(detector.getInputStream(), output, name);
        default:
            copy(detector.getInputStream(), output, name);
            return 0;
        }
    }

    private int instrumentZip(final InputStream input,
            final OutputStream output, final String name) throws IOException {
        final ZipInputStream zipin = new ZipInputStream(input);
        final ZipOutputStream zipout = new ZipOutputStream(output);
        ZipEntry entry;
        int count = 0;
        while ((entry = nextEntry(zipin, name)) != null) {
            final String entryName = entry.getName();
            if (signatureRemover.removeEntry(entryName)) {
                continue;
            }

            zipout.putNextEntry(new ZipEntry(entryName));
            if (!signatureRemover.filterEntry(entryName, zipin, zipout)) {
                count += instrumentAll(zipin, zipout, name + "@" + entryName);
            }
            zipout.closeEntry();
        }
        zipout.finish();
        return count;
    }

    private ZipEntry nextEntry(ZipInputStream input, String location)
            throws IOException {
        try {
            return input.getNextEntry();
        } catch (IOException e) {
            throw instrumentError(location, e);
        }
    }

    private int instrumentGzip(final InputStream input,
            final OutputStream output, final String name) throws IOException {
        final GZIPInputStream gzipInputStream;
        try {
            gzipInputStream = new GZIPInputStream(input);
        } catch (IOException e) {
            throw instrumentError(name, e);
        }
        final GZIPOutputStream gzout = new GZIPOutputStream(output);
        final int count = instrumentAll(gzipInputStream, gzout, name);
        gzout.finish();
        return count;
    }

    private int instrumentPack200(final InputStream input,
            final OutputStream output, final String name) throws IOException {
        final InputStream unpackedInput;
        try {
            unpackedInput = Pack200Streams.unpack(input);
        } catch (IOException e) {
            throw instrumentError(name, e);
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final int count = instrumentAll(unpackedInput, buffer, name);
        Pack200Streams.pack(buffer.toByteArray(), output);
        return count;
    }

    private void copy(final InputStream input, final OutputStream output,
            final String name) throws IOException {
        final byte[] buffer = new byte[1024];
        int len;
        while ((len = read(input, buffer, name)) != -1) {
            output.write(buffer, 0, len);
        }
    }

    private int read(final InputStream input, final byte[] buffer,
            final String name) throws IOException {
        try {
            return input.read(buffer);
        } catch (IOException e) {
            throw instrumentError(name, e);
        }
    }

}