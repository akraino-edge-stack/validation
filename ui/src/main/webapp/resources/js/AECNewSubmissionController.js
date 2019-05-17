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

var AECBlueprintValidationUIApp = angular
		.module('BlueprintValidationUIManagement');

AECBlueprintValidationUIApp
		.controller(
				'AECNewSubmissionController',
				function($scope, appContext, restAPISvc) {

					initialize();

					function initialize() {
						restAPISvc
								.getRestAPI(
										"/api/blueprintData/",
										function(data) {
											$scope.blueprintDatas = data;
											$scope.blueprintNames = [];
											angular
													.forEach(
															$scope.blueprintDatas,
															function(
																	blueprintData) {
																if ($scope.blueprintNames
																		.indexOf(blueprintData["blueprint"]) === -1) {
																	$scope.blueprintNames
																			.push(blueprintData["blueprint"]);
																}
															});
										});
					}
					;

					$scope.selectedBluePrintNameChange = function() {
						$scope.blueprintVersions = [];
						$scope.blueprintLayers = [];
						$scope.declerativeTimeslots = [];
						angular
								.forEach(
										$scope.blueprintDatas,
										function(blueprintData) {
											if ($scope.selectedBlueprintName === blueprintData["blueprint"]) {
												if ($scope.blueprintVersions
														.indexOf(blueprintData["version"]) === -1) {
													$scope.blueprintVersions
															.push(blueprintData["version"]);
												}
											}
										});
					}

					$scope.selectedBluePrintVersionChange = function() {
						$scope.blueprintLayers = [];
						$scope.declerativeTimeslots = [];
						angular
								.forEach(
										$scope.blueprintDatas,
										function(blueprintData) {
											if ($scope.selectedBlueprintName === blueprintData["blueprint"]) {
												if ($scope.selectedBlueprintVersion === blueprintData["version"]) {
													if ($scope.blueprintLayers
															.indexOf(blueprintData["layer"]) === -1) {
														$scope.blueprintLayers
																.push(blueprintData["layer"]);
													}
												}
											}
										});
					}

					$scope.selectedBluePrintLayerChange = function() {
						$scope.declerativeTimeslots = [];
						angular
								.forEach(
										$scope.blueprintDatas,
										function(blueprintData) {
											if ($scope.selectedBlueprintName === blueprintData["blueprint"]) {
												if ($scope.selectedBlueprintVersion === blueprintData["version"]) {
													if ($scope.selectedBlueprintLayer === blueprintData["layer"]) {
														var temp = "id: "
																+ blueprintData["timeslot"].timeslotId
																+ " Start date and time: "
																+ blueprintData["timeslot"].startDateTime
																+ " duration(in sec) :"
																+ blueprintData["timeslot"].duration
																+ " lab :"
																+ blueprintData["timeslot"].lab;
														if ($scope.declerativeTimeslots
																.indexOf(temp) === -1) {
															$scope.declerativeTimeslots
																	.push(temp);
														}
													}
												}
											}
										});
					}

					$scope.submit = function() {
						var finalBlueprint;
						angular
								.forEach(
										$scope.blueprintDatas,
										function(blueprintData) {
											if (blueprintData["blueprint"] === $scope.selectedBlueprintName) {
												if (blueprintData["version"] === $scope.selectedBlueprintVersion) {
													if (blueprintData["layer"] === $scope.selectedBlueprintLayer) {
														var selectedDeclerativeTimeslotId = $scope.selectedDeclerativeTimeslot
																.substring(
																		$scope.selectedDeclerativeTimeslot
																				.indexOf("id:") + 4,
																		$scope.selectedDeclerativeTimeslot
																				.indexOf("Start date and time:") - 1);
														if (selectedDeclerativeTimeslotId
																.toString()
																.trim() === blueprintData["timeslot"]["timeslotId"]
																.toString()
																.trim()) {
															finalBlueprint = blueprintData;
														}
													}
												}
											}
										});
						var submission = {
							"blueprintData" : finalBlueprint
						};
						restAPISvc
								.postRestAPI(
										"/api/submission/",
										submission,
										function(data) {
											if (data !== undefined) {
												confirm("Submission committed successfully");
											} else {
												confirm("Error when committing the submission");
											}
										});
					}

				});
