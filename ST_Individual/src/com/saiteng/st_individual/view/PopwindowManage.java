package com.saiteng.st_individual.view;

import java.util.ArrayList;
import java.util.List;

import android.widget.PopupWindow;
/**���ڹ���
 * �����ť�򿪴��ڽ��в�����
 * Ӧ�Դ��ڽ��й���
 * */
public class PopwindowManage {
	private boolean bShow = true;
	private static PopwindowManage popwindowManage = null;

	private PopwindowManage() {
	}

	public static PopwindowManage getInstance() {
		if (popwindowManage == null) {
			popwindowManage = new PopwindowManage();
		}
		return popwindowManage;
	}

	private List<PopupWindow> listWindows = new ArrayList<PopupWindow>();

	public void addPopwindow(PopupWindow popupWindow) {
		listWindows.add(popupWindow);
	}

	public void removePopwindow(PopupWindow popupWindow) {
		for (PopupWindow window : listWindows) {
			if (window.equals(popupWindow)) {
				listWindows.remove(window);
			}
		}
	}

	private void dismissPopwindow() {
		for (PopupWindow window : listWindows) {
			listWindows.remove(window);
			window.dismiss();
		}
	}

}
