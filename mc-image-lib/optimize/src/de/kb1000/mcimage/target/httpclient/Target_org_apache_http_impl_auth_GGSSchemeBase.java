/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.httpclient;

import com.oracle.svm.core.SubstrateUtil;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.AlwaysInline;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import de.kb1000.mcimage.util.Environment;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.auth.KerberosScheme;
import org.apache.http.impl.auth.SPNegoScheme;

@TargetClass(className = "org.apache.http.impl.auth.GGSSchemeBase", onlyWith = Environment.ClientOnly.class)
final class Target_org_apache_http_impl_auth_GGSSchemeBase {
    @Alias
    Log log;
    @Alias
    Base64 base64codec;
    @Alias
    boolean stripPort;
}

final class GGSSchemeBaseSupport {
    @AlwaysInline("")
    static void initialize(Target_org_apache_http_impl_auth_GGSSchemeBase scheme, Log log, boolean stripPort) {
        scheme.log = log;
        scheme.base64codec = new Base64(0);
        scheme.stripPort = stripPort;
    }
}

@TargetClass(className = "org.apache.http.impl.auth.SPNegoScheme", onlyWith = Environment.ClientOnly.class)
final class Target_org_apache_http_impl_auth_SPNegoScheme {
    @Substitute
    Target_org_apache_http_impl_auth_SPNegoScheme(final boolean stripPort) {
        GGSSchemeBaseSupport.initialize(SubstrateUtil.cast(this, Target_org_apache_http_impl_auth_GGSSchemeBase.class), LogFactory.getLog(SPNegoScheme.class), stripPort);
    }

    @Substitute
    Target_org_apache_http_impl_auth_SPNegoScheme() {
        this(false);
    }
}

@TargetClass(className = "org.apache.http.impl.auth.KerberosScheme", onlyWith = Environment.ClientOnly.class)
final class Target_org_apache_http_impl_auth_KerberosScheme {
    @Substitute
    Target_org_apache_http_impl_auth_KerberosScheme(final boolean stripPort) {
        GGSSchemeBaseSupport.initialize(SubstrateUtil.cast(this, Target_org_apache_http_impl_auth_GGSSchemeBase.class), LogFactory.getLog(KerberosScheme.class), stripPort);
    }

    @Substitute
    Target_org_apache_http_impl_auth_KerberosScheme() {
        this(false);
    }
}