var app = angular.module('myApp', []);

app.controller('myCtrl', function ($scope, $http) {
    $scope.countries = [];
    $scope.cities = [];
    $scope.weathers = [];

    var url = "countries";

    $http.get(url).then(function(response) {
        $scope.countries = response.data;
    }, function (response) {
        $scope.countries = [];
        alert('Сетевая ошибка');
    });

    $scope.updateCountry = function () {
        if($scope.country !== null && $scope.country !== undefined) {
            var params = {
                "country" : $scope.country
            };

            var url = "cities?" + $.param(params);

            $http.get(url).then(function (response) {
                    $scope.cities = response.data;
                    $scope.updateCity();
                }, function (response) {
                    $scope.cities = [];
                    $scope.updateCity();
                    alert('Сетевая ошибка');
            });
        } else {
            $scope.cities = [];
        }
    }

    $scope.updateCity = function () {
        if ($scope.country !== null && $scope.country !== undefined &&
        $scope.city !== null && $scope.city !== undefined) {
            var params = {
                "country" : $scope.country,
                "city" : $scope.city
            };

            var url = "weather?" + $.param(params);

            $http.get(url).then(function (response) {
                $scope.weathers = response.data;
            }, function (response) {
                $scope.weathers = [];
                alert('Сетевая ошибка!');
            });
        } else {
            $scope.weathers = [];
        }
    }
});