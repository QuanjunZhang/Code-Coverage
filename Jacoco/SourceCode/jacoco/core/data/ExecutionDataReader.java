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
package org.jacoco.core.data;

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;

import org.jacoco.core.internal.data.CompactDataInput;

/**
 * Deserialization of execution data from binary streams.
 * 二进制的execution data 反序列化
 */
public class ExecutionDataReader {

	/** Underlying data input */
	protected final CompactDataInput in;

	private ISessionInfoVisitor sessionInfoVisitor = null;

	private IExecutionDataVisitor executionDataVisitor = null;

	private boolean firstBlock = true;

	/**
	 * Creates a new reader based on the given input stream input. Depending on
	 * the nature of the underlying stream input should be buffered as most data
	 * is read in single bytes.
	 * 
	 * @param input
	 *            input stream to read execution data from
	 */
	public ExecutionDataReader(final InputStream input) {
		this.in = new CompactDataInput(input);
	}

	/**
	 * Sets an listener for session information.
	 * 
	 * @param visitor
	 *            visitor to retrieve session info events
	 */
	public void setSessionInfoVisitor(final ISessionInfoVisitor visitor) {
		this.sessionInfoVisitor = visitor;
	}

	/**
	 * Sets an listener for execution data.
	 * 
	 * @param visitor
	 *            visitor to retrieve execution data events
	 */
	public void setExecutionDataVisitor(final IExecutionDataVisitor visitor) {
		this.executionDataVisitor = visitor;
	}

	/**
	 * Reads all data and reports it to the corresponding visitors. The stream
	 * is read until its end or a command confirmation has been sent.
	 * 
	 * @return <code>true</code> if additional data can be expected after a
	 *         command has been executed. <code>false</code> if the end of the
	 *         stream has been reached.
	 * @throws IOException
	 *             might be thrown by the underlying input stream
	 * @throws IncompatibleExecDataVersionException
	 *             incompatible data version from different JaCoCo release
	 */
	//读取所有的信息，一个一个字节读取，把每一个字节调用readBlock，进行判断每一个字节
	//(如果读到的是sessionInfo或者execution data)调用相应的visitors。

	public boolean read() throws IOException,
			IncompatibleExecDataVersionException {
		byte type;
		do {
			int i = in.read();//读取in的下一个字节，返回0-255范围内的int字节值
			if (i == -1) {//如果为-1；折达到了流末尾
				return false; // EOF
			}//读到内容
			type = (byte) i;//读取i的字节值
			if (firstBlock && type != ExecutionDataWriter.BLOCK_HEADER) {
				throw new IOException("Invalid execution data file.");
			}
			firstBlock = false;
		} while (readBlock(type));
		return true;
	}

	/**
	 * Reads a block of data identified by the given id. Subclasses may
	 * overwrite this method to support additional block types.
	 * 
	 * @param blocktype
	 *            block type
	 * @return <code>true</code> if there are more blocks to read
	 * @throws IOException
	 *             might be thrown by the underlying input stream
	 */
	protected boolean readBlock(final byte blocktype) throws IOException {
		switch (blocktype) {
		case ExecutionDataWriter.BLOCK_HEADER:
			readHeader();
			return true;
		case ExecutionDataWriter.BLOCK_SESSIONINFO:
			readSessionInfo();
			return true;
		case ExecutionDataWriter.BLOCK_EXECUTIONDATA:
			readExecutionData();
			return true;
		default:
			throw new IOException(format("Unknown block type %x.",
					Byte.valueOf(blocktype)));
		}
	}

	private void readHeader() throws IOException {
		if (in.readChar() != ExecutionDataWriter.MAGIC_NUMBER) {
			throw new IOException("Invalid execution data file.");
		}
		final char version = in.readChar();
		if (version != ExecutionDataWriter.FORMAT_VERSION) {
			throw new IncompatibleExecDataVersionException(version);
		}
	}
//如果读到sessioninfo，调用该函数
	private void readSessionInfo() throws IOException {
		if (sessionInfoVisitor == null) {//判断该visitor是否存在
			throw new IOException("No session info visitor.");
		}
		final String id = in.readUTF();//Session ID：zqj-pc...
		final long start = in.readLong();//开始时间
		final long dump = in.readLong();//结束时间
		//将信息传入sessionOnfoVisitor
		sessionInfoVisitor.visitSessionInfo(new SessionInfo(id, start, dump));
	}

	private void readExecutionData() throws IOException {
		if (executionDataVisitor == null) {
			throw new IOException("No execution data visitor.");
		}
		final long id = in.readLong();//class ID
		final String name = in.readUTF();//Class VM Name
		final boolean[] probes = in.readBooleanArray();//probe探针信息
		executionDataVisitor.visitClassExecution(new ExecutionData(id, name,
				probes));
	}

}
