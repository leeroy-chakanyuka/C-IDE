


import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Fonts{
        private static final Map<String, Font> loadedFonts = new HashMap<>();

        public static Font loadFont(String fontFileName, int style, float size) {

            String resourcePath = "fonts/" + fontFileName;


            String fontKey = fontFileName + "_" + style + "_" + size;
            if (loadedFonts.containsKey(fontKey)) {
                return loadedFonts.get(fontKey);
            }

            try (InputStream is = Fonts.class.getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new IOException("Font file not found in resources: " + resourcePath);
                }
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
                Font derivedFont = baseFont.deriveFont(style, size);

                loadedFonts.put(fontKey, derivedFont);
                return derivedFont;

            } catch (FontFormatException | IOException e) {
                System.err.println("Error loading font '" + fontFileName + "': " + e.getMessage());
                System.err.println("Using fallback MONOSPACED font.");
                Font fallbackFont = new Font(Font.MONOSPACED, style, (int) size);
                loadedFonts.put(fontKey, fallbackFont);
                return fallbackFont;
            }
        }


    }

