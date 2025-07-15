package top.xfunny.mod.keymapping;

import java.util.HashMap;
import java.util.Map;

public class DefaultButtonsKeyMapping {
    final private Map<String, ButtonArea> buttonMap = new HashMap<>();

    public void registerButton(String buttonName, float[] location, float[] dimension) {
        if (!buttonMap.containsKey(buttonName)) {
            buttonMap.put(buttonName, new ButtonArea(location, dimension));
        }
    }

    public void removeButton(String buttonName) {
        buttonMap.remove(buttonName);
    }

    public String mapping(double x, double hitY) {
        for (Map.Entry<String, ButtonArea> entry : buttonMap.entrySet()) {
            ButtonArea area = entry.getValue();
            boolean hit = x < -(area.location[0] - 0.5) && x > -(area.location[0] - 0.5) - area.dimension[0]
                    && hitY > area.location[1] && hitY < area.location[1] + area.dimension[1];
            if (hit) {
                return entry.getKey();
            }
        }
        return "null";
    }

    public int getButtonCount() {
        return buttonMap.size();
    }

    private static class ButtonArea {
        float[] location;
        float[] dimension;

        ButtonArea(float[] location, float[] dimension) {
            this.location = location;
            this.dimension = dimension;
        }

    }

}
