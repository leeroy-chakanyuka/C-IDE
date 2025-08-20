package utils;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class handleFiles {
    public String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public boolean isTextFile(String fileName) {
        String[] textExtensions = {
                ".txt", ".java", ".js", ".html", ".css", ".xml", ".json",
                ".md", ".py", ".cpp", ".c", ".h", ".cs", ".php", ".sql",
                ".properties", ".yml", ".yaml", ".log", ".bat", ".sh", ".mjs"
        };

        String lowerName = fileName.toLowerCase();
        for (String ext : textExtensions) {
            if (lowerName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static String detectSyntaxStyle(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".java")) return SyntaxConstants.SYNTAX_STYLE_JAVA;
        else if (lowerName.endsWith(".js") || lowerName.endsWith(".mjs")) return SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
        else if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) return SyntaxConstants.SYNTAX_STYLE_HTML;
        else if (lowerName.endsWith(".css")) return SyntaxConstants.SYNTAX_STYLE_CSS;
        else if (lowerName.endsWith(".xml") || lowerName.endsWith(".xhtml")) return SyntaxConstants.SYNTAX_STYLE_XML;
        else if (lowerName.endsWith(".json")) return SyntaxConstants.SYNTAX_STYLE_JSON;
        else if (lowerName.endsWith(".py")) return SyntaxConstants.SYNTAX_STYLE_PYTHON;
        else if (lowerName.endsWith(".c") || lowerName.endsWith(".h")) return SyntaxConstants.SYNTAX_STYLE_C;
        else if (lowerName.endsWith(".cpp") || lowerName.endsWith(".cxx") || lowerName.endsWith(".hpp") || lowerName.endsWith(".cc")) return SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
        else if (lowerName.endsWith(".cs")) return SyntaxConstants.SYNTAX_STYLE_CSHARP;
        else if (lowerName.endsWith(".php")) return SyntaxConstants.SYNTAX_STYLE_PHP;
        else if (lowerName.endsWith(".sql")) return SyntaxConstants.SYNTAX_STYLE_SQL;
        else if (lowerName.endsWith(".bat") || lowerName.endsWith(".cmd")) return SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH;
        else if (lowerName.endsWith(".sh") || lowerName.endsWith(".bash")) return SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
        else if (lowerName.endsWith(".properties")) return SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE;
        else if (lowerName.endsWith(".yml") || lowerName.endsWith(".yaml")) return SyntaxConstants.SYNTAX_STYLE_YAML;
        else if (lowerName.endsWith(".md") || lowerName.endsWith(".markdown")) return SyntaxConstants.SYNTAX_STYLE_MARKDOWN;
        return SyntaxConstants.SYNTAX_STYLE_NONE;
    }

    public static String getSyntaxStyleName(String syntaxStyle) {
        switch (syntaxStyle) {
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JAVA:
                return "Java";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT:
                return "JavaScript";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_HTML:
                return "HTML";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_CSS:
                return "CSS";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_XML:
                return "XML";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_JSON:
                return "JSON";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_PYTHON:
                return "Python";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_C:
                return "C";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS:
                return "C++";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_CSHARP:
                return "C#";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_PHP:
                return "PHP";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_SQL:
                return "SQL";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_MARKDOWN:
                return "Markdown";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_YAML:
                return "YAML";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH:
                return "Batch";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL:
                return "Shell";
            case org.fife.ui.rsyntaxtextarea.SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE:
                return "Properties";
            default:
                return "Plain Text";
        }
    }




}
