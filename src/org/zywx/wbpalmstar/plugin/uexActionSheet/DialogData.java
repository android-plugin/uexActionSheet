package org.zywx.wbpalmstar.plugin.uexActionSheet;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class DialogData {

	private static final String KEY_ACTIONSHEET_STYLE = "actionSheet_style";
	private static final String KEY_FRAME_BGCOLOR = "frameBgColor";
	private static final String KEY_FRAM_BROUNDCOLOR = "frameBroundColor";
	private static final String KEY_FRAME_BG_IMG = "frameBgImg";
	private static final String KEY_ITEM_BTN_SELECT_BGIMG = "btnSelectBgImg";
	private static final String KEY_ITEM_BTN_UNSELECT_BGIMG = "btnUnSelectBgImg";
	private static final String KEY_CANCEL_BTN_SELECT_BGIMG = "cancelBtnSelectBgImg";
	private static final String KEY_CANCEL_BTN_UNSELECT_BGIMG = "cancelBtnUnSelectBgImg";
	private static final String KEY_TEXT_SIZE = "textSize";
	private static final String KEY_TEXT_N_COLOR = "textNColor";
    private static final String KEY_TEXT_H_COLOR = "textHColor";
    private static final String KEY_CANCEL_N_COLOR = "cancelTextNColor";
    private static final String KEY_CANCEL_H_COLOR = "cancelTextHColor";
	
	private static final String KEY_ACTION_SHEET_LIST = "actionSheetList";
	private static final String KEY_ITEM_NAME = "name"; 
	
	private String frameBgColor;
	private String frameBroundColor;
	private String isAngle;
	private String framBgImg;
	private String btnSelectBgImg;
	private String btnUNSelectBgImg;
	private String cancelBtnSelectBgImg;
	private String cancelBtnUnSelectBgImg;
    private String textNColor;
	private String textHColor;
    private String cancelTextNColor;
    private String cancelTextHColor;	
	private ArrayList<String> listStr;
	private Bitmap btnSelectBitmap;
	private Bitmap btnUnSelectBitmap;
	private Bitmap cancelBtnSelectBitmap;
	private Bitmap cancelBtnUnSelectBitmap;
	private int textSize;
	
	public static DialogData parseDialogStyleJson(String dialogStyle){
		if (dialogStyle == null || dialogStyle.length() == 0) {
			return null;
		}
		try {
			JSONObject objJson = new JSONObject(dialogStyle);
			if (objJson != null) {
				JSONObject dialogJson = objJson
						.optJSONObject(KEY_ACTIONSHEET_STYLE);
				if (dialogJson != null) {
					DialogData dialogData = new DialogData();
					dialogData.setFrameBgColor(dialogJson.optString(KEY_FRAME_BGCOLOR));
					dialogData.setFrameBroundColor(dialogJson.optString(KEY_FRAM_BROUNDCOLOR));
					dialogData.setIsAngle("N");
					dialogData.setFramBgImg(dialogJson.optString(KEY_FRAME_BG_IMG));
					dialogData.setBtnSelectBgImg(dialogJson.optString(KEY_ITEM_BTN_SELECT_BGIMG));
					dialogData.setBtnUNSelectBgImg(dialogJson.optString(KEY_ITEM_BTN_UNSELECT_BGIMG));
					dialogData.setCancelBtnSelectBgImg(dialogJson.optString(KEY_CANCEL_BTN_SELECT_BGIMG));
					dialogData.setCancelBtnUnSelectBgImg(dialogJson.optString(KEY_CANCEL_BTN_UNSELECT_BGIMG));
					dialogData.setTextNColor(dialogJson.optString(KEY_TEXT_N_COLOR));
					dialogData.setTextHColor(dialogJson.optString(KEY_TEXT_H_COLOR));
					dialogData.setCancelTextNColor(dialogJson.optString(KEY_CANCEL_N_COLOR));
					dialogData.setCancelTextHColor(dialogJson.optString(KEY_CANCEL_H_COLOR));
					dialogData.setTextSize(dialogJson.optInt(KEY_TEXT_SIZE));
					JSONArray jsonArray = dialogJson.optJSONArray(KEY_ACTION_SHEET_LIST);
					if(jsonArray != null){
						ArrayList<String> list = new ArrayList<String>();
						for(int i=0,length = jsonArray.length(); i < length;i++){
							JSONObject jsonObj = jsonArray.optJSONObject(i);
							String itemName = jsonObj.optString(KEY_ITEM_NAME);
							list.add(itemName);
						}
						dialogData.setListStr(list);
					}
					return dialogData;
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getFrameBgColor() {
		return frameBgColor;
	}

	public void setFrameBgColor(String frameBgColor) {
		this.frameBgColor = frameBgColor;
	}

	public String getFrameBroundColor() {
		return frameBroundColor;
	}

	public void setFrameBroundColor(String frameBroundColor) {
		this.frameBroundColor = frameBroundColor;
	}

	public String getIsAngle() {
		return isAngle;
	}

	public void setIsAngle(String isAngle) {
		this.isAngle = isAngle;
	}

	public String getFramBgImg() {
		return framBgImg;
	}

	public void setFramBgImg(String framBgImg) {
		this.framBgImg = framBgImg;
	}

	public String getBtnSelectBgImg() {
		return btnSelectBgImg;
	}

	public void setBtnSelectBgImg(String btnSelectBgImg) {
		this.btnSelectBgImg = btnSelectBgImg;
	}

	public String getBtnUNSelectBgImg() {
		return btnUNSelectBgImg;
	}

	public void setBtnUNSelectBgImg(String btnUNSelectBgImg) {
		this.btnUNSelectBgImg = btnUNSelectBgImg;
	}

	public String getCancelBtnSelectBgImg() {
		return cancelBtnSelectBgImg;
	}

	public void setCancelBtnSelectBgImg(String cancelBtnSelectBgImg) {
		this.cancelBtnSelectBgImg = cancelBtnSelectBgImg;
	}

	public String getCancelBtnUnSelectBgImg() {
		return cancelBtnUnSelectBgImg;
	}

	public void setCancelBtnUnSelectBgImg(String cancelBtnUnSelectBgImg) {
		this.cancelBtnUnSelectBgImg = cancelBtnUnSelectBgImg;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public ArrayList<String> getListStr() {
		return listStr;
	}

	public void setListStr(ArrayList<String> listStr) {
		this.listStr = listStr;
	}

	public Bitmap getBtnSelectBitmap() {
		return btnSelectBitmap;
	}

	public void setBtnSelectBitmap(Bitmap btnSelectBitmap) {
		this.btnSelectBitmap = btnSelectBitmap;
	}

	public Bitmap getBtnUnSelectBitmap() {
		return btnUnSelectBitmap;
	}

	public void setBtnUnSelectBitmap(Bitmap btnUnSelectBitmap) {
		this.btnUnSelectBitmap = btnUnSelectBitmap;
	}

	public Bitmap getCancelBtnSelectBitmap() {
		return cancelBtnSelectBitmap;
	}

	public void setCancelBtnSelectBitmap(Bitmap cancelBtnSelectBitmap) {
		this.cancelBtnSelectBitmap = cancelBtnSelectBitmap;
	}

	public Bitmap getCancelBtnUnSelectBitmap() {
		return cancelBtnUnSelectBitmap;
	}

	public void setCancelBtnUnSelectBitmap(Bitmap cancelBtnUnSelectBitmap) {
		this.cancelBtnUnSelectBitmap = cancelBtnUnSelectBitmap;
	}

    public String getTextNColor() {
        return textNColor;
    }

    public void setTextNColor(String textNColor) {
        this.textNColor = textNColor;
    }

    public String getTextHColor() {
        return textHColor;
    }

    public void setTextHColor(String textHColor) {
        this.textHColor = textHColor;
    }

    public String getCancelTextNColor() {
        return cancelTextNColor;
    }

    public void setCancelTextNColor(String cancelTextNColor) {
        this.cancelTextNColor = cancelTextNColor;
    }

    public String getCancelTextHColor() {
        return cancelTextHColor;
    }

    public void setCancelTextHColor(String cancelTextHColor) {
        this.cancelTextHColor = cancelTextHColor;
    }
}
