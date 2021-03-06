/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.SameNameAndDescriptionForRouteNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to validate that a Route description is different than the Route name.
 */
public class ValidateRouteDescriptionAndNameAreDifferent {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     */
    public ValidateRouteDescriptionAndNameAreDifferent(final GtfsDataRepository dataRepo,
                                                       final ValidationResultRepository resultRepo,
                                                       final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if Route description is the same as Route long and short names
     * for every Routes in a {@link GtfsDataRepository}. A new notice is generated each time this condition is true.
     * This notice is then added to the {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E024 - Same name and description for route'");
        dataRepo.getRouteAll().values().stream()
                .filter(route -> !(isValidRouteDesc(route.getRouteDesc(),
                        route.getRouteShortName(),
                        route.getRouteLongName())))
                .forEach(route -> resultRepo.addNotice(
                        new SameNameAndDescriptionForRouteNotice(
                                "routes.txt",
                                route.getRouteId()))
                );
    }

    /**
     * @param routeDesc      the description of a Route
     * @param routeShortName the short name of a Route
     * @param routeLongName  the long name of a Route
     * @return true if Route description is the same as Route short or long name, false if not or null.
     */
    private boolean isValidRouteDesc(final String routeDesc, final String routeShortName, final String routeLongName) {
        return routeDesc == null || (!routeDesc.equals(routeShortName) && !routeDesc.equals(routeLongName));
    }
}
