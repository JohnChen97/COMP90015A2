import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.json.JSONString;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeRecord {
    private int x;
    private int y;
    private int width;
    private int height;
    private String shape;

    private Color color;
    private int size;

    public ShapeRecord(int x, int y, int width, int height, String shape, Color color, int size) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shape = shape;
        this.color = color;
        this.size = size;
    }

    public  ShapeRecord(String jsonString){
        JSONObject jsonObject = new JSONObject(jsonString);
        this.x = jsonObject.getInt("x");
        this.y = jsonObject.getInt("y");
        this.width = jsonObject.getInt("width");
        this.height = jsonObject.getInt("height");
        this.shape = jsonObject.getString("shape");
        Color color = parseColor(jsonObject.getString("color"));
        this.color = this.parseColor(jsonObject.getString("color"));

        this.size = jsonObject.getInt("size");
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", this.x);
        jsonObject.put("y", this.y);
        jsonObject.put("width", this.width);
        jsonObject.put("height", this.height);
        jsonObject.put("shape", this.shape);
        jsonObject.put("color", this.color);
        jsonObject.put("size", this.size);

        return jsonObject;
    }

    public String serialize() {
        return this.toJsonObject().toString();
    }

    private Color parseColor(String colorStr) {
        // Regex pattern that matches "java.awt.Color[r=0,g=0,b=0]"
        Pattern pattern = Pattern.compile("java\\.awt\\.Color\\[r=(\\d+),g=(\\d+),b=(\\d+)\\]");
        Matcher matcher = pattern.matcher(colorStr);
        if (matcher.find()) {
            int red = Integer.parseInt(matcher.group(1));
            int green = Integer.parseInt(matcher.group(2));
            int blue = Integer.parseInt(matcher.group(3));
            return new Color(red, green, blue);
        } else {
            throw new IllegalArgumentException("Could not parse color from string: " + colorStr);
        }
    }
}
