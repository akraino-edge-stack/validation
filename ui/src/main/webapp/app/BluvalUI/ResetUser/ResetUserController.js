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

var app = angular.module('ResetUser');
app.controller('ResetUserController', function($scope, restAPISvc) {

    initialize();

    function initialize() {
        $scope.definedLoginId = '';
        $scope.definedNewLoginPwd = '';
    }

    $scope.register = function() {
        if (!$scope.definedLoginId || !$scope.definedNewLoginPwd) {
            confirm("You must specify all data fields");
            return;
        }
        var userInfo = {
            "loginId" : $scope.definedLoginId,
            "loginPwd" : $scope.definedNewLoginPwd
        };
        restAPISvc.postRestAPI("/api/v1/user/", userInfo, function(data) {
            if (data) {
                var confirmText = "The user has been reset successfully.";
                confirm(confirmText);
            } else {
                confirm("Error when resetting the user");
            }
            initialize();
        });
    }
});
