/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.jdk;

import com.oracle.svm.core.SubstrateUtil;
import com.oracle.svm.core.annotate.*;
import jdk.vm.ci.meta.MetaAccessProvider;
import jdk.vm.ci.meta.ResolvedJavaField;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;
import sun.reflect.generics.repository.AbstractRepository;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.tree.Tree;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;

@TargetClass(TypeVariableImpl.class)
final class Target_sun_reflect_generics_reflectiveObjects_TypeVariableImpl {
    @Alias
    @InjectAccessors(GenericDeclarationAccessor.class)
    private GenericDeclaration genericDeclaration = null;

    @Inject
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Custom, declClass = GenericDeclarationTransformer.class)
    // TODO: disable caching
    public GenericDeclaration genericDeclarationField;

    @Inject
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Custom, declClass = GenericDeclarationCreatorComputer.class) // TODO: disable caching
    GenericDeclarationCreator genericDeclarationCreator;

    @Platforms(Platform.HOSTED_ONLY.class)
    private static final class GenericDeclarationTransformer implements RecomputeFieldValue.CustomFieldValueComputer {
        @Override
        public Object compute(MetaAccessProvider metaAccess, ResolvedJavaField original, ResolvedJavaField annotated, Object receiver) {
            GenericDeclaration originalValue = ((TypeVariableImpl<?>) receiver).getGenericDeclaration();
            if (originalValue instanceof Executable executable) {
                return ImageSingletons.lookup(ReflectionTracerSupport.class).isReflected(executable) ? executable : null;
            } else {
                return originalValue;
            }
        }
        @Override
        public RecomputeFieldValue.ValueAvailability valueAvailability() {
            return RecomputeFieldValue.ValueAvailability.BeforeAnalysis;
        }
    }

    @Platforms(Platform.HOSTED_ONLY.class)
    private static final class GenericDeclarationCreatorComputer implements RecomputeFieldValue.CustomFieldValueComputer {
        @Override
        public Object compute(MetaAccessProvider metaAccess, ResolvedJavaField original, ResolvedJavaField annotated, Object receiver) {
            return ((TypeVariableImpl<?>) receiver).getGenericDeclaration() instanceof Executable executable
                    ? ImageSingletons.lookup(ReflectionTracerSupport.class).getCreator(executable)
                    : null;
        }
        @Override
        public RecomputeFieldValue.ValueAvailability valueAvailability() {
            return RecomputeFieldValue.ValueAvailability.BeforeAnalysis;
        }
    }

    private static final class GenericDeclarationAccessor {
        private static GenericDeclaration get(Target_sun_reflect_generics_reflectiveObjects_TypeVariableImpl typeVariable) {
            GenericDeclarationCreator creator = typeVariable.genericDeclarationCreator;
            if (creator != null) {
                typeVariable.genericDeclarationCreator = null;
                return (typeVariable.genericDeclarationField = creator.get());
            }
            return typeVariable.genericDeclarationField;
        }

        private static void set(Target_sun_reflect_generics_reflectiveObjects_TypeVariableImpl typeVariable, GenericDeclaration genericDeclaration) {
            typeVariable.genericDeclarationField = genericDeclaration;
        }
    }
}

@TargetClass(AbstractRepository.class)
final class Target_sun_reflect_generics_repository_AbstractRepository {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private GenericsFactory factory;

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private Tree tree;
}

@TargetClass(LazyReflectiveObjectGenerator.class)
final class Target_sun_reflect_generics_reflectiveObjects_LazyReflectiveObjectGenerator {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private GenericsFactory factory;
}


@TargetClass(Method.class)
final class Target_java_lang_reflect_Method {
    @Alias
    private Class<?> clazz;
    @Alias
    private String name;
    @Alias
    private int modifiers;
    @Alias
    private String signature;
    @Alias
    private MethodRepository genericInfo;

    Target_java_lang_reflect_Method(Class<?> declaringClass, String name, int modifiers, String signature, MethodRepository genericInfo) {
        clazz = declaringClass;
        this.name = name;
        this.modifiers = modifiers;
        this.signature = signature;
        this.genericInfo = genericInfo;
    }
}

@TargetClass(Method.class)
final class Target_java_lang_reflect_Constructor {
    @Alias
    private Class<?> clazz;
    @Alias
    private int modifiers;
    @Alias
    private String signature;
    @Alias
    private ConstructorRepository genericInfo;

    Target_java_lang_reflect_Constructor(Class<?> declaringClass, int modifiers, String signature, ConstructorRepository genericInfo) {
        clazz = declaringClass;
        this.modifiers = modifiers;
        this.signature = signature;
        this.genericInfo = genericInfo;
    }
}

final class GenericDeclarationCreator {
    private GenericDeclaration cache;
    private final boolean isMethod;
    private final Class<?> declaringClass;
    private final String name;
    private final int modifiers;
    private final String signature;
    private final ConstructorRepository genericInfo;

    @Platforms(Platform.HOSTED_ONLY.class)
    <T> GenericDeclarationCreator(Executable executable) {
        declaringClass = executable.getDeclaringClass();
        modifiers = executable.getModifiers();
        try {
            // genericInfo's fields should be initialized already given we even reach this?
            //noinspection AssignmentUsedAsCondition
            if (isMethod = executable instanceof Method) {
                name = executable.getName();
                signature = (String) GenericDeclarationCreatorSupport.METHOD_GET_SIGNATURE.invokeExact((Method) executable);
                this.genericInfo = (MethodRepository) GenericDeclarationCreatorSupport.METHOD_GET_GENERIC_INFO.invokeExact((Method) executable);
            } else {
                name = null;
                signature = (String) GenericDeclarationCreatorSupport.CONSTRUCTOR_GET_SIGNATURE.invokeExact((Constructor<?>) executable);
                genericInfo = (ConstructorRepository) GenericDeclarationCreatorSupport.CONSTRUCTOR_GET_GENERIC_INFO.invokeExact((Constructor<?>) executable);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // shouldn't be called too often... synchronized should be fine
    synchronized GenericDeclaration get() {
        if (cache != null) return cache;
        return (cache = create());
    }

    private GenericDeclaration create() {
        Executable executable;
        if (isMethod) {
            Target_java_lang_reflect_Method m = new Target_java_lang_reflect_Method(declaringClass, name, modifiers, signature, (MethodRepository) genericInfo);
            executable = SubstrateUtil.cast(m, Executable.class);
        } else {
            Target_java_lang_reflect_Constructor c = new Target_java_lang_reflect_Constructor(declaringClass, modifiers, signature, genericInfo);
            executable = SubstrateUtil.cast(c, Executable.class);
        }

        return executable;
    }
}

@Platforms(Platform.HOSTED_ONLY.class)
final class GenericDeclarationCreatorSupport {
    private static final MethodHandles.Lookup IMPL_LOOKUP;
    static final MethodHandle CONSTRUCTOR_GET_SIGNATURE;
    static final MethodHandle METHOD_GET_SIGNATURE;
    static final MethodHandle CONSTRUCTOR_GET_GENERIC_INFO;
    static final MethodHandle METHOD_GET_GENERIC_INFO;

    static {
        try {
            final Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            implLookupField.setAccessible(true); // could fail? but Graal itself does this too, so...
            IMPL_LOOKUP = (MethodHandles.Lookup) implLookupField.get(null);
            CONSTRUCTOR_GET_SIGNATURE = IMPL_LOOKUP.findGetter(Constructor.class, "signature", String.class);
            METHOD_GET_SIGNATURE = IMPL_LOOKUP.findGetter(Method.class, "signature", String.class);
            CONSTRUCTOR_GET_GENERIC_INFO = IMPL_LOOKUP.findGetter(Constructor.class, "genericInfo", ConstructorRepository.class);
            METHOD_GET_GENERIC_INFO = IMPL_LOOKUP.findGetter(Method.class, "genericInfo", MethodRepository.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

interface ReflectionTracerSupport {
    void registerReflected(Executable executable);

    boolean isReflected(Executable executable);

    boolean commit();

    GenericDeclarationCreator getCreator(Executable executable);
}


@Platforms(Platform.HOSTED_ONLY.class)
final class ReflectionTracerSupportImpl implements ReflectionTracerSupport {
    private boolean hasChanged;
    private final Set<Executable> reflected = Collections.synchronizedSet(new HashSet<>());
    private final Map<Executable, GenericDeclarationCreator> creators = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void registerReflected(Executable executable) {
        if (executable != null)
            hasChanged |= reflected.add(executable);
    }

    @Override
    public boolean isReflected(Executable executable) {
        return reflected.contains(executable);
    }

    @Override
    public boolean commit() {
        boolean hasChanged = this.hasChanged;
        this.hasChanged = false;
        return hasChanged;
    }

    @Override
    public GenericDeclarationCreator getCreator(Executable executable) {
        if (executable == null || isReflected(executable))
            return null;

        return creators.computeIfAbsent(executable, GenericDeclarationCreator::new);
    }
}


@AutomaticFeature
final class ReflectionTracerFeature implements Feature {
    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        //throw new UnsupportedOperationException("FIXME: implement recreation in GenericDeclarationCreator");
        ImageSingletons.add(ReflectionTracerSupport.class, new ReflectionTracerSupportImpl());
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        access.registerObjectReplacer(o -> {
            if (o instanceof Executable executable)
                ImageSingletons.lookup(ReflectionTracerSupport.class).registerReflected(executable);

                // initialize all lazy fields so that resetting the generic factory is safe
            /*else if (o instanceof GenericDeclRepository repository) {
                ((GenericDeclRepository<?>) repository).getTypeParameters();
                if (repository instanceof ConstructorRepository constructorRepository) {
                    constructorRepository.getParameterTypes();
                    constructorRepository.getExceptionTypes();
                    if (constructorRepository instanceof MethodRepository methodRepository) {
                        methodRepository.getReturnType();
                    }
                }
                if (repository instanceof ClassRepository classRepository) {
                    classRepository.getSuperclass();
                    classRepository.getSuperInterfaces();
                }
            } else if (o instanceof FieldRepository fieldRepository) {
                fieldRepository.getGenericType();
            }*/ // already done in SVM's ReflectionObjectReplacer
            return o;
        });
    }

    @Override
    public void duringAnalysis(DuringAnalysisAccess access) {
        if (ImageSingletons.lookup(ReflectionTracerSupport.class).commit()) {
            access.requireAnalysisIteration();
        }
    }
}
