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
				function($scope, $http, $filter, $state, $controller,
						appContext, $interval, $rootScope, restAPISvc) {

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
						restAPISvc.getRestAPI("/api/timeslots/",
								function(data) {
									$scope.completeTimeslots = data;
									$scope.timeslots = [];
									angular.forEach(data, function(timeslot) {
										var temp = "Start date and time: "
												+ timeslot.startDateTime
												+ " duration(in sec) :"
												+ timeslot.duration;
										$scope.timeslots.push(temp);
									});
								});
					}
					;

					$scope.selectedBluePrintNameChange = function() {
						$scope.blueprintVersions = [];
						$scope.blueprintLabs = [];
						$scope.blueprintLayers = [];
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
												if ($scope.blueprintLabs
														.indexOf(blueprintData["lab"]) === -1) {
													$scope.blueprintLabs
															.push(blueprintData["lab"]);
												}
												if ($scope.blueprintLayers
														.indexOf(blueprintData["layerData"]["layer"]) === -1) {
													$scope.blueprintLayers
															.push(blueprintData["layerData"]["layer"]
																	+ " "
																	+ blueprintData["layerData"]["description"]);
												}
											}
										});
					}

					$scope.selectedBluePrintVersionChange = function() {
						$scope.blueprintLabs = [];
						$scope.blueprintLayers = [];
						angular
								.forEach(
										$scope.blueprintDatas,
										function(blueprintData) {
											if ($scope.selectedBlueprintName === blueprintData["blueprint"]) {
												if ($scope.selectedBlueprintVersion === blueprintData["version"]) {
													if ($scope.blueprintLabs
															.indexOf(blueprintData["lab"]) === -1) {
														$scope.blueprintLabs
																.push(blueprintData["lab"]);
													}
													if ($scope.blueprintLayers
															.indexOf(blueprintData["layerData"]["layer"]) === -1) {
														$scope.blueprintLayers
																.push(blueprintData["layerData"]["layer"]
																		+ " "
																		+ blueprintData["layerData"]["description"]);
													}
												}
											}
										});
					}

					$scope.selectedBluePrintLabChange = function() {
						$scope.blueprintLayers = [];
						angular
								.forEach(
										$scope.blueprintDatas,
										function(blueprintData) {
											if ($scope.selectedBlueprintName === blueprintData["blueprint"]) {
												if ($scope.selectedBlueprintVersion === blueprintData["version"]) {
													if ($scope.selectedBlueprintLab === blueprintData["lab"]) {
														if ($scope.blueprintLayers
																.indexOf(blueprintData["layerData"]["layer"]) === -1) {
															$scope.blueprintLayers
																	.push(blueprintData["layerData"]["layer"]
																			+ " "
																			+ blueprintData["layerData"]["description"]);
														}
													}
												}
											}
										});
					}

					$scope.submit = function() {
						var selectedLayerData = {
							"layer" : $scope.selectedBlueprintLayer.substr(0,
									$scope.selectedBlueprintLayer.indexOf(' ')),
							"description" : $scope.selectedBlueprintLayer
									.substr($scope.selectedBlueprintLayer
											.indexOf(' ') + 1)
						};
						var selectedBlueprintData = {
							"blueprint" : $scope.selectedBlueprintName,
							"version" : $scope.selectedBlueprintVersion,
							"layerData" : selectedLayerData,
							"lab" : $scope.selectedBlueprintLab
						};
						var selectedTimeslot = {
							"startDateTime" : $scope.selectedTimeslot
									.substr(
											$scope.selectedTimeslot
													.indexOf('Start date and time: ') + 21,
											$scope.selectedTimeslot
													.indexOf('duration(in sec) :') - 21),
							"duration" : $scope.selectedTimeslot
									.substr($scope.selectedTimeslot
											.indexOf(' duration(in sec) :') + 19)
						};

						var initialCandidateBlueprints = [];
						var finalBlueprint;
						angular
								.forEach(
										$scope.blueprintDatas,
										function(blueprintData) {
											if (blueprintData["layerData"]["layer"] === selectedLayerData["layer"]) {
												if (blueprintData["layerData"]["description"] === selectedLayerData["description"]) {
													initialCandidateBlueprints
															.push(blueprintData);
												}
											}
										});
						angular
								.forEach(
										initialCandidateBlueprints,
										function(blueprintData) {
											if (blueprintData["blueprint"] === selectedBlueprintData["blueprint"]) {
												if (blueprintData["version"] === selectedBlueprintData["version"]) {
													if (blueprintData["lab"] === selectedBlueprintData["lab"]) {
														finalBlueprint = blueprintData;
													}
												}
											}
										});
						var finalTimeslot;
						angular
								.forEach(
										$scope.completeTimeslots,
										function(timeslot) {
											if (timeslot["duration"].toString()
													.trim() === selectedTimeslot["duration"]
													.toString().trim()) {
												if (timeslot["startDateTime"]
														.toString().trim() === selectedTimeslot["startDateTime"]
														.toString().trim()) {
													finalTimeslot = timeslot;
												}
											}
										});

						var submission = {
							"blueprintData" : finalBlueprint,
							"timeslot" : finalTimeslot
						};
						restAPISvc.postRestAPI("/api/submission/", submission,
								function() {
								});
					}

				});
