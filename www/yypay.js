cordova.define("cordova/plugins/yypay",function(require, exports, module) {

	var exec = require("cordova/exec");

	var yypay = function() {};

	yypay.prototype.pay = function(trade_code,mode,successCallback, errorCallback) {

		if (errorCallback == null) { errorCallback = function() {}}

		if (typeof errorCallback != "function" || typeof successCallback != "function")  {

			errorCallback('参数错误！');

			return;

		}

		exec(successCallback, errorCallback, 'YYpay', 'unionpay', [{"trade_code":trade_code, "payMode": mode}]);

	};

  module.exports = new yypay();

});


if(!cordova.plugins) cordova.plugins = {};

if (!cordova.plugins.yypay) cordova.plugins.yypay = cordova.require("cordova/plugins/yypay");
