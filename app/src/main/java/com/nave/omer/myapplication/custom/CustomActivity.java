package com.nave.omer.myapplication.custom;

import android.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.nave.omer.myapplication.R;
import com.nave.omer.myapplication.utils.TouchEffect;

/**
 * This is a common activity that all other activities of the app can extend to
 * inherit the common behaviors like implementing a common interface that can be
 * used in all child activities.
 */
public class CustomActivity extends FragmentActivity implements OnClickListener
{

	/**
	 * Apply this Constant as touch listener for views to provide alpha touch
	 * effect. The view must have a Non-Transparent background.
	 */
	public static final TouchEffect TOUCH = new TouchEffect();

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
	}

	/**
	 * Sets the touch and click listener for a view with given id.
	 * 
	 * @param id
	 *            the id
	 * @return the view on which listeners applied
	 */
	public View setTouchNClick(int id)
	{

		View v = setClick(id);
		if (v != null)
			v.setOnTouchListener(TOUCH);
		return v;
	}

	/**
	 * Sets the click listener for a view with given id.
	 * 
	 * @param id
	 *            the id
	 * @return the view on which listener is applied
	 */
	public View setClick(int id)
	{

		View v = findViewById(id);
		if (v != null)
			v.setOnClickListener(this);
		return v;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{

	}
}
