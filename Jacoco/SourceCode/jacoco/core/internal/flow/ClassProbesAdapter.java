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
package org.jacoco.core.internal.flow;

import org.jacoco.core.internal.instr.InstrSupport;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;

/**
 * A {@link org.objectweb.asm.ClassVisitor} that calculates probes for every
 * method.
 */
// 为每个方法计算探针
//自定义的适配器
public class ClassProbesAdapter extends ClassVisitor implements
		IProbeIdGenerator {

	private static final MethodProbesVisitor EMPTY_METHOD_PROBES_VISITOR = new MethodProbesVisitor() {
	};

	private final ClassProbesVisitor cv;

	private final boolean trackFrames;

	private int counter = 0;

	private String name;

	/**
	 * Creates a new adapter that delegates to the given visitor.
	 * 根据给定的Visitor来创建一个新的adapter
	 * @param cv
	 *            instance to delegate to
	 *        cv 
	 *            需要传入的Visitor对象
	 * @param trackFrames
	 *            if <code>true</code> stackmap frames are tracked and provided
	 *        trackFrames
	 *            Boolean类型 如果是true，堆栈帧（不懂）被跟踪
	 */
	public ClassProbesAdapter(final ClassProbesVisitor cv,
			final boolean trackFrames) {
		super(InstrSupport.ASM_API_VERSION, cv);
		this.cv = cv;
		this.trackFrames = trackFrames;
	}

	@Override
	public void visit(final int version, final int access, final String name,
			final String signature, final String superName,
			final String[] interfaces) {
		this.name = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}
//核心
	@Override
	public final MethodVisitor visitMethod(final int access, final String name,
			final String desc, final String signature, final String[] exceptions) {
		final MethodProbesVisitor methodProbes;

		final MethodProbesVisitor mv = cv.visitMethod(access, name, desc,
				signature, exceptions);
		if (mv == null) {
			// We need to visit the method in any case, otherwise probe ids
			// are not reproducible
			//我们需要访问任何用例里面的函数，否则probe探针ID将不会再生
			methodProbes = EMPTY_METHOD_PROBES_VISITOR;
		} else {
			//若函数不为空
			methodProbes = mv;
		}
		//应返回一个MethordVisitor接口的实例
		//此处MethodSanitizer主要解决两个问题，jsr版本问题
		return new MethodSanitizer(null, access, name, desc, signature,exceptions) {
			@Override
			public void visitEnd() {
				super.visitEnd();
				//LabelFlowAnalyzer继承自MethodVisitor
				//标记该方法
				LabelFlowAnalyzer.markLabels(this);
				//MethodProbesAdapter extends MethodVisitor
				final MethodProbesAdapter probesAdapter = new MethodProbesAdapter(
						methodProbes, ClassProbesAdapter.this);
				if (trackFrames) {
					final AnalyzerAdapter analyzer = new AnalyzerAdapter(
							ClassProbesAdapter.this.name, access, name, desc,
							probesAdapter);
					probesAdapter.setAnalyzer(analyzer);
					methodProbes.accept(this, analyzer);
				} else {
					methodProbes.accept(this, probesAdapter);
				}
			}//visitEnd
		};
	}//visitMethod

	@Override
	public void visitEnd() {
		cv.visitTotalProbeCount(counter);
		super.visitEnd();
	}

	// 继承自接口=== IProbeIdGenerator ===

	public int nextId() {
		return counter++;
	}

}
