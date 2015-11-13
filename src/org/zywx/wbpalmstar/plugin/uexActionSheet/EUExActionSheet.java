package org.zywx.wbpalmstar.plugin.uexActionSheet;

import java.util.ArrayList;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexActionSheet.ActionSheetDialog.ActionSheetDialogItemClickListener;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

public class EUExActionSheet extends EUExBase {

    public static final String CALLBACK_RESULT_DATA = "uexActionSheet.onClickItem";
    private static final int MSG_OPEN = 1;
    private static final String TAG_PARAM = "data";
    private String[] m_inButtonLables;
	private String m_inCancel = "取消";
	public EUExActionSheet(Context context, EBrowserView inParent) {
		super(context, inParent);
	}

	@Override
	protected boolean clean() {
		return false;
	}

    public void open(String[] params){
        if(params == null || params.length < 5)
            return;
        Message msg = new Message();
        msg.what = MSG_OPEN;
        Bundle bd = new Bundle();
        bd.putStringArray(TAG_PARAM, params);
        msg.obj = this;
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

	public void openMsg(String[] params){
		try {
			final float x = Float.parseFloat(params[0]);
			final float y = Float.parseFloat(params[1]);
			final float w = Float.parseFloat(params[2]);
			final float h = Float.parseFloat(params[3]);
			String dialogStyle = params[4];
			final DialogData dialogData = DialogData.parseDialogStyleJson(dialogStyle);
			if(dialogData != null){
				dialogData.setBtnSelectBgImg(BUtility.makeRealPath(
						BUtility.makeUrl(mBrwView.getCurrentUrl(), dialogData.getBtnSelectBgImg()),
						mBrwView.getCurrentWidget().m_widgetPath,
						mBrwView.getCurrentWidget().m_wgtType));
				dialogData.setBtnUNSelectBgImg(BUtility.makeRealPath(
						BUtility.makeUrl(mBrwView.getCurrentUrl(), dialogData.getBtnUNSelectBgImg()),
						mBrwView.getCurrentWidget().m_widgetPath,
						mBrwView.getCurrentWidget().m_wgtType));
				dialogData.setCancelBtnSelectBgImg(BUtility.makeRealPath(
						BUtility.makeUrl(mBrwView.getCurrentUrl(), dialogData.getCancelBtnSelectBgImg()),
						mBrwView.getCurrentWidget().m_widgetPath,
						mBrwView.getCurrentWidget().m_wgtType));
				dialogData.setCancelBtnUnSelectBgImg(BUtility.makeRealPath(
						BUtility.makeUrl(mBrwView.getCurrentUrl(), dialogData.getCancelBtnUnSelectBgImg()),
						mBrwView.getCurrentWidget().m_widgetPath,
						mBrwView.getCurrentWidget().m_wgtType));
			}
            ArrayList<String> dataList = dialogData.getListStr();
            if (dataList != null)
                m_inButtonLables = (String[]) (dataList.toArray(new String[dataList.size()]));
            actionSheet((int) x, (int) y, (int) w, (int) h, dialogData);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	
	private void actionSheet(int x,int y,int width,int height,DialogData dialogData) {
		ActionSheetDialog.show(mContext, m_inButtonLables, "", m_inCancel,
				new ActionSheetDialogItemClickListener() {

					@Override
					public void onItemClicked(ActionSheetDialog dialog,
							int postion) {
						String js = SCRIPT_HEADER + "if(" + CALLBACK_RESULT_DATA
								+ "){" + CALLBACK_RESULT_DATA + "('" + postion + "');}";
						onCallback(js);
					}

					@Override
					public void onCanceled(ActionSheetDialog dialog) {
						String js = SCRIPT_HEADER + "if(" + CALLBACK_RESULT_DATA
								+ "){" + CALLBACK_RESULT_DATA + "('-1');}";
						onCallback(js);
					}
				},x,y,width,height,dialogData);
	}

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {
            case MSG_OPEN:
                openMsg(bundle.getStringArray(TAG_PARAM));
                break;
            default:
                super.onHandleMessage(message);
        }
    }
}
