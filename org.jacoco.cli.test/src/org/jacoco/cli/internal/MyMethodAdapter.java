package org.jacoco.cli.internal;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MyMethodAdapter extends MethodVisitor {
	private MethodVisitor mv;

	public MyMethodAdapter(MethodVisitor mv) {
		super(Opcodes.ASM9, mv);
		this.mv = mv;
	}

	// 在每个方法方法体前面插入System.out.println("ASM INSERT！");
	@Override
	public void visitCode() {
		System.out.println("visitCode+visitCode=");
		System.out.println("ASM INSERT！");
		this.mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
				"Ljava/io/PrintStream;");
		this.mv.visitLdcInsn("ASM INSERT\uff01");
		this.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
				"println", "(Ljava/lang/String;)V", false);
	}

	@Override
	public void visitEnd() {
		this.mv.visitEnd();
	}
}
