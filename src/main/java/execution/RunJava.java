package execution;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;

public class RunJava {
    private JTextArea outputArea;

    public RunJava(JTextArea outputArea) {
        this.outputArea = outputArea;
    }

    public void runJavaFile(final File sourceFile, final String content) throws IOException, InterruptedException { // Made sourceFile and content final
        final String className = sourceFile.getName().replace(".java", "");
        File tempDirLocal = sourceFile.getParentFile();

        if (tempDirLocal == null || !tempDirLocal.isDirectory()) {
            tempDirLocal = Files.createTempDirectory("chax_java_run_").toFile();
            tempDirLocal.deleteOnExit();
        }

        final File tempDir = tempDirLocal;

        if (sourceFile.getName().startsWith("temp_run_")) {
            try (FileWriter writer = new FileWriter(sourceFile)) {
                writer.write(content);
            }
        }


        outputArea.append("[JAVA] Compiling " + sourceFile.getName() + "...\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());

        new SwingWorker<Integer, String>() {
            @Override
            protected Integer doInBackground() throws Exception {
                ProcessBuilder compilePb = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
                compilePb.directory(tempDir);
                Process compileProcess = compilePb.start();

                readProcessOutput(compileProcess, "[JAVA-COMPILER-ERROR]");
                int compileExitCode = compileProcess.waitFor();

                if (compileExitCode == 0) {
                    publish("[JAVA] Compilation successful.\n");
                    publish("[JAVA] Running " + className + "...\n");

                    ProcessBuilder runPb = new ProcessBuilder("java", "-cp", tempDir.getAbsolutePath(), className);
                    runPb.directory(tempDir);
                    Process runProcess = runPb.start();
                    readProcessOutput(runProcess, "[JAVA-RUNTIME-ERROR]");
                    return runProcess.waitFor();
                } else {
                    return compileExitCode;
                }
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String chunk : chunks) {
                    outputArea.append(chunk);
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                }
            }

            @Override
            protected void done() {
                try {
                    int exitCode = get();
                    if (exitCode == 0) {
                        outputArea.append("[JAVA] Program finished successfully.\n");
                    } else if (exitCode != 0) {
                        outputArea.append("[JAVA] Program exited with code: " + exitCode + " (See errors above).\n");
                    }
                } catch (Exception ex) {
                    outputArea.append("[ERROR] Failed to run Java program: " + ex.getMessage() + "\n");
                } finally {
                    if (sourceFile.getName().startsWith("temp_run_")) {
                        sourceFile.delete();
                        File classFile = new File(tempDir, className + ".class");
                        if (classFile.exists()) {
                            classFile.delete();
                        }
                        if (tempDir.getName().startsWith("chax_java_run_")) {
                            if (tempDir.isDirectory() && tempDir.list().length == 0) {
                                tempDir.delete();
                            }
                        }
                    }
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                }
            }

            private void readProcessOutput(Process process, String errorPrefix) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    publish(line + "\n");
                }

                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    publish(errorPrefix + " " + line + "\n");
                }
            }
        }.execute();
    }
}
