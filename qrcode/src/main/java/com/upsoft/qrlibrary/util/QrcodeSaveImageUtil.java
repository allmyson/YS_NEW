package com.upsoft.qrlibrary.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Copyright (c) 2016,重庆扬讯软件技术有限公司<br>
 * All rights reserved.<br>
 * <p/>
 * 文件名称：FragmentTask<br>
 * 摘要：保存 返回图片<br>
 * -------------------------------------------------------<br>
 * 当前版本：1.1.1<br>
 * 作者：董杰科<br>
 * 完成日期：2017/2/16<br>
 * -------------------------------------------------------<br>
 * 取代版本：1.1.0<br>
 * 原作者：董杰科<br>
 * 完成日期：2017/2/16<br>
 */
public final class QrcodeSaveImageUtil {
	/**
	 * @author 董杰科
	 * @version 1.0
	 * @Description: TODO
	 * @return void
	 * @time： 2016/11/3
	 */
	@TargetApi(19)
	public static String handleImageOnKitKat(Context context, Intent data) {
		String imagePath = "";
		Uri uri = data.getData();
		if (DocumentsContract.isDocumentUri(context, uri)) {
			//如果是document类型的Uri,则通过document id处理
			String docId = DocumentsContract.getDocumentId(uri);
			if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
				String id = docId.split(":")[1];  //解析出数字格式的id
				String selection = MediaStore.Images.Media._ID + "=" + id;
				imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
			} else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
				Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
				imagePath = getImagePath(context, contentUri, null);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			//如果不是document类型的Uri,则使用普通方式处理
			imagePath = getImagePath(context, uri, null);
		}
		return imagePath;
	}

	/**
	 * @author 董杰科
	 * @version 1.0
	 * @Description: 4.4以下系统使用这个方法处理图片
	 * @return void
	 * @time： 2016/11/3
	 */
	public static String handleImageBeforeKitKat(Context context, Intent data) {
		String imagePath = "";
		Uri uri = data.getData();
		imagePath = getImagePath(context, uri, null);
		return imagePath;
	}
	/**
	 * @author 董杰科
	 * @version 1.0
	 * @Description: 通过Uri和selection来获取真实的图片路径
	 * @return void
	 * @time： 2016/11/3
	 */
	public static String getImagePath(Context context, Uri uri, String selection) {
		String path = null;
		Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			}
			cursor.close();
		}
		return path;
	}


}
