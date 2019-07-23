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

var app = angular.module('DisplayTestSuiteResults');
app
        .controller(
                'DisplayTestSuiteResultsController',

                function($scope, restAPISvc, $location) {

                    initialize();

                    function initialize() {
                        var searchObject = $location.search();
                        $scope.blueprintName = searchObject.blueprintname;
                        $scope.version = searchObject.version;
                        $scope.lab = searchObject.lab;
                        $scope.timestamp = searchObject.timestamp;
                        $scope.submissionId = searchObject.submissionid;
                        $scope.layer = searchObject.layer;
                        $scope.outcome = searchObject.outcome;
                        $scope.selectedTestId = null;
                        $scope.selectedTest = null;
                        $scope.wRobotTestResults = [];
                        $scope.resultsLayers = [];
                        $scope.resultsLayerTestSuitesNames = [];
                        $scope.selectedRobotTestResult = [];
                        $scope.showResults = false;
                        $scope.loadingResults = true;
                        var reqUrl = null;
                        if ($scope.submissionId) {
                            reqUrl = "/api/v1/results/getbysubmissionid/"
                                    + $scope.submissionId;
                            restAPISvc
                                    .getRestAPI(
                                            "/api/v1/submission/"
                                                    + $scope.submissionId,
                                            function(data) {
                                                if (data) {
                                                    $scope.blueprintName = data.blueprintInstanceForValidation.blueprint.blueprintName;
                                                    $scope.version = data.blueprintInstanceForValidation.version;
                                                    $scope.lab = data.timeslot.lab.lab;
                                                }
                                            });
                        } else if ($scope.outcome) {
                            reqUrl = "/api/v1/results/getlastrun/" + $scope.lab
                                    + "/" + $scope.blueprintName + "/"
                                    + $scope.version + "/" + $scope.layer + "/"
                                    + $scope.outcome;
                        } else {
                            reqUrl = "/api/v1/results/getbytimestamp/"
                                    + $scope.lab + "/" + $scope.blueprintName
                                    + "/" + $scope.version + "/"
                                    + $scope.timestamp;
                        }
                        restAPISvc
                                .getRestAPI(
                                        reqUrl,
                                        function(data) {
                                            $scope.loadingResults = false;
                                            if (data) {
                                                $scope.wRobotTestResults = data;
                                                $scope.showResults = true;
                                                angular
                                                        .forEach(
                                                                $scope.wRobotTestResults,
                                                                function(result) {
                                                                    $scope.resultsLayers
                                                                            .push(result.blueprintLayer);
                                                                });
                                            } else {
                                                confirm("No data was found");
                                            }
                                        });
                    }

                    $scope.selectedResultsLayerChange = function(selectedLayer) {
                        $scope.selectedTestId = null;
                        $scope.selectedTest = null;
                        $scope.resultsLayerTestSuitesNames = [];
                        $scope.robotTestResults = [];
                        $scope.selectedRobotTestResult = [];
                        var selectedLayerResult = [];
                        angular.forEach($scope.wRobotTestResults, function(
                                result) {
                            if (result.blueprintLayer === selectedLayer) {
                                selectedLayerResult = result;
                            }
                        });
                        $scope.robotTestResults = selectedLayerResult.robotTestResults;
                        angular.forEach($scope.robotTestResults, function(
                                robotTestResult) {
                            $scope.resultsLayerTestSuitesNames
                                    .push(robotTestResult.name);
                        });
                    }

                    $scope.selectedTestSuitesNameChange = function(
                            selectedTestSuiteName) {
                        if (!selectedTestSuiteName) {
                            return;
                        }
                        $scope.selectedTestId = null;
                        $scope.selectedTest = null;
                        angular
                                .forEach(
                                        $scope.robotTestResults,
                                        function(robotTestResult) {
                                            if (robotTestResult.name.trim() === selectedTestSuiteName
                                                    .trim()) {
                                                $scope.selectedRobotTestResult = robotTestResult;
                                            }
                                        });
                    }

                    $scope.setClickedTest = function(test) {
                        $scope.selectedTestId = test.id;
                        $scope.selectedTest = test;
                    }

                });
