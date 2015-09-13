package com.example.pixperfect;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class PictureUtils {
	/**
	 * Returns a grayscale Bitmap of the original image. 
	 * @param img - original image to be manipulated
	 * @return newImg - a grayscaled version of img
	 */
	public static Bitmap grayscale(Bitmap img){
		Bitmap newImg = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Config.ARGB_8888);
		
		/*To grayscale an image, simply take the average of the red, green, and blue values at every pixel
		and set the red, green, and blue values at that pixel to the average*/
		int average, color;
		int height = newImg.getHeight(), width = newImg.getWidth();
		for (int r = 0; r < height; r++){
			for (int c = 0; c < width; c++){
				color = img.getPixel(c, r);
				average = (Color.blue(color) + Color.red(color) + Color.green(color))/3;
				newImg.setPixel(c, r, Color.rgb(average, average, average));
			}
		}
		
		return newImg;
	}
	
	/**
	 * Returns a negated Bitmap of the original image
	 * @param img - original image to be manipulated
	 * @return
	 */
	public static Bitmap negate(Bitmap img){
		Bitmap newImg = img.copy(Bitmap.Config.ARGB_8888, true);
		for (int r = 0; r < newImg.getHeight(); r++){
			for (int c = 0; c < newImg.getWidth(); c++){
				int color = newImg.getPixel(c, r);
				int red = 255 - Color.red(color);
				int green = 255 - Color.green(color);
				int blue = 255 - Color.blue(color);
				newImg.setPixel(c, r, Color.rgb(red, green, blue));
			}
		}
		
		return newImg;
	}
	
	public static Bitmap sepia(Bitmap img){
		Bitmap newImg = grayscale(img); //first get a grayscaled version of the image
		
		//then traverse through the image applying the sepia modification to each pixel individually
		for (int x = 0; x < newImg.getWidth(); x++){
			for (int y = 0; y < newImg.getHeight(); y++){
				int color = newImg.getPixel(x, y); //get the color
				float[] hsv = new float[3]; //a holder array for the HSV values 
				Color.colorToHSV(color, hsv); //convert the RGB color to HSV color and put the HSV into the hsv array
				hsv[0] += 30; //increment the Hue by 30
				hsv[1] += .3f; //incrememnt the Saturation by .3...leave Value unchanged
				int newColor = Color.HSVToColor(hsv); //transform the HSV color back to a RGB color
				newImg.setPixel(x, y, newColor);
			}
		}
		
		return newImg;
	}
	
	/**
	 * Returns a Bitmap that contains only the edges of the original image. The returned Bitmap looks somewhat like a hand-drawn image.
	 * @param img - the image to be manipulated
	 * @param colorDiff - the minimum difference in color intensity between two pixels for an edge to be established
	 * @return newImg - the edge-only version of the original image
	 */
	public static Bitmap edgeDetect(Bitmap img, int colorDiff){
		Bitmap newImg = img.copy(Bitmap.Config.ARGB_8888, true);
		//Check horizontally for color difference
		for (int r = 0; r < newImg.getHeight()-1; r++){
			for (int c = 0; c < newImg.getWidth()-1; c++){
				if (getColorDiff(newImg.getPixel(c, r), newImg.getPixel(c+1, r)) > colorDiff){
					newImg.setPixel(c, r, Color.rgb(0, 0, 0));
				}else if (getColorDiff(newImg.getPixel(c, r), newImg.getPixel(c, r+1)) > colorDiff){
					newImg.setPixel(c, r, Color.rgb(0, 0, 0));
				}else{
					newImg.setPixel(c, r, Color.rgb(255, 255, 255));
				}
			}
		}
		
		return newImg;
	}
	
	/**
	 * Returns the "color difference" between two color. It is similar to the distance formula between two points (x,y). The color difference 
	 * is defined to be the square root of the sum of the difference in red, green, and blue values squared
	 * @param color1
	 * @param color2
	 * @return
	 */
	public static int getColorDiff(int color1, int color2){
		//returns the "color difference" between two colors - similar to the distance formula between two points (x,y)
		int red1 = Color.red(color1), green1 = Color.green(color1), blue1 = Color.blue(color1);
		int red2 = Color.red(color2), green2 = Color.green(color2), blue2 = Color.blue(color2);
		
		int deltaRed = red2 - red1;
		int deltaGreen = green2 - green1;
		int deltaBlue = blue2 - blue1;
		
		return (int)Math.sqrt((deltaRed*deltaRed) + (deltaGreen*deltaGreen) + (deltaBlue*deltaBlue));
	}
}
