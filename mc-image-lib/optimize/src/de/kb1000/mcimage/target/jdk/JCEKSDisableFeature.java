/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.jdk;

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.sun.crypto.provider.SunJCE;
import de.kb1000.mcimage.util.HostedConstants;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.hosted.Feature;

import java.lang.invoke.VarHandle;
import java.security.Provider;
import java.security.Security;
import java.util.Map;

// This keystore type is not used at runtime
@AutomaticFeature
public class JCEKSDisableFeature implements Feature {
    @Platforms(Platform.HOSTED_ONLY.class)
    static final VarHandle PROVIDER_SERVICE_MAP;

    static {
        try {
            PROVIDER_SERVICE_MAP = HostedConstants.IMPL_LOOKUP.findVarHandle(Provider.class, "serviceMap", Map.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void duringSetup(DuringSetupAccess access) {
        access.registerObjectReplacer(o -> {
            if (o instanceof SunJCE jce) {
                @SuppressWarnings("unchecked")
                Map<?, Provider.Service> serviceMap = (Map<?, Provider.Service>) PROVIDER_SERVICE_MAP.get(jce);
                serviceMap.replaceAll((o1, service) -> service.getAlgorithm().equals("JCEKS")
                        ? Security.getProvider("SUN").getService("KeyStore", "JKS")
                        : service);
            }
            return o;
        });
    }
}
