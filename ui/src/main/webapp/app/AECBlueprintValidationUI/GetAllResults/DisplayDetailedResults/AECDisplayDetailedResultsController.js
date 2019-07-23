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

var app = angular.module('AECGetAllResults');
app
    .controller(
        'AECDisplayDetailedResultsController',
        function ($scope, restAPISvc, sharedContext, generalGetAllResultsSvc) {

            initialize();

            function initialize() {
                $scope.selectedTestId = null;
                $scope.selectedTest = null;
                $scope.mapResult = generalGetAllResultsSvc.mapResult;
                $scope.timestampResult = sharedContext.getData("timestampResult");
                $scope.wrapperTimestampRobotTestResult = sharedContext.getData("wrapperTimestampRobotTestResult");

                $scope.results = [];
                $scope.resultsLayers = [];
                $scope.resultsLayerTestSuitesNames = [];
                $scope.robotTestResults = [];
                $scope.selectedRobotTestResult = [];
                angular
                    .forEach(
                        $scope.timestampResult.wrapperRobotTestResults,
                        function (result) {
                            $scope.results
                                .push(result);
                            $scope.resultsLayers
                                .push(result.blueprintLayer);
                        });
            }

            $scope.selectedResultsLayerChange = function (selectedLayer) {
                $scope.resultsLayerTestSuitesNames = [];
                $scope.robotTestResults = [];
                $scope.selectedRobotTestResult = [];
                var selectedLayerResult = [];
                angular.forEach($scope.results, function (result) {
                    if (result.blueprintLayer === selectedLayer) {
                        selectedLayerResult = result;
                    }
                });
                $scope.robotTestResults = selectedLayerResult.robotTestResults;
                angular.forEach($scope.robotTestResults, function (
                    robotTestResult) {
                    $scope.resultsLayerTestSuitesNames
                        .push(robotTestResult.name);
                });
            }

            $scope.selectedTestSuitesNameChange = function (
                selectedTestSuiteName) {
                angular
                    .forEach(
                        $scope.robotTestResults,
                        function (robotTestResult) {
                            if (robotTestResult.name.trim() === selectedTestSuiteName
                                .trim()) {
                                $scope.selectedRobotTestResult = robotTestResult;
                            }
                        });
            }

            $scope.setClickedTest = function (test) {
                $scope.selectedTestId = test.id;
                $scope.selectedTest = test;
            }

        });
