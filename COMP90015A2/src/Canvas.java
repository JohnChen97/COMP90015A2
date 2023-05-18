import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

public class Canvas {
    private JSONObject canvasJSONObject;
    private String canvasName;

    public void createNewCanvasJsonObject(){
        this.canvasJSONObject = new JSONObject();
    }

    public void putShapeRecord(ShapeRecord shapeRecord){
        this.canvasJSONObject.put("shapeRecord", shapeRecord);
    }
    public void createNewCanvas(String canvasName){
        try{
            FileWriter file = new FileWriter(canvasName + ".json");
            file.write(this.canvasJSONObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public JSONObject getCanvasJSONObject() {
        return canvasJSONObject;
    }

    public void setCanvasJSONObject(JSONObject canvasJSONObject) {
        this.canvasJSONObject = canvasJSONObject;
    }

    public String getCanvasName() {
        return canvasName;
    }

    public void setCanvasName(String canvasName) {
        this.canvasName = canvasName;
    }
}
