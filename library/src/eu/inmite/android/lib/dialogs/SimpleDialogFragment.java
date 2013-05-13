package eu.inmite.android.lib.dialogs;
/*
 * Copyright (c) 2013. Inmite s.r.o. (www.inmite.eu).
 * All rights reserved. This source code can be used only for purposes specified
 * by the given license contract signed by the rightful deputy of Inmite s.r.o.
 * This source code can be used only by the owner of the license.
 * Any disputes arising in respect of this agreement (license) shall be brought
 * before the Municipal Court of Prague.
 */

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

/**
 * Dialog for displaying simple message, message with title or message with title and two buttons. Implement {@link
 * ISimpleDialogListener} in your Fragment or Activity to rect on positive and negative button clicks. This class can
 * be extended and more parameters can be added in overriden build() method.
 *
 * @author David Vávra (david@inmite.eu)
 */
public class SimpleDialogFragment extends BaseDialogFragment {

	public static String TAG = "simple_dialog";

	private static String ARG_MESSAGE = "message";
	private static String ARG_TITLE = "title";
	private static String ARG_POSITIVE_BUTTON = "positive_button";
	private static String ARG_NEGATIVE_BUTTON = "negative_button";
	private static String ARG_REQUEST_CODE = "request_code";

	protected ISimpleDialogListener mListener;
	protected ISimpleDialogCancelListener mCancelListener;
	protected int mRequestCode;

	/**
	 * Shows dialog with supplied message and Close button.
	 *
	 * @param activity parent Activity
	 * @param message  message to display
	 */
	public static void show(FragmentActivity activity, Fragment fragment, String message) {
		show(activity, fragment, message, null, activity.getString(R.string.dialog_close), null);
	}

	/**
	 * Shows dialog with supplied message and Close button.
	 *
	 * @param activity  parent Activity
	 * @param messageId message to display
	 */
	public static void show(FragmentActivity activity, Fragment fragment, int messageId) {
		show(activity, fragment, activity.getString(messageId), null, activity.getString(R.string.dialog_close), null);
	}

	/**
	 * Shows dialog with supplied message, title and Close button.
	 *
	 * @param activity parent Activity
	 * @param message  message to display
	 * @param title    title to display
	 */
	public static void show(FragmentActivity activity, Fragment fragment, String message, String title) {
		show(activity, fragment, message, title, activity.getString(R.string.dialog_close), null);
	}

	/**
	 * Shows dialog with supplied message, title and Close button.
	 *
	 * @param activity  parent Activity
	 * @param messageId message to display
	 * @param titleId   title to display
	 */
	public static void show(FragmentActivity activity, Fragment fragment, int messageId, int titleId) {
		show(activity, fragment, activity.getString(messageId), activity.getString(titleId),
				activity.getString(R.string.dialog_close), null);
	}


	/**
	 * Shows dialog with supplied message, title and custom
	 *
	 * @param activity parent Activity
	 * @param message  message to display
	 * @param title    title to display
	 */
	public static void show(FragmentActivity activity, Fragment targetFragment, String message, String title, String positiveButtonText,
	                        String negativeButtonText) {
		show(activity, targetFragment, 0, message, title, positiveButtonText, negativeButtonText);
	}

	public static void show(FragmentActivity activity, Fragment targetFragment, int requestCode, String message, String title, String positiveButtonText,
	                        String negativeButtonText) {
		show(activity, targetFragment, requestCode, message, title, positiveButtonText, negativeButtonText, true);
	}

	public static void show(FragmentActivity activity, Fragment targetFragment, int requestCode, String message, String title, String positiveButtonText,
				String negativeButtonText, boolean cancelable) {
		Bundle args = new Bundle();
		args.putString(ARG_MESSAGE, message);
		args.putString(ARG_TITLE, title);
		args.putString(ARG_POSITIVE_BUTTON, positiveButtonText);
		args.putString(ARG_NEGATIVE_BUTTON, negativeButtonText);
		BaseDialogFragment fragment = new SimpleDialogFragment();
		if (targetFragment != null) {
			fragment.setTargetFragment(targetFragment, requestCode);
		} else {
			args.putInt(ARG_REQUEST_CODE, requestCode);
		}
		fragment.setArguments(args);
		fragment.setCancelable(cancelable);
		fragment.show(activity.getSupportFragmentManager(), TAG);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Fragment targetFragment = getTargetFragment();
		if (targetFragment != null) {
			if (targetFragment instanceof ISimpleDialogListener) {
				mListener = (ISimpleDialogListener) targetFragment;
			}
			if (targetFragment instanceof ISimpleDialogCancelListener) {
				mCancelListener = (ISimpleDialogCancelListener) targetFragment;
			}
			mRequestCode = getTargetRequestCode();
		} else {
			if (getActivity() instanceof ISimpleDialogListener) {
				mListener = (ISimpleDialogListener) getActivity();
			}
			if (getActivity() instanceof ISimpleDialogCancelListener) {
				mCancelListener = (ISimpleDialogCancelListener) getActivity();
			}
			Bundle args = getArguments();
			if (args != null) {
				mRequestCode = args.getInt(ARG_REQUEST_CODE, 0);
			}
		}

	}

	/**
	 * Children can extend this to add more things to builder.
	 */
	public Builder build(Builder builder) {
		if (!TextUtils.isEmpty(getTitle())) {
			builder.setTitle(getTitle());
		}
		if (!TextUtils.isEmpty(getMessage())) {
			builder.setMessage(getMessage());
		}
		if (!TextUtils.isEmpty(getPositiveButtonText())) {
			builder.setPositiveButton(getPositiveButtonText(), new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mListener != null) {
						mListener.onPositiveButtonClicked(mRequestCode);
					}
					dismiss();
				}
			});
		}
		if (!TextUtils.isEmpty(getNegativeButtonText())) {
			builder.setNegativeButton(getNegativeButtonText(), new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mListener != null) {
						mListener.onNegativeButtonClicked(mRequestCode);
					}
					dismiss();
				}
			});
		}
		return builder;
	}

	private String getMessage() {
		return getArguments().getString(ARG_MESSAGE);
	}

	private String getTitle() {
		return getArguments().getString(ARG_TITLE);
	}

	private String getPositiveButtonText() {
		return getArguments().getString(ARG_POSITIVE_BUTTON);
	}

	private String getNegativeButtonText() {
		return getArguments().getString(ARG_NEGATIVE_BUTTON);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (mCancelListener != null) {
			mCancelListener.onCancelled(mRequestCode);
		}
	}
}
