#!/usr/bin/env bash
#
# Copyright (c) 2021-2022 kb1000.
#
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
#

if [ -f env.sh ]; then
    . env.sh
fi

if [ ! -d "$GRAAL_HOME" ]; then
    echo "\$GRAAL_HOME does not exist!"
    exit 1
fi

if TEMP=$(getopt -l 'jfr,debug-attach::,no-optimize,server' -o 'g' -- "$@"); then
    true
else
    echo 'Terminating...' >&2
    exit 1
fi

eval set -- "$TEMP"
unset TEMP
jfr=()
debug_attach=()
debug=()

optimize=true
server=false

while true; do
    case "$1" in
        '--jfr')
            jfr=(-H:\+AllowVMInspection -R:\+FlightRecorder)
            shift
            continue
        ;;
        '--debug-attach'*)
            debug_attach=($1 -H:NumberOfThreads=1)
            shift
            continue
        ;;
        '--no-optimize')
            optimize=false
            shift
            continue
        ;;
        '-g')
            debug=(-g -H:+IncludeDebugHelperMethods)
            shift
            continue
        ;;
        '--server')
            server=true
            shift
            continue
        ;;
        '--')
            shift
            break
        ;;
        '')
            shift
            continue
        ;;
        *)
            echo "Internal error!" >&2
            exit 1
        ;;
    esac
done


# TODO
allowed_classes="org.apache.logging,com.sun.org.apache.xerces,jdk.xml.internal.SecuritySupport,javax.xml.parsers.FactoryFinder,jdk.xml.internal.JdkXmlUtils,com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator,jdk.xml.internal.JdkConstants,io.netty.util.internal.SystemPropertyUtil,org.slf4j.LoggerFactory,oshi.util.FileUtil,com.sun.jna.Platform,oshi.SystemInfo,oshi.SystemInfo\$1,oshi.util.ExecutingCommand,oshi.util.Memoizer,oshi.util.ParseUtil,oshi.util.FormatUtil,org.lwjgl.system.Configuration,org.lwjgl.system.APIUtil,org.lwjgl.system.MathUtil,org.lwjgl.system.MultiReleaseTextDecoding,org.lwjgl.system.MultiReleaseMemCopy,org.lwjgl.system.Pointer,org.lwjgl.system.Pointer\$Default,org.lwjgl.system.Struct,org.lwjgl.system.Checks,org.lwjgl.system.CheckIntrinsics,org.lwjgl.system.ThreadLocalUtil,org.lwjgl.system.MemoryStack,org.lwjgl.system.MemoryUtil\$LazyInit,org.lwjgl.system.APIUtil\$1,org.lwjgl.Version,de.kb1000.mcimage.agent,de.kb1000.mcimage.util,de.kb1000.mcimage.target.lwjgl,com.mojang.blaze3d.platform,com.mojang.datafixers,com.mojang.bridge,it.unimi.dsi.fastutil,$(if $optimize; then echo -n java.awt.AWTEvent,; fi)com.mojang.serialization,com.mojang.brigadier,io.netty.util.concurrent.DefaultPromise"
# Used to disallow JNI libs from being loaded at runtime (by loading them at build time, TODO: this could be better)
lwjgl_library_classes="org.lwjgl.system.Library,org.lwjgl.opengl.GL,org.lwjgl.openal.AL,org.lwjgl.util.tinyfd.TinyFileDialogs"


if $optimize; then
    mc_classes="ab,agv,dh,pg,pc,ww,ags,c" # SharedConstants, JsonHelper, TranslatableBuiltInExceptions (dependency of SharedConstants), TranslatableText (dependency of TranslatableBuiltInExceptions), Style (dependency of TranslatableText), Identifier (dependency of Style), OrderedText (dependency of TranslatableText), Matrix3f

    if ! $server; then
        mc_classes+=",dpg,dpg\$a,dpg\$b,dpc" # InputUtil (InputUtil$Key, InputUtil$Type), Untracker
    fi
fi

. mc-image-lib/classpaths.sh

# TODO: object tree broken
"$GRAAL_HOME"/bin/native-image "${debug_attach[@]}" "${jfr[@]}" $(true -H:Dump=:3 -H:MethodFilter=java.util.concurrent.ConcurrentSkipListMap.doPut,java.util.concurrent.ConcurrentSkipListMap.add -H:+PrintBackendCFG) "${debug[@]}" --diagnostics-mode -H:ReportAnalysisForbiddenType=$(if $optimize; then echo -n java.io.ObjectInputStream; fi) -H:+PrintAnalysisCallTree -H:+EarlyGVN --native-compiler-path=/usr/lib64/ccache/clang --native-compiler-options=-fuse-ld=lld --native-compiler-options=-ggdb --native-compiler-options=-Wl,--trace --native-compiler-options=-flto --enable-http --enable-https -H:+ReportExceptionStackTraces -cp "$(if $optimize; then echo -n "$MCIMAGE_CLASSPATH_OPTIMIZE:"; fi)$(if $server; then echo -n "$MCIMAGE_CLASSPATH_SERVER"; else echo -n "$MCIMAGE_CLASSPATH_CLIENT"; fi)" --no-fallback --allow-incomplete-classpath -H:-SpawnIsolates --initialize-at-run-time=org.lwjgl.system.jemalloc.JEmalloc\$Functions,org.lwjgl.system.jemalloc.JEmalloc,org.lwjgl.stb.LibSTB,org.lwjgl.glfw.GLFW --initialize-at-build-time="$lwjgl_library_classes" --initialize-at-build-time=net.minecraft.client.main.Main --initialize-at-build-time=io.netty.util.ResourceLeakDetector,io.netty.buffer.AbstractByteBufAllocator,io.netty.util.ReferenceCountUtil --initialize-at-build-time=oshi.util.GlobalConfig --initialize-at-build-time=com.ibm.icu --initialize-at-build-time=com.mojang.util.QueueLogAppender --initialize-at-build-time=org.lwjgl.system.MemoryUtil --initialize-at-build-time=org.lwjgl.system.Callback --initialize-at-build-time=org.lwjgl.system.Platform $(if $optimize; then echo -n --initialize-at-build-time="$mc_classes"; fi) --initialize-at-build-time=com.google --initialize-at-build-time=org.apache.commons --initialize-at-build-time=org.apache.http --initialize-at-run-time=org.apache.http.impl.auth.NTLMEngineImpl --initialize-at-build-time=com.mojang.patchy --initialize-at-build-time=joptsimple --initialize-at-build-time="$allowed_classes" -Dlog4j2.disableJmx=true -Dlog4j2.formatMsgNoLookups=true -Djava.awt.headless=true -Djava.library.path=natives -Dorg.lwjgl.opengl.explicitInit=true -Dorg.lwjgl.util.Debug=true -Dde.kb1000.mcimage.server=$server -H:-PrintImageObjectTree -H:Name=minecraft-$(if $server; then echo -n server; else echo -n client; fi)$(if ! $optimize; then echo -n -unoptimized; fi) -J-javaagent:mc-image-agent/build/libs/mc-image-agent$(if $optimize; then echo -n -optimize; fi).jar $(if $optimize; then echo -n -J--patch-module=java.base=java.base; fi) "${MCIMAGE_BUILD_FLAGS[@]}" net.minecraft.$(if $server; then echo -n server; else echo -n client.main; fi).Main

# vim: et ts=4 sw=4
