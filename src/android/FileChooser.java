package com.megster.cordova;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

public class FileChooser extends CordovaPlugin {

    private static final String TAG = "FileChooser";
    private static final String ACTION_OPEN = "open";
    private static final int PICK_FILE_REQUEST = 1;
    CallbackContext callback;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_OPEN)) {
            chooseFile(callbackContext);
            return true;
        }

        return false;
    }

    public void chooseFile(CallbackContext callbackContext) {

        // type and title should be configurable

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); 

        Intent chooser = Intent.createChooser(intent, "Select or several Files");
        cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FILE_REQUEST && callback != null && data != null) {

            if (resultCode == Activity.RESULT_OK) {
				List<String> uris = new ArrayList<String>();
				if(null != data.getClipData()) { // checking multiple selection or not
					for(int i = 0; i < data.getClipData().getItemCount(); i++) {
						Uri uri = data.getClipData().getItemAt(i).getUri();
						uris.add(uri.toString());
						Log.w(TAG, uri.toString());
					}
				} else {
					Uri uri = data.getData();
					if (uri != null) {
					} else {
						callback.error("File uri was null");
						return;
					}
				}
                callback.success(uris.toString());



            } else if (resultCode == Activity.RESULT_CANCELED) {

                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                callback.sendPluginResult(pluginResult);

            } else {

                callback.error(resultCode);
            }
        }
    }
}
