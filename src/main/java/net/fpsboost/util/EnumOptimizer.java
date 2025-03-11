package net.fpsboost.util;

import lombok.SneakyThrows;
import net.fpsboost.Client;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LangYa466
 * @since 3/12/2025
 */
public class EnumOptimizer {
    private static final String ENUM_VALUES_METHOD = "values";

    @SneakyThrows
    public static void init() {
        String[] classDirs = getClassDirectories();

        for (String classDir : classDirs) {
            Path classPath = Paths.get(classDir);
            if (!Files.exists(classPath)) {
                Logger.info("[EnumOptimizer] Skipping non-existent directory: " + classDir);
                continue;
            }

            List<Path> classFiles = Files.walk(classPath)
                    .filter(p -> p.toString().endsWith(".class"))
                    .filter(p -> !p.toString().contains("florianmichael")) // 排除跨版本 避免崩溃
                    .collect(Collectors.toList());

            for (Path classFile : classFiles) {
                try {
                    byte[] classBytes = Files.readAllBytes(classFile);
                    ClassReader cr = new ClassReader(classBytes);
                    ClassNode cn = new ClassNode();
                    Logger.debug("Processing: " + classFile + ", Size: " + classBytes.length);
                    cr.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

                    if ((cn.access & Opcodes.ACC_ENUM) != 0) {
                        boolean modified = false;
                        for (Object mno : cn.methods) {
                            if (mno instanceof MethodNode) {
                                MethodNode mn = (MethodNode) mno;
                                if (ENUM_VALUES_METHOD.equals(mn.name) && mn.desc.startsWith("()[L")) {
                                    optimizeValuesMethod(cn, mn);
                                    modified = true;
                                }
                            } else {
                                Logger.error("[EnumOptimizer] Unable to handle: " + cn.name + " " + mno.toString());
                            }
                        }
                        if (modified) {
                            Logger.info("[EnumOptimizer] Processed: " + cn.name);
                        }
                    }

                    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                    cn.accept(cw);
                    Files.write(classFile, cw.toByteArray());

                } catch (Exception e) {
                    Logger.error("[EnumOptimizer] Failed to process: " + classFile + ", Skipping. Error: " + e.getMessage());
                }
            }
        }
    }

    private static void optimizeValuesMethod(ClassNode cn, MethodNode mn) {
        InsnList instructions = new InsnList();
        instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, "$VALUES", "[L" + cn.name + ";"));
        instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "[L" + cn.name + ";", "clone", "()Ljava/lang/Object;"));
        instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, "[L" + cn.name + ";"));
        instructions.add(new InsnNode(Opcodes.ARETURN));

        mn.instructions.clear();
        mn.instructions.add(instructions);
    }

    private static String[] getClassDirectories() {
        String classPath = System.getProperty("java.class.path");
        return classPath.split(File.pathSeparator);
    }
}
