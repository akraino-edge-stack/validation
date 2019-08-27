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

var app = angular.module('GetByTimestamp');
app.controller('GetByTimestampController',

function($scope, restAPISvc, $window, appContext) {
    initialize();

    function initialize() {
        $scope.loadingLabs = true;
        $scope.loadingBlueprints = false;
        $scope.loadingVersions = false;
        restAPISvc.getRestAPI("/api/v1/results/getlabs/", function(data) {
            $scope.labs = data;
            $scope.loadingLabs = false;
        });
    }

    $scope.selectedLabChange = function() {
        $scope.loadingLabs = false;
        $scope.loadingBlueprints = true;
        $scope.loadingVersions = false;
        $scope.blueprints = [];
        $scope.versions = [];
        $scope.selectedBlueprint = {};
        $scope.selectedVersion = {};
        restAPISvc.getRestAPI("/api/v1/results/getblueprintnamesoflab/"
                + $scope.selectedLab, function(data) {
            $scope.blueprints = data;
            $scope.loadingBlueprints = false;
        });
    }

    $scope.selectedBlueprintChange = function() {
        if (!$scope.selectedLab) {
            return;
        }
        $scope.loadingLabs = false;
        $scope.loadingBlueprints = false;
        $scope.loadingVersions = true;
        $scope.versions = [];
        $scope.selectedVersion = {};
        restAPISvc.getRestAPI("/api/v1/results/getblueprintversions/"
                + $scope.selectedBlueprint + "/" + $scope.selectedLab,
                function(data) {
                    $scope.versions = data;
                    $scope.loadingVersions = false;
                });
    }

    $scope.selectedVersionChange = function() {
        $scope.loadingLabs = false;
        $scope.loadingBlueprints = false;
        $scope.loadingVersions = false;
        $scope.loadingResults = false;
    }

    $scope.get = function() {
        if (!$scope.selectedLab || !$scope.selectedBlueprint
                || !$scope.selectedVersion || !$scope.definedTimestamp) {
            confirm("You must specify all data fields");
            return;
        }

        $window.location.href = appContext
                + "/displaytestsuiteresults#?blueprintname="
                + $scope.selectedBlueprint + "&" + "version="
                + $scope.selectedVersion + "&" + "lab=" + $scope.selectedLab
                + "&" + "timestamp=" + $scope.definedTimestamp;
    }

});
