/*
  *Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  *	Educational Community License, Version 2.0 (the "License"); you may
  *	not use this file except in compliance with the License. You may
  *	obtain a copy of the License at
  *
  *http://www.osedu.org/licenses/ECL-2.0
  *
  *	Unless required by applicable law or agreed to in writing,
  *	software distributed under the License is distributed on an "AS IS"
  *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  *	or implied. See the License for the specific language governing
  *	permissions and limitations under the License.
  */

package metridoc.ezproxy.services

import metridoc.core.services.CamelService
import metridoc.core.services.ConfigService
import metridoc.core.services.DefaultService
import metridoc.core.services.ParseArgsService
import metridoc.service.gorm.GormService

/**
 * Created with IntelliJ IDEA on 9/24/13
 * @author Tommy Barker
 */
class EzproxyWireService extends DefaultService {

    boolean preview

    EzproxyStepsService wireupServices(Class ezproxyIngestClass) {
        includeService(ParseArgsService)
        includeService(ConfigService)

        wireupNonConfigServices(ezproxyIngestClass)
    }

    protected EzproxyStepsService wireupNonConfigServices(Class ezproxyIngestClass) {
        if (!preview) {
            try {
                includeService(GormService).enableFor(ezproxyIngestClass)
            }
            catch (IllegalStateException ignored) {
                //already enabled
            }
        }

        def camelService = includeService(CamelService)
        def ezproxyFileFilter = includeService(EzproxyFileFilterService, entityClass: ezproxyIngestClass)
        camelService.bind("ezproxyFileFilter", ezproxyFileFilter)

        includeService(EzproxyIngestService, entityClass: ezproxyIngestClass)
        includeService(EzproxyStepsService)
    }


}
