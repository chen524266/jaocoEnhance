package org.jacoco.cli.internal;

import org.objectweb.asm.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyClassVistor extends ClassVisitor {

	public MyClassVistor(int api, ClassVisitor classVisitor) {
		super(api, classVisitor);
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name,
			final String desc, final String signature,
			final String[] exceptions) {
		System.out.println(
				"name=" + name + ",desc=" + desc + ",signature=" + signature);
		// 返回一个MyMethodVistor对象，用于进一步访问方法的字节码
		MethodVisitor mv = super.cv.visitMethod(access, name, desc, signature,
				exceptions);
		MyMethodAdapter myMethodAdapter = new MyMethodAdapter(mv);
		return myMethodAdapter;
	}

	@Override
	public void visitEnd() {
		// 在访问结束时，可以执行一些清理工作或对类的结束进行相应的处理
		System.out.println("Class visited successfully!");
		super.visitEnd();
	}

	public static void main(String[] args) throws IOException {
		InputStream inputStream = new FileInputStream("D:\\test.class");
		ClassReader reader = new ClassReader(inputStream);
		final ClassWriter writer = new ClassWriter(reader, 0);
		ClassVisitor myClassVistor = new MyClassVistor(Opcodes.ASM9, writer);
		reader.accept(myClassVistor, ClassReader.EXPAND_FRAMES);
		FileOutputStream out = new FileOutputStream("D:\\test2.class");
		out.write(writer.toByteArray());
		out.flush();
		out.close();
	}
}
