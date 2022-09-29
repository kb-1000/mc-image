/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.logging;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.LoggerContext;
import org.graalvm.nativeimage.ImageSingletons;

@TargetClass(LogManager.class)
final class Target_org_apache_logging_log4j_LogManager {
    /*@AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getFormatterLogger() {
        return getFormatterLogger(GET_CALLER_CLASS_STACKWALKER.getCallerClass());
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getFormatterLogger(final Class<?> clazz) {
        return getLogger(clazz != null ? clazz : GET_CALLER_CLASS_STACKWALKER.getCallerClass(),
                StringFormatterMessageFactory.INSTANCE);
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getFormatterLogger(final Object value) {
        return getLogger(value != null ? value.getClass() : GET_CALLER_CLASS_STACKWALKER.getCallerClass(),
                StringFormatterMessageFactory.INSTANCE);
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getFormatterLogger(final String name) {
        return name == null ? getFormatterLogger(GET_CALLER_CLASS_STACKWALKER.getCallerClass()) : getLogger(name,
                StringFormatterMessageFactory.INSTANCE);
    }

    @Delete
    static native Class<?> callerClass(final Class<?> clazz);

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getLogger() {
        return getLogger(GET_CALLER_CLASS_STACKWALKER.getCallerClass());
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getLogger(final Class<?> clazz) {
        final Class<?> cls = clazz != null ? clazz : GET_CALLER_CLASS_STACKWALKER.getCallerClass();
        return LogManager.getContext(cls.getClassLoader(), false).getLogger(cls);
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getLogger(final Class<?> clazz, final MessageFactory messageFactory) {
        final Class<?> cls = clazz != null ? clazz : GET_CALLER_CLASS_STACKWALKER.getCallerClass();
        return LogManager.getContext(cls.getClassLoader(), false).getLogger(cls, messageFactory);
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getLogger(final MessageFactory messageFactory) {
        return getLogger(GET_CALLER_CLASS_STACKWALKER.getCallerClass(), messageFactory);
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getLogger(final Object value) {
        return getLogger(value != null ? value.getClass() : GET_CALLER_CLASS_STACKWALKER.getCallerClass());
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getLogger(final Object value, final MessageFactory messageFactory) {
        return getLogger(value != null ? value.getClass() : GET_CALLER_CLASS_STACKWALKER.getCallerClass(), messageFactory);
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getLogger(final String name) {
        return name != null ? LogManager.getContext(false).getLogger(name) : getLogger(GET_CALLER_CLASS_STACKWALKER.getCallerClass());
    }

    @AlwaysInline("Try enforcing getCallerClass optimization")
    @Substitute
    static Logger getLogger(final String name, final MessageFactory messageFactory) {
        return name != null ? LogManager.getContext(false).getLogger(name, messageFactory) : getLogger(
                GET_CALLER_CLASS_STACKWALKER.getCallerClass(), messageFactory);
    }*/

    @Substitute
    static LoggerContext getContext() {
        return ImageSingletons.lookup(LoggerContext.class);
    }

    @Substitute
    static LoggerContext getContext(final ClassLoader loader, final boolean currentContext) {
        return ImageSingletons.lookup(LoggerContext.class);
    }
}
