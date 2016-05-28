package com.android.delta_airlines;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class SwipeView_TicketClass extends PagerAdapter implements ViewPager.PageTransformer {

	private static final float MIN_PAGE_SIZE = 0.85f;
	private static final float MIN_ALPHA = 0.5f;
	
	Context context;
	String selectedItem;
	int[] seatImages = {R.drawable.econ, R.drawable.bus, R.drawable.first};
	
	
	public SwipeView_TicketClass(Context c){
		context = c;
	}
	
	/** Total Images Present **/
	@Override
	public int getCount() {

		return seatImages.length;
	}

	/** Check if view == the ImageViews **/
	@Override
	public boolean isViewFromObject(View view, Object object) {
		
		return view == ((ImageView) object);
	}
	
	
	/** Create ImageView in view to Swipe in Activity **/
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imgView = new ImageView(context);
		
		imgView.setScaleType(ImageView.ScaleType.FIT_XY);
		imgView.setPadding(10, 10, 10, 10);
		imgView.setImageResource(seatImages[position]);
		((ViewPager) container).addView(imgView, 0);
		
		return imgView;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      
		((ViewPager) container).removeView((ImageView) object);
    }

	
	/** Zoom-Out Page SwipeView Transformer**/
	@Override
	public void transformPage(View view, float position) {
		
		int imageWidth = view.getWidth();
		int imageHeight = view.getHeight();
		
		//Page/Image is Off-Screen to the Left; Its Invisible
		if(position < -1){
		   view.setAlpha(0);
		
		//Change the default Slide Transition to Shrink the images
		}else if(position <= 1){
		   float scaleFactor = Math.max(MIN_PAGE_SIZE, 1 - Math.abs(position));
		   float horizontalMargin = imageWidth * (1 - scaleFactor)/2;
		   float verticalMargin = imageHeight * (1 - scaleFactor)/2;
		   
		   if(position < 0)
			  view.setTranslationX(horizontalMargin - (verticalMargin/2));
		   else
			  view.setTranslationX(-horizontalMargin + (verticalMargin/2));
		   
		   //Scale the Page Size down from (1 to MIN_PAGE_SIZE = .85F)
		   view.setScaleX(scaleFactor);
		   view.setScaleY(scaleFactor);
		   
		   //Fade the page/image Relative to its size
		   view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_PAGE_SIZE) / (1 - MIN_PAGE_SIZE) * (1 - MIN_ALPHA));
		   
	    //Page/Image is Off-Screen to the Right; Its Invisible
		}else
		   view.setAlpha(0);	
	}
}

