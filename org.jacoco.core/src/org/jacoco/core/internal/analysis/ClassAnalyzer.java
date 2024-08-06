/*******************************************************************************
 * Copyright (c) 2009, 2021 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.core.internal.analysis;

import org.jacoco.core.internal.analysis.filter.Filters;
import org.jacoco.core.internal.analysis.filter.IFilter;
import org.jacoco.core.internal.analysis.filter.IFilterContext;
import org.jacoco.core.internal.diff.ClassInfoDto;
import org.jacoco.core.internal.flow.ClassProbesVisitor;
import org.jacoco.core.internal.flow.MethodProbesVisitor;
import org.jacoco.core.internal.instr.InstrSupport;
import org.jacoco.core.tools.ExecFileLoader;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

/**
 * Analyzes the structure of a class.
 */
public class ClassAnalyzer extends ClassProbesVisitor
        implements IFilterContext {

    private final ClassCoverageImpl coverage;

    private final boolean[] probes;

    private final StringPool stringPool;

    private final Set<String> classAnnotations = new HashSet<String>();

    private final Set<String> classAttributes = new HashSet<String>();

    private String sourceDebugExtension;

    // 只收集方法中的指令的覆盖率，在收集到指令后退出后面的分析流程
    private boolean onlyAnaly = false;

    /**
     * 变更类信息
     */
    private List<ClassInfoDto> classInfos;

    private final IFilter filter;

    /**
     * Creates a new analyzer that builds coverage data for a class.
     *
     * @param coverage   coverage node for the analyzed class data
     * @param probes     execution data for this class or <code>null</code>
     * @param stringPool shared pool to minimize the number of {@link String} instances
     */
    public ClassAnalyzer(final ClassCoverageImpl coverage,
                         final boolean[] probes, final StringPool stringPool) {
        this.coverage = coverage;
        this.probes = probes;
        this.stringPool = stringPool;
        this.filter = Filters.all();
    }

    public ClassAnalyzer(final ClassCoverageImpl coverage,
                         final boolean[] probes, final StringPool stringPool,
                         List<ClassInfoDto> classInfos, boolean onlyAnaly) {
        this.coverage = coverage;
        this.probes = probes;
        this.stringPool = stringPool;
        this.filter = Filters.all();
        this.classInfos = classInfos;
        this.onlyAnaly = onlyAnaly;
    }

    public List<ClassInfoDto> getClassInfos() {
        return classInfos;
    }

    public void setClassInfos(List<ClassInfoDto> classInfos) {
        this.classInfos = classInfos;
    }

    @Override
    public void visit(final int version, final int access, final String name,
                      final String signature, final String superName,
                      final String[] interfaces) {
        coverage.setSignature(stringPool.get(signature));
        coverage.setSuperName(stringPool.get(superName));
        coverage.setInterfaces(stringPool.get(interfaces));
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        classAnnotations.add(desc);
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitAttribute(final Attribute attribute) {
        classAttributes.add(attribute.type);
    }

    @Override
    public void visitSource(final String source, final String debug) {
        coverage.setSourceFileName(stringPool.get(source));
        sourceDebugExtension = debug;
    }

    @Override
    public MethodProbesVisitor visitMethod(final int access, final String name,
                                           final String desc, final String signature,
                                           final String[] exceptions) {

        InstrSupport.assertNotInstrumented(name, coverage.getName());

        final InstructionsBuilder builder = new InstructionsBuilder(probes);

        // 对方法解析完毕后的一个钩子方法，从visitMethod的mv对象调用过来
        return new MethodAnalyzer(builder) {

            @Override
            public void accept(final MethodNode methodNode,
                               final MethodVisitor methodVisitor) {
                // 统计method的方法体的指令级别覆盖率，指令级别需要关注的是braches和coverbraches，line代码合并不需要关注，染色用
                super.accept(methodNode, methodVisitor);
                // 合并多版本覆盖率的时候不要走后面addMethodCoverage的流程，只获取到指令覆盖率就行
                String methodSign = access + name + desc + signature;
                if (exceptions != null) {
                    for (String s : exceptions) {
                        methodSign += s;
                    }
                }
                Map<String, Map<String, Map<String, Instruction>>> instrunctions = ExecFileLoader.instrunctionsThreadLocal.get();
                if (onlyAnaly) {
                    Map<String, Map<String, Instruction>> methodInstructions = new HashMap<>();
                    Map<String, Instruction> instructionMap = new HashMap<>();
                    Map<AbstractInsnNode, Instruction> builderInstructions = builder
                            .getInstructions();
                    for (Instruction instruction : builderInstructions
                            .values()) {
                        instructionMap.put(instruction.getSign(), instruction);
                    }
                    methodInstructions.put(methodSign, instructionMap);
                    if (instrunctions == null) {
                        instrunctions = new HashMap<>();
                        instrunctions.put(coverage.getName(),
                                methodInstructions);
                        ExecFileLoader.instrunctionsThreadLocal
                                .set(instrunctions);
                    } else {
                        if (instrunctions.containsKey(coverage.getName())) {
                            instrunctions.get(coverage.getName())
                                    .put(methodSign, instructionMap);
                        } else {
                            instrunctions.put(coverage.getName(),
                                    methodInstructions);
                        }
                    }
                    return;
                }
                // 如果存在已有的覆盖率数据，则合并method的指令覆盖率
                if (instrunctions != null && instrunctions.containsKey(coverage.getName())) {
                    // 合并method的指令数据
                    Map<String, Instruction> mergeInstructionMap = instrunctions.get(coverage.getName()).get(methodSign);
                    // 通过指令判断是否为同一个方法，所有指令签名一样的情况下判断是一样的
                    Map<AbstractInsnNode, Instruction> nowInstructions = builder.getInstructionsNotWireJumps();
                    if (mergeInstructionMap != null && mergeInstructionMap.size() == nowInstructions.size()) {
                        boolean match = true;
                        for (Instruction instruction : mergeInstructionMap
                                .values()) {
                            if (!isExistInstruction(nowInstructions,
                                    instruction)) {
                                match = false;
                                break;
                            }
                        }
                        // 同一个方法
                        if (match) {
                            for (AbstractInsnNode key : nowInstructions.keySet()) {
                                Instruction instruction = nowInstructions.get(key);
                                if (name.contains("list") && coverage.getName().contains("ProjectPageObjectController") && instruction.getProbeIndex() == 17) {
                                    System.out.println("ClassAnalyzer.accept");
                                }
                                //合并指令
                                Instruction other = mergeInstructionMap.get(instruction.getSign());
                                Instruction merge = instruction.merge(other);
                                nowInstructions.put(key, merge);
                                // 合并探针  https://github.com/jacoco/jacoco/issues/1644,fix由于插桩策略的问题导致的jump探针不能直接合并的问题
                                if (probes != null && instruction.getProbeIndex() != -1) {
                                    if ((instruction.branches > 1 && merge.getBranchCounter().getMissedCount() == 0) || instruction.branches == 1) {
                                        probes[instruction.getProbeIndex()] = merge.getInstructionCounter().getCoveredCount() > 0;
                                    }
                                }
                            }
                        }
                    }
                }
                if (name.contains("list") && coverage.getName().contains("ProjectPageObjectController")) {
                    System.out.println("ClassAnalyzer!!!");
                }
                addMethodCoverage(stringPool.get(name), stringPool.get(desc), stringPool.get(signature), builder, methodNode);
            }
        };
    }

    private boolean isExistInstruction(
            Map<AbstractInsnNode, Instruction> instrunctions,
            Instruction instruction) {
        for (Instruction instruction1 : instrunctions.values()) {
            if (instruction1.getSign().equals(instruction.getSign())) {
                return true;
            }
        }
        return false;
    }

    private void addMethodCoverage(final String name, final String desc,
                                   final String signature, final InstructionsBuilder icc,
                                   final MethodNode methodNode) {
        final MethodCoverageCalculator mcc = new MethodCoverageCalculator(
                icc.getInstructions());
        filter.filter(methodNode, this, mcc);

        final MethodCoverageImpl mc = new MethodCoverageImpl(name, desc,
                signature);
        // method的级别级别的计算，指令级别是最小的行覆盖率，这是整个覆盖率计算的基础
        mcc.calculate(mc);
        if (mc.containsCode()) {
            // class级别的覆盖率统计
            // Only consider methods that actually contain code
            coverage.addMethod(mc);
        }

    }

    @Override
    public FieldVisitor visitField(final int access, final String name,
                                   final String desc, final String signature, final Object value) {
        InstrSupport.assertNotInstrumented(name, coverage.getName());
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitTotalProbeCount(final int count) {
        // nothing to do
    }

    // IFilterContext implementation

    public String getClassName() {
        return coverage.getName();
    }

    public String getSuperClassName() {
        return coverage.getSuperName();
    }

    public Set<String> getClassAnnotations() {
        return classAnnotations;
    }

    public Set<String> getClassAttributes() {
        return classAttributes;
    }

    public String getSourceFileName() {
        return coverage.getSourceFileName();
    }

    public String getSourceDebugExtension() {
        return sourceDebugExtension;
    }

}
