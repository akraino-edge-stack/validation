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

var app = angular.module('GetAllResults');
app.controller('GetAllResultsController', function ($scope, restAPISvc,
    generalGetAllResultsSvc, $location, sharedContext) {

    initialize();

    function initialize() {
        $scope.descending = true;

        $scope.wrapperTimestampRobotTestResult = sharedContext.getData("wrapperTimestampRobotTestResult");
        $scope.blueprints = sharedContext.getData("blueprints");
        $scope.versions = sharedContext.getData("versions");
        $scope.labs = sharedContext.getData("labs");

        $scope.getBlueprintLayers = generalGetAllResultsSvc.getBlueprintLayers;
        $scope.mapResult = generalGetAllResultsSvc.mapResult;
        $scope.filterWithLayer = generalGetAllResultsSvc.filterWithLayer;
        $scope.filterWithResult = generalGetAllResultsSvc.filterWithResult;

        if (!$scope.wrapperTimestampRobotTestResult || !$scope.wrapperTimestampRobotTestResult.timestampRobotTestResults || $scope.wrapperTimestampRobotTestResult.timestampRobotTestResults.length === 0) {
            $scope.showResults = false;
        } else {
            $scope.showResults = true;
        }
        $scope.loadingLabs = true;
        $scope.loadingBlueprints = false;
        $scope.loadingVersions = false;
        $scope.loadingResults = false;
        restAPISvc.getRestAPI("/api/v1/results/getlabs/", function (data) {
            $scope.labs = data;
            $scope.loadingLabs = false;
        });
    }

    $scope.selectedLabChange = function () {
        $scope.showResults = false;
        $scope.loadingLabs = false;
        $scope.loadingBlueprints = true;
        $scope.loadingVersions = false;
        $scope.loadingResults = false;
        $scope.blueprints = [];
        $scope.versions = [];
        $scope.wrapperTimestampRobotTestResult = [];
        restAPISvc.getRestAPI("/api/v1/results/getblueprintnamesoflab/"
            + $scope.selectedLab, function (data) {
                $scope.blueprints = data;
                $scope.loadingBlueprints = false;
            });
    }

    $scope.selectedBlueprintChange = function () {
        if (!$scope.selectedLab) {
            return;
        }
        $scope.showResults = false;
        $scope.loadingLabs = false;
        $scope.loadingBlueprints = false;
        $scope.loadingVersions = true;
        $scope.loadingResults = false;
        $scope.versions = [];
        $scope.wrapperTimestampRobotTestResult = [];
        restAPISvc.getRestAPI("/api/v1/results/getblueprintversions/"
            + $scope.selectedBlueprint + "/" + $scope.selectedLab,
            function (data) {
                $scope.versions = data;
                $scope.loadingVersions = false;
            });
    }

    $scope.selectedVersionChange = function () {
        if ($scope.selectedLab && $scope.selectedBlueprint
            && $scope.selectedVersion) {
            $scope.loadingResults = true;
            restAPISvc.getRestAPI("/api/v1/results/getbyblueprint/"
                + $scope.selectedBlueprint + "/" + $scope.selectedVersion
                + "/" + $scope.selectedLab, function (data) {
                    $scope.loadingResults = false;
                    $scope.wrapperTimestampRobotTestResult = data;
                    $scope.showResults = true;
                });
        }
    }

    $scope.getDetailedResults = function (timestampResult) {
        sharedContext.addData("labs", $scope.labs);
        sharedContext.addData("blueprints", $scope.blueprints);
        sharedContext.addData("versions", $scope.versions);
        sharedContext.addData("timestampResult", timestampResult);
        sharedContext.addData("wrapperTimestampRobotTestResult", $scope.wrapperTimestampRobotTestResult);
        $location.path("displayDetailedResults");
    }

    $scope.dateTimeSort = function (timestampRobotTestResult) {
        return new Date(timestampRobotTestResult.dateOfStorage).getTime();
    }

    $scope.descendingOrder = function () {
        $scope.descending = true;
    }

    $scope.ascendingOrder = function () {
        $scope.descending = false;
    }

    $scope.refreshWrapperTimestampRobotTestResult = function () {
        $scope.selectedVersionChange();
    }

});
