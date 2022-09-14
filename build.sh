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
            echo Server isn\'t supported yet, aborting # TODO
            exit 1
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
allowed_classes="org.apache.logging,com.sun.org.apache.xerces,jdk.xml.internal.SecuritySupport,javax.xml.parsers.FactoryFinder,jdk.xml.internal.JdkXmlUtils,com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator,jdk.xml.internal.JdkConstants,io.netty.util.internal.SystemPropertyUtil,org.slf4j.LoggerFactory,oshi.util.FileUtil,com.sun.jna.Platform,oshi.SystemInfo,oshi.SystemInfo\$1,oshi.util.ExecutingCommand,oshi.util.Memoizer,oshi.util.ParseUtil,oshi.util.FormatUtil,org.lwjgl.system.Configuration,org.lwjgl.system.APIUtil,org.lwjgl.system.MathUtil,org.lwjgl.system.MultiReleaseTextDecoding,org.lwjgl.system.Pointer,org.lwjgl.system.Pointer\$Default,org.lwjgl.system.Struct,org.lwjgl.system.Checks,org.lwjgl.system.CheckIntrinsics,org.lwjgl.system.ThreadLocalUtil,org.lwjgl.system.MemoryStack,org.lwjgl.system.MemoryUtil\$LazyInit,org.lwjgl.system.APIUtil\$1,org.lwjgl.Version,de.kb1000.mcimage.agent,de.kb1000.mcimage.util,de.kb1000.mcimage.target.lwjgl,com.mojang.blaze3d.platform,com.mojang.datafixers,com.mojang.bridge,it.unimi.dsi.fastutil,$(if $optimize; then echo -n java.awt.AWTEvent,; fi)com.mojang.serialization,com.mojang.brigadier,io.netty.util.concurrent.DefaultPromise"
# Used to disallow JNI libs from being loaded at runtime (by loading them at build time, TODO: this could be better)
lwjgl_library_classes="org.lwjgl.system.Library,org.lwjgl.opengl.GL,org.lwjgl.openal.AL,org.lwjgl.util.tinyfd.TinyFileDialogs"
if $server; then
    mc_libraries="$HOME/.m2/repository/org/slf4j/jcl-over-slf4j/1.8.0-beta4/jcl-over-slf4j-1.8.0-beta4.jar:libraries/com/mojang/minecraft/1.17.1/minecraft-1.17.1-server.jar"
else
    mc_libraries="libraries/com/mojang/patchy/2.1.6/patchy-2.1.6.jar:libraries/com/mojang/blocklist/1.0.5/blocklist-1.0.5.jar:libraries/com/mojang/minecraft/1.17.1/minecraft-1.17.1-client.jar:libraries/org/apache/logging/log4j/log4j-core/2.14.1/log4j-core-2.14.1.jar:libraries/org/apache/logging/log4j/log4j-api/2.14.1/log4j-api-2.14.1.jar:libraries/it/unimi/dsi/fastutil/8.2.1/fastutil-8.2.1.jar:libraries/org/apache/httpcomponents/httpcore/4.3.2/httpcore-4.3.2.jar:$HOME/.m2/repository/org/slf4j/jcl-over-slf4j/1.8.0-beta4/jcl-over-slf4j-1.8.0-beta4.jar:libraries/org/apache/httpcomponents/httpclient/4.3.3/httpclient-4.3.3.jar:libraries/org/apache/commons/commons-compress/1.8.1/commons-compress-1.8.1.jar:libraries/com/mojang/authlib/2.3.31/authlib-2.3.31.jar:libraries/com/google/code/gson/gson/2.8.0/gson-2.8.0.jar:libraries/com/mojang/datafixerupper/4.0.26/datafixerupper-4.0.26.jar:libraries/com/mojang/brigadier/1.0.18/brigadier-1.0.18.jar:libraries/commons-codec/commons-codec/1.10/commons-codec-1.10.jar:libraries/commons-io/commons-io/2.5/commons-io-2.5.jar:libraries/org/apache/commons/commons-lang3/3.5/commons-lang3-3.5.jar:libraries/com/google/guava/guava/21.0/guava-21.0.jar:libraries/io/netty/netty-all/4.1.25.Final/netty-all-4.1.25.Final.jar:libraries/net/sf/jopt-simple/jopt-simple/5.0.3/jopt-simple-5.0.3.jar:libraries/com/mojang/javabridge/1.1.23/javabridge-1.1.23.jar:libraries/com/ibm/icu/icu4j/66.1/icu4j-66.1.jar:libraries/org/apache/logging/log4j/log4j-slf4j18-impl/2.14.1/log4j-slf4j18-impl-2.14.1.jar:libraries/org/slf4j/slf4j-api/1.8.0-beta4/slf4j-api-1.8.0-beta4.jar:libraries/net/java/dev/jna/jna-platform/5.8.0/jna-platform-5.8.0.jar:libraries/net/java/dev/jna/jna/5.8.0/jna-5.8.0.jar:libraries/com/github/oshi/oshi-core/5.7.5/oshi-core-5.7.5.jar:libraries/org/lwjgl/lwjgl/3.2.2/lwjgl-3.2.2.jar:libraries/org/lwjgl/lwjgl-tinyfd/3.2.2/lwjgl-tinyfd-3.2.2.jar:libraries/org/lwjgl/lwjgl-stb/3.2.2/lwjgl-stb-3.2.2.jar:libraries/org/lwjgl/lwjgl-opengl/3.2.2/lwjgl-opengl-3.2.2.jar:libraries/org/lwjgl/lwjgl-openal/3.2.2/lwjgl-openal-3.2.2.jar:libraries/org/lwjgl/lwjgl-jemalloc/3.2.2/lwjgl-jemalloc-3.2.2.jar:libraries/org/lwjgl/lwjgl-glfw/3.2.2/lwjgl-glfw-3.2.2.jar:libraries/com/mojang/text2speech/1.11.3/text2speech-1.11.3.jar"
fi


if $optimize; then
    mc_classes="ab,agv,dh,pg,pc,ww,ags,c" # SharedConstants, JsonHelper, TranslatableBuiltInExceptions (dependency of SharedConstants), TranslatableText (dependency of TranslatableBuiltInExceptions), Style (dependency of TranslatableText), Identifier (dependency of Style), OrderedText (dependency of TranslatableText), Matrix3f

    if ! $server; then
        mc_classes+=",dpg,dpg\$a,dpg\$b,dpc" # InputUtil (InputUtil$Key, InputUtil$Type), Untracker
    fi
fi

# TODO: object tree broken
"$GRAAL_HOME"/bin/native-image "${debug_attach[@]}" "${jfr[@]}" -H:Dump=:3 -H:MethodFilter=java.util.concurrent.ConcurrentSkipListMap.doPut,java.util.concurrent.ConcurrentSkipListMap.add -H:+PrintBackendCFG "${debug[@]}" --diagnostics-mode -H:ReportAnalysisForbiddenType=$(if $optimize; then echo -n java.io.ObjectInputStream; fi) -H:+PrintAnalysisCallTree -H:+EarlyGVN --native-compiler-path=/usr/lib64/ccache/clang --native-compiler-options=-fuse-ld=lld --native-compiler-options=-ggdb --native-compiler-options=-Wl,--trace --native-compiler-options=-flto --enable-http --enable-https -H:+ReportExceptionStackTraces -cp $(if $optimize; then echo -n out/production/mc-image-lib.optimize:; else echo -n mc-image-lib/unoptimized/resources:; fi)out/production/mc-image-lib:hashed-1.17.1.jar:$mc_libraries:$HOME/.m2/repository/net/fabricmc/tiny-mappings-parser/0.3.0+build.17/tiny-mappings-parser-0.3.0+build.17.jar --no-fallback --allow-incomplete-classpath -H:-SpawnIsolates --initialize-at-run-time=org.lwjgl.system.jemalloc.JEmalloc\$Functions,org.lwjgl.system.jemalloc.JEmalloc,org.lwjgl.stb.LibSTB,org.lwjgl.glfw.GLFW --initialize-at-build-time="$lwjgl_library_classes" --initialize-at-build-time=net.minecraft.client.main.Main --initialize-at-build-time=io.netty.util.ResourceLeakDetector,io.netty.buffer.AbstractByteBufAllocator,io.netty.util.ReferenceCountUtil --initialize-at-build-time=oshi.util.GlobalConfig --initialize-at-build-time=com.ibm.icu --initialize-at-build-time=com.mojang.util.QueueLogAppender --initialize-at-build-time=org.lwjgl.system.MemoryUtil --initialize-at-build-time=org.lwjgl.system.Callback --initialize-at-build-time=org.lwjgl.system.Platform $(if $optimize; then echo -n --initialize-at-build-time="$mc_classes"; fi) --initialize-at-build-time=com.google --initialize-at-build-time=org.apache.commons --initialize-at-build-time=org.apache.http --initialize-at-run-time=org.apache.http.impl.auth.NTLMEngineImpl --initialize-at-build-time=com.mojang.patchy --initialize-at-build-time=joptsimple --initialize-at-build-time="$allowed_classes" -Dlog4j2.disableJmx=true -Dlog4j2.formatMsgNoLookups=true -Djava.awt.headless=true -Djava.library.path=natives -Dorg.lwjgl.opengl.explicitInit=true -Dorg.lwjgl.util.Debug=true -H:-PrintImageObjectTree -H:Name=minecraft-$(if $server; then echo -n server; else echo -n client; fi)$(if ! $optimize; then echo -n -unoptimized; fi) -J-javaagent:out/artifacts/mc_image_agent$(if $optimize; then echo -n _optimize; fi)/mc-image-agent.jar -J--patch-module=java.base=java.base "${MCIMAGE_BUILD_FLAGS[@]}" net.minecraft.$(if $server; then echo -n server; else echo -n client.main; fi).Main

# vim: et ts=4 sw=4
