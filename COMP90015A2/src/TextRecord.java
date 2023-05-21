import org.json.JSONObject;

import java.awt.*;

public class TextRecord {
    private int x;
    private int y;
    private int fontSize;
    private String fontType;
    private String text;
    private Font font;

    public TextRecord(int x, int y, int fontSize, String fontType, String text) {
        this.x = x;
        this.y = y;
        this.fontSize = fontSize;
        this.fontType = fontType;
        this.text = text;

        this.font = new Font(fontType, Font.PLAIN, fontSize);
    }

    public TextRecord(int x, int y, int fontSize, String fontType) {
        this.x = x;
        this.y = y;
        this.fontSize = fontSize;
        this.fontType = fontType;

        this.font = new Font(fontType, Font.PLAIN, fontSize);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void draw(Graphics2D g2) {
        g2.setFont(this.font);
        g2.drawString(this.text, x, y);
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", this.x);
        jsonObject.put("y", this.y);
        jsonObject.put("fontSize", this.fontSize);
        jsonObject.put("fontType", this.fontType);
        jsonObject.put("text", this.text);

        return jsonObject;
    }

    public Font parseFont(String fontString) {

        return new Font(this.fontType, Font.PLAIN, this.fontSize);
    }
}
