package com.yonyou.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.unionpay.UPPayAssistEx;

/**
 *
 * 基于Cordova实现的银联插件
 *
 * @author Vicent
 *
 */
public class YYpayPlugin extends CordovaPlugin  {

	public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;

    private String mMode = "00"; // 支付环境，01表示测试环境，00表示正式环境

    private CallbackContext callback;

	public boolean execute(String action , JSONArray args , CallbackContext callbackContext) {

		if (action.equals("unionpay")) {	// 银联支付动作

			try {

				if(args.isNull(0)){

					callbackContext.error("无效的参数");

					return false;

				}else{

					JSONObject json = args.optJSONObject(0);

					String trade_code = json.optString("trade_code");	//订单流水号
					mMode = json.optString("payMode");
					if(TextUtils.isEmpty(mMode)){
						mMode = "00";
					}
					this.callback = callbackContext;
					startUnionPay(trade_code);
				}

				return true;

			} catch (Exception ex) {

				callbackContext.error("支付失败！");

				return false;
			}

		} else {
			callbackContext.error("无效的Action");
			return false;
		}

	}


	private void startUnionPay(String code){
		cordova.setActivityResultCallback(this);
		int ret = UPPayAssistEx.startPay(cordova.getActivity(), null, null, code, mMode);
        if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
            builder.setTitle("提示");
            builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UPPayAssistEx.installUPPayPlugin(cordova.getActivity());
                            dialog.dismiss();
                        }
                    });

            builder.setPositiveButton("取消",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        int code = -1;
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            code = 0;
						msg = "支付成功";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "您取消了支付";
            code = -2;
        }
        JSONObject json = new JSONObject();
        try {
        	json.put("code", code);
        	json.put("msg", msg);
		} catch (Exception e) {
			// TODO: handle exception
		}
        callback.success(json);
    }

}
