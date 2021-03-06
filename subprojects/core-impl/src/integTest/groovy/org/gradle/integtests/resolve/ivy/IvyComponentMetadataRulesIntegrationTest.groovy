/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.integtests.resolve.ivy

import org.gradle.integtests.resolve.ComponentMetadataRulesIntegrationTest
import org.gradle.test.fixtures.ivy.IvyHttpRepository

class IvyComponentMetadataRulesIntegrationTest extends ComponentMetadataRulesIntegrationTest {
    @Override
    IvyHttpRepository getRepo() {
        ivyHttpRepo
    }

    @Override
    String getRepoDeclaration() {
"""
repositories {
    ivy {
        url "$ivyHttpRepo.uri"
    }
}
"""
    }

    @Override
    String getDefaultStatus() {
        "integration"
    }

    def "can have IvyModuleDescriptor injected into rule"() {
        repo.module('org.test', 'projectA', '1.0').withExtraInfo(foo: "fooValue", bar: "barValue").publish().allowAll()

        buildFile <<
"""
def ruleInvoked = false

dependencies {
    components {
        eachComponent { details, IvyModuleDescriptor descriptor ->
            ruleInvoked = true
            assert descriptor.extraInfo == ["my:foo": "fooValue", "my:bar": "barValue"]
        }
    }
}

resolve.doLast { assert ruleInvoked }
"""

        expect:
        succeeds 'resolve'
        succeeds 'resolve'
    }
}
