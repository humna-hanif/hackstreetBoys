package SpriteFont;

import Engine.GraphicsHandler;
import Engine.ImageLoader;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

// This class represents a sprite font, which is graphic text (text drawn to the screen as if it were an image)
public class SpriteFont {
	protected String text;
	protected Font font;
	protected float x;
	protected float y;
	protected Color color;
	protected Color outlineColor;
	protected float outlineThickness = 1f;
	protected FontRenderContext context;
	
	private  BufferedImage image;
	

	public SpriteFont(String text, float x, float y, String fontName, int fontSize, Color color) {
		this.text = text;
		font = new Font(fontName, Font.PLAIN, fontSize);
		this.x = x;
		this.y = y;
		this.color = color;
	}
     
//	public SpriteFont(BufferedImage bufferedImage, float x, float y) {
//		this.image = bufferedImage;
//		this.x = x;
//		this.y = y;
//	}

	
	
	
	public void setColor(Color color) {
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setFontName(String fontName) {
		this.font = new Font(fontName, this.font.getStyle(), this.font.getSize());
	}

	public void setFontStyle(int fontStyle) {
		this.font = new Font(font.getFontName(), fontStyle, this.font.getSize());
	}

	public void setFontSize(int size) {
		this.font = new Font(font.getFontName(), this.font.getStyle(), size);
	}

	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}
	
	public void setOutlineThickness(float outlineThickness) {
		this.outlineThickness = outlineThickness;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void moveX(float dx) {
		x += dx;
	}

	public void moveY(float dy) {
		y += dy;
	}

	public void moveRight(float dx) {
		x += dx;
	}
	
	public void moveLeft(float dx) {
		x -= dx;
	}
	
	public void moveDown(float dy) {
		y += dy;
	}
	
	public void moveUp(float dy) {
		y -= dy;
	}
	
	Rectangle2D getBounds(String message) {
		return font.getStringBounds(message, context);
	}

	public double getWidth(String message) {

	    Rectangle2D bounds = getBounds(message);
	    return bounds.getWidth();
	  }

	public double getHeight(String message) {

	    Rectangle2D bounds = getBounds(message);
	    return bounds.getHeight();
	  }

	public void draw(GraphicsHandler graphicsHandler) {
		if (outlineColor != null && !outlineColor.equals(color)) {
			context = graphicsHandler.getFontRenderContext();
			graphicsHandler.drawStringWithOutline(text, Math.round(x), Math.round(y), font, color, outlineColor, outlineThickness);
		} else {
			context = graphicsHandler.getFontRenderContext();
			graphicsHandler.drawString(text, Math.round(x), Math.round(y), font, color);
		}
	}

	// this can be called instead of regular draw to have the text drop to the next line in graphics space on a new line character
	public void drawWithParsedNewLines(GraphicsHandler graphicsHandler) {
		int drawLocationY = Math.round(this.y);
		context = graphicsHandler.getFontRenderContext();
		for (String line: text.split("\n")) {
			if (outlineColor != null && !outlineColor.equals(color)) {
				graphicsHandler.drawStringWithOutline(line, Math.round(x), drawLocationY, font, color, outlineColor, outlineThickness);
			} else {
				graphicsHandler.drawString(line, Math.round(x), drawLocationY, font, color);
			}
			drawLocationY += font.getSize();
		}
	}
}
