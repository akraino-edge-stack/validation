/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright Â© 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use
 * this software except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is
 * licensed under the Creative Commons License, Attribution 4.0 Intl. (the
 * "License"); you may not use this documentation except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing,
 * documentation distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 *
 */
package org.akraino.validation.ui.service;

import java.util.Set;

import org.onap.portalapp.service.IAdminAuthExtension;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("adminAuthExtension")
@Transactional
/**
 * Provides empty implementations of the methods in IAdminAuthExtension.
 */
public class AdminAuthExtension implements IAdminAuthExtension {

    private static final EELFLoggerDelegate LOGGER = EELFLoggerDelegate.getLogger(AdminAuthExtension.class);

    @Override
    public void saveUserExtension(User user) {
        LOGGER.debug(EELFLoggerDelegate.debugLogger, "saveUserExtension");
    }

    @Override
    public void editUserExtension(User user) {
        LOGGER.debug(EELFLoggerDelegate.debugLogger, "editUserExtension");
    }

    @Override
    public void saveUserRoleExtension(Set<Role> roles, User user) {
        LOGGER.debug(EELFLoggerDelegate.debugLogger, "saveUserRoleExtension");
    }

}
