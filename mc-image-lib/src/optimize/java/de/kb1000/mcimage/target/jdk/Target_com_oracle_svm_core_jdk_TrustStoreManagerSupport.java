/*
 * Copyright (c) 2021-2022 kb1000.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.kb1000.mcimage.target.jdk;

import com.oracle.svm.core.annotate.AlwaysInline;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "com.oracle.svm.core.jdk.TrustStoreManagerSupport")
final class Target_com_oracle_svm_core_jdk_TrustStoreManagerSupport {
    @Substitute
    @AlwaysInline("")
    static Target_sun_security_ssl_TrustStoreManager_TrustStoreDescriptor getRuntimeTrustStoreDescriptor() {
        return null;
    }
}

@TargetClass(className = "sun.security.ssl.TrustStoreManager", innerClass = "TrustStoreDescriptor")
final class Target_sun_security_ssl_TrustStoreManager_TrustStoreDescriptor {
}
