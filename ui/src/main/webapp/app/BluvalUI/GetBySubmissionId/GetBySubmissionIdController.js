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

var app = angular.module('GetBySubmissionId');
app
        .controller(
                'GetBySubmissionIdController',
                function($scope, restAPISvc, appContext, $window) {

                    initialize();

                    function initialize() {
                        restAPISvc
                                .getRestAPI(
                                        "/api/v1/submission/",
                                        function(data) {
                                            $scope.submissions = data;
                                            $scope.submissionsForDisplay = [];
                                            angular
                                                    .forEach(
                                                            $scope.submissions,
                                                            function(
                                                                    submissionData) {
                                                                if (submissionData.submissionStatus === "Completed") {
                                                                    var temp = "id: "
                                                                            + submissionData.submissionId
                                                                            + " blueprint: "
                                                                            + submissionData.blueprintInstanceForValidation.blueprint.blueprintName
                                                                            + " version: "
                                                                            + submissionData.blueprintInstanceForValidation.version
                                                                            + " layer: "
                                                                            + submissionData.blueprintInstanceForValidation.layer
                                                                            + " lab: "
                                                                            + submissionData.timeslot.lab.lab
                                                                            + " Start date and time: "
                                                                            + submissionData.timeslot.startDateTime
                                                                    /*
                                                                     * + "
                                                                     * duration: " +
                                                                     * submissionData.blueprintInstanceForValidation.timeslot.duration
                                                                     */;
                                                                    $scope.submissionsForDisplay
                                                                            .push(temp);
                                                                }
                                                            });
                                        });
                    }
                    $scope.selectedSubmissionChange = function(
                            selectedSubmission) {
                        var id = selectedSubmission.substring(
                                selectedSubmission.indexOf("id:") + 4,
                                selectedSubmission.indexOf("blueprint") - 1);
                        $window.location.href = appContext
                                + "/displaytestsuiteresults#?submissionid="
                                + id;
                    }

                });
