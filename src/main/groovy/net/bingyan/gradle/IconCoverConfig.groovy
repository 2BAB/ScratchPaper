package net.bingyan.gradle

import java.awt.*

class IconCoverConfig {

    static DEFAULT_CONFIG = new IconCoverConfig()

    int textSize = 12

    String textColor = "#FFFFFFFF"

    int verticalLinePadding = 4

    String backgroundColor = "#CC000000"

    public Color getBackgroundColor() {
        int[] color = hexColorToRGBIntArray(backgroundColor)
        return new Color(color[1], color[2], color[3], color[0])
    }

    public Color getTextColor() {
        int[] color = hexColorToRGBIntArray(textColor)
        return new Color(color[1], color[2], color[3], color[0])
    }


    static int[] hexColorToRGBIntArray(String hexColor) {
        if (!hexColor.startsWith("#")) {
            throw new IllegalArgumentException()
        } else {
            hexColor = hexColor.replace("#", "")
        }
        int[] argbIntArray = new int[4];
        int colorLength = hexColor.length()
        String octetHexColor
        switch (colorLength) {
            case 3:
                octetHexColor = "FF" + hexColor.substring(0, 1) + hexColor.substring(0, 1)
                + hexColor.substring(1, 2) + hexColor.substring(1, 2)
                + hexColor.substring(2, 3) + hexColor.substring(2, 3)
                break

            case 6:
                octetHexColor = "FF" + hexColor
                break

            case 8:
                octetHexColor = hexColor
                break

            default:
                throw new IllegalArgumentException()
                break
        }

        for (int i = 0; i < 4; i++) {
            argbIntArray[i] = Integer.parseInt(
                    octetHexColor.substring(2 * i, 2 * i + 1)
                    + octetHexColor.substring(2 * i + 1, 2 * i + 2)
                    , 16)
        }
        return argbIntArray
    }
}