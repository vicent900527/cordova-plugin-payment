cordova.define("cordova/plugins/Unionpay",function(require, exports, module) {

	var exec = require("cordova/exec");

	var Unionpay = function() {};

	Unionpay.prototype.pay = function(trade_code,mode,successCallback, errorCallback) {

		if (errorCallback == null) { errorCallback = function() {}}

		if (typeof errorCallback != "function" || typeof successCallback != "function")  {

			errorCallback('参数错误！');

			return;

		}

		exec(successCallback, errorCallback, 'Unionpay', 'unionpay', [{"trade_code":out_trade_no, "payMode": mode}]);

	};

    module.exports = new Unionpay();

});


if(!window.plugins) window.plugins = {};

if (!window.plugins.Alipay) window.plugins.Unionpay = cordova.require("cordova/plugins/Unionpay");
