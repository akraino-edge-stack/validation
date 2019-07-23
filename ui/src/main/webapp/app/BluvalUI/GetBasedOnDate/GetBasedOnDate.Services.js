/*
 * Copyright (c) 2019 AT&T Intellectual Property. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var app = angular.module('GetBasedOnDate');

app.factory('generalGetMostRecentSvc', [ function() {
    var svc = [];
    svc.getBlueprintLayers = function(wrapperRobotTestResults) {
        var layers = [];
        angular.forEach(wrapperRobotTestResults, function(
                wrapperRobotTestResult) {
            if (wrapperRobotTestResult.blueprintLayer !== undefined) {
                layers.push(wrapperRobotTestResult.blueprintLayer);
            }
        });
        return layers;
    };
    svc.mapResult = function(result) {
        if (result === true) {
            return 'SUCCESS';
        }
        return 'FAILURE'
    };
    svc.filterWithLayer = function(timestampResults, filterLayer) {
        if (filterLayer === undefined || filterLayer === '') {
            return timestampResults;
        }
        var filteredResults = [];
        angular.forEach(timestampResults, function(timestampResult) {
            angular.forEach(timestampResult.wrobotTestResults, function(
                    wrobotTestResult) {
                if (wrobotTestResult.blueprintLayer.toLowerCase().includes(
                        filterLayer.toLowerCase())) {
                    filteredResults.push(timestampResult);
                }
            });
        });
        return filteredResults;
    }
    svc.filterWithResult = function(timestampResults, filterResult) {
        if (filterResult === undefined || filterResult === '') {
            return timestampResults;
        }
        var filteredResults = [];
        angular.forEach(timestampResults, function(timestampResult) {
            if (timestampResult.result === true
                    && 'success'.includes(filterResult.toLowerCase())) {
                filteredResults.push(timestampResult);
            } else if (timestampResult.result === false
                    && 'failure'.includes(filterResult.toLowerCase())) {
                filteredResults.push(timestampResult);
            }
        });
        return filteredResults;
    }
    return svc;
} ]);