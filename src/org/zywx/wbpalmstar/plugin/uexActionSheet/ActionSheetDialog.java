package org.zywx.wbpalmstar.plugin.uexActionSheet;

import java.util.ArrayList;
import org.zywx.wbpalmstar.base.ResoureFinder;
import org.zywx.wbpalmstar.base.cache.BytesArrayFactory$BytesArray;
import org.zywx.wbpalmstar.base.cache.ImageLoadTask;
import org.zywx.wbpalmstar.base.cache.ImageLoadTask$ImageLoadTaskCallback;
import org.zywx.wbpalmstar.base.cache.ImageLoaderManager;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActionSheetDialog extends Dialog implements OnClickListener,
		OnItemClickListener {

	private LayoutInflater inflater;
	// private TextView tvTitle;
	private LinearLayout dialog_cancelBtn_parent, window_back;
	private ListView lvBtnList;
	private Button btnCancel;
	private ListAdapter lisAdapter;
	private boolean mCloseByBackKey;
	private ActionSheetDialogItemClickListener listener;
	public static final int MESSAGE_ACTION_CLICKED_POSTION = 100;
	public static final int MESSAGE_ACTION_CLICKED_CANCEL = 101;
	public static final int INDEX_DELETE = 0;
	public static final int ANIM_TIME = 300;
	private ResoureFinder finder;
	private DialogData mDialogData;
	private ImageLoaderManager imgLoadMgr;
	private Context mContext;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_ACTION_CLICKED_POSTION:
				if (listener != null) {
					listener.onItemClicked(ActionSheetDialog.this, msg.arg1);
				}
				break;
			case MESSAGE_ACTION_CLICKED_CANCEL:
				if (listener != null) {
					listener.onCanceled(ActionSheetDialog.this);
				}
				break;
			}
		}
	};

	public ActionSheetDialog(Context context, int x, int y, int width,
			int height, DialogData dialogData) {
		super(context, ResoureFinder.getInstance(context).getStyleId(
				"Style_plugin_uexActionSheet_dialog"));
		this.mDialogData = dialogData;
		this.mContext = context;
		finder = ResoureFinder.getInstance(context);
		inflater = LayoutInflater.from(context);
		imgLoadMgr = ImageLoaderManager.initImageLoaderManager(context);
		init(x, y, width, height);
	}

	private void init(int x, int y, int width, int height) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.windowAnimations = finder
				.getStyleId("Anim_platform_window_actionsheet_dialog");
		if (x == 0 && y == 0) {
			// 对话框与底部对齐，横向填满
			params.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
		} else {
			params.x = x;// 设置x坐标
			params.y = y;// 设置y坐标
		}
		params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		params.dimAmount = 0.5f;
		setContentView(finder
				.getLayoutId("plugin_uexactionsheet_dialog_layout"));
		View view = inflater
				.inflate(finder
						.getLayoutId("plugin_uexactionsheet_dialog_layout"),
						null);

		if (width == 0) {
			setContentView(view);
		} else {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					width, RelativeLayout.LayoutParams.WRAP_CONTENT);
			setContentView(view, lp);
		}
		// tvTitle = (TextView) findViewById(finder.getId("dialog_title"));
		btnCancel = (Button) findViewById(finder.getId("dialog_cancel_button"));
		btnCancel.setOnClickListener(this);
		btnCancel.setTextSize(mDialogData.getTextSize());
		int normal = ImageColorUtils.parseColor(mDialogData.getCancelTextNColor());
		int pressed = ImageColorUtils.parseColor(mDialogData.getCancelTextHColor());
		ColorStateList cancelColors = ImageColorUtils.createColorStateList(normal, pressed, pressed, normal);
		btnCancel.setTextColor(cancelColors);
		lvBtnList = (ListView) findViewById(finder.getId("dialog_button_list"));
		lvBtnList.setOnItemClickListener(this);
		if (mDialogData != null) {
			Bitmap unselImage = ImageColorUtils.getImage(mContext,mDialogData.getCancelBtnUnSelectBgImg());
			Bitmap selImage = ImageColorUtils.getImage(mContext,mDialogData.getCancelBtnSelectBgImg());
			btnCancel.setBackgroundDrawable(ImageColorUtils
				.bgColorDrawableSelector(
						unselImage,selImage));
			window_back = (LinearLayout) findViewById(finder
					.getId("window_back"));
			dialog_cancelBtn_parent = (LinearLayout) findViewById(finder
                    .getId("dialog_cancelBtn_parent"));
			LinearLayout frameLayout = (LinearLayout) findViewById(finder
					.getId("frameLayout"));
			if (!TextUtils.isEmpty(mDialogData.getFrameBgColor())
					&& "Y".equals(mDialogData.getIsAngle())) {
				int frameBgColor = ImageColorUtils.parseColor(mDialogData
						.getFrameBgColor());
				GradientDrawable gradientFrameBound = new GradientDrawable();
				gradientFrameBound.setCornerRadius(10);
				gradientFrameBound.setColor(frameBgColor);
				window_back.setBackgroundDrawable(gradientFrameBound);
			} else if (!TextUtils.isEmpty(mDialogData.getFrameBgColor())
					&& !"Y".equals(mDialogData.getIsAngle())) {
				window_back.setBackgroundColor(ImageColorUtils
						.parseColor(mDialogData.getFrameBgColor()));
			} else if ("Y".equals(mDialogData.getIsAngle())
					&& TextUtils.isEmpty(mDialogData.getFramBgImg())) {
				GradientDrawable gradientFrameBound = new GradientDrawable();
				gradientFrameBound.setCornerRadius(10);
				gradientFrameBound
						.setColor(EUExUtil
								.getResDrawableID("plugin_uexactionsheet_menu_bg_color_style"));
				window_back.setBackgroundDrawable(gradientFrameBound);
			} else if (!TextUtils.isEmpty(mDialogData.getFramBgImg())) {
				Bitmap frameBitmap = imgLoadMgr.getCacheBitmap(mDialogData
						.getFramBgImg());
				if (frameBitmap == null) {
					ActionSheetImageTask actionSheetImageTask = new ActionSheetImageTask(
							mContext, mDialogData.getFramBgImg(),
							mDialogData.getIsAngle());
					actionSheetImageTask
							.addCallback(new ImageLoadTask$ImageLoadTaskCallback() {

								@Override
								public void onImageLoaded(ImageLoadTask arg0,
										Bitmap bitmap) {
									if (bitmap != null) {
										BitmapDrawable bd = new BitmapDrawable(
												bitmap);
										window_back.setBackgroundDrawable(bd);
									}
								}

							});
					imgLoadMgr.asyncLoad(actionSheetImageTask);
				} else {
					BitmapDrawable bd = new BitmapDrawable(frameBitmap);
					window_back.setBackgroundDrawable(bd);
				}
			}
			if (!TextUtils.isEmpty(mDialogData.getFrameBroundColor())
					&& "Y".equals(mDialogData.getIsAngle())) {
				GradientDrawable gradientFrameBound = new GradientDrawable();
				gradientFrameBound.setCornerRadius(10);
				gradientFrameBound.setColor(ImageColorUtils
						.parseColor(mDialogData.getFrameBroundColor()));
				frameLayout.setBackgroundDrawable(gradientFrameBound);
			} else if (!TextUtils.isEmpty(mDialogData.getFrameBroundColor())
					&& !"Y".equals(mDialogData.getIsAngle())) {
				frameLayout.setBackgroundColor(ImageColorUtils
						.parseColor(mDialogData.getFrameBroundColor()));
			} else if ("Y".equals(mDialogData.getIsAngle())) {
				GradientDrawable gradientFrameBound = new GradientDrawable();
				gradientFrameBound.setCornerRadius(10);
				gradientFrameBound.setColor(ImageColorUtils
						.parseColor("#DFE0E1"));
				frameLayout.setBackgroundDrawable(gradientFrameBound);
			}
		}
		setCanceledOnTouchOutside(true);
	}

	@Override
	protected void onStop() {
		if (mCloseByBackKey) {
			handler.sendEmptyMessageDelayed(MESSAGE_ACTION_CLICKED_CANCEL,
					ANIM_TIME);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mCloseByBackKey = true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void setupData(String[] labels) {
		if (labels == null) {
			return;
		}
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0, size = labels.length; i < size; i++) {
			list.add(labels[i]);
		}
		lisAdapter = new ListAdapter(list);
		lvBtnList.setAdapter(lisAdapter);
	}

	@Override
	public void setTitle(CharSequence title) {
		// tvTitle.setText(title);
	}

	public void setButtonLabel(String label) {
		btnCancel.setText(label);
	}

	@Override
	public void show() {
		super.show();
	}

	public void setOnDialogItemClickedListener(
			ActionSheetDialogItemClickListener cb) {
		this.listener = cb;
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		handler.sendEmptyMessageDelayed(MESSAGE_ACTION_CLICKED_CANCEL,
				ANIM_TIME);
	}

	private void sendPostionMessage(int postion) {
		Message message = handler.obtainMessage(MESSAGE_ACTION_CLICKED_POSTION);
		message.arg1 = postion;
		handler.sendMessageDelayed(message, ANIM_TIME);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.dismiss();
		sendPostionMessage(position);
	}

	private class ListAdapter extends BaseAdapter {

		private ArrayList<String> list;
		private Bitmap btnUnSelBitmap;
		private Bitmap btnSelBitmap;
		private int textNColor, textHColor;
		public ListAdapter(ArrayList<String> texts) {
			list = texts;
			btnUnSelBitmap = ImageColorUtils.getImage(mContext,mDialogData.getBtnUNSelectBgImg());
			btnSelBitmap = ImageColorUtils.getImage(mContext,mDialogData.getBtnSelectBgImg());
			textNColor = ImageColorUtils.parseColor(mDialogData.getTextNColor());
			textHColor = ImageColorUtils.parseColor(mDialogData.getTextHColor());
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			RelativeLayout layout = (RelativeLayout) inflater
					.inflate(finder
							.getLayoutId("plugin_uexactionsheet_list_item"),
							null);
			final TextView button = (TextView) layout.findViewById(finder
					.getId("actionsheet_list_menu_item_btn"));
			button.setText(list.get(position));
			button.setTextSize(mDialogData.getTextSize());
			ColorStateList colors = ImageColorUtils.createColorStateList(textNColor, textHColor, textHColor, textNColor);
			button.setTextColor(colors);
			button.setBackgroundDrawable(ImageColorUtils
					.bgColorDrawableSelector(
							btnUnSelBitmap,btnSelBitmap));
			return layout;
		}

	}

	public static void show(Context context, String[] lables, String title,
			String cancelLabel, ActionSheetDialogItemClickListener listener,
			int x, int y, int width, int height, DialogData dialogData) {
		ActionSheetDialog actionSheetDialog = new ActionSheetDialog(context, x,
				y, width, height, dialogData);
		actionSheetDialog.setTitle(title);
		actionSheetDialog.setButtonLabel(cancelLabel);
		actionSheetDialog.setupData(lables);
		actionSheetDialog.setOnDialogItemClickedListener(listener);
		actionSheetDialog.show();
	}

	public static interface ActionSheetDialogItemClickListener {
		void onItemClicked(ActionSheetDialog dialog, int postion);

		void onCanceled(ActionSheetDialog dialog);
	}

	private class ActionSheetImageTask extends ImageLoadTask {

		private String mImageUrl;
		private String mIsAngle;

		public ActionSheetImageTask(Context context, String imgUrl,
				String isAngle) {
			super(imgUrl);
			this.mImageUrl = imgUrl;
			this.mIsAngle = isAngle;
		}

		@Override
		protected Bitmap doInBackground() {
			Bitmap myoriginalImage = ImageColorUtils.getImage(mContext,
					mImageUrl);
			if ("Y".equals(mIsAngle)) {
				myoriginalImage = ImageColorUtils
						.getRoundedBitmap(myoriginalImage);
			}
			return myoriginalImage;
		}

		@Override
		protected BytesArrayFactory$BytesArray transBitmapToBytesArray(
				Bitmap arg0) {
			return null;
		}

	}
	
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        int lstHeight = lvBtnList.getHeight();
        lvBtnList.getLayoutParams().height = lstHeight;
        int cancelBtnHeight = dialog_cancelBtn_parent.getHeight();
        int bgHeight = lstHeight + cancelBtnHeight;
        window_back.getLayoutParams().height = bgHeight;
        super.onWindowFocusChanged(hasFocus);
    }

}
