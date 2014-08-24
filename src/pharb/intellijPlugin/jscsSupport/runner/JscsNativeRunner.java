package pharb.intellijPlugin.jscsSupport.runner;

import pharb.intellijPlugin.jscsSupport.error.ErrorReporter;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JscsNativeRunner {

    /**
     * Milliseconds to wait between checking is process is still running.
     */
    private static final long PROCESS_CHECK_INTERVALL = 100;

    private static final long PROCESS_TIMEOUT_INTERVALL = 5000;

    private final String workingDirectory;

    public JscsNativeRunner(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public synchronized String runJscs(final String rawFileContent) {

        final StringBuilder resultBuilder = new StringBuilder();

        try {
            Thread jscsRunnerThread = new Thread(new JscsRunnable(rawFileContent, resultBuilder));
            monitorRunnerThread(jscsRunnerThread);
        } catch (InterruptedException e) {
            System.out.println("jscs runner got interrupted: " + e.getMessage());
        }

        System.out.println("jscs result: " + resultBuilder);

        return resultBuilder.toString();
    }

    private Process createJscsProcess() throws IOException {
        return new ProcessBuilder("jscs", "-r", "checkstyle", "-v", "-").directory(new File(workingDirectory)).start();
    }

    private void monitorRunnerThread(Thread jscsRunnerThread) throws InterruptedException {
        Long runnerStart = System.nanoTime();
        jscsRunnerThread.start();

        while (jscsRunnerThread.isAlive()) {
            wait(PROCESS_CHECK_INTERVALL);
            if ((System.nanoTime() - runnerStart) / 1_000_000 > PROCESS_TIMEOUT_INTERVALL) {
                System.out.println("interrupting thread after timeout!");
                jscsRunnerThread.interrupt();
            }
            System.out.println(jscsRunnerThread.getState() + " " + System.nanoTime());
        }
    }


    private class JscsRunnable implements Runnable {
        private final String rawFileContent;
        private final StringBuilder resultBuilder;

        public JscsRunnable(String rawFileContent, StringBuilder resultBuilder) {
            this.rawFileContent = rawFileContent;
            this.resultBuilder = resultBuilder;
        }

        @Override
        public void run() {
            try {
                Process createdProcess = createJscsProcess();

                OutputStreamWriter streamWriter = new OutputStreamWriter(createdProcess.getOutputStream(), StandardCharsets.UTF_8);
                BufferedWriter outputWriter = new BufferedWriter(streamWriter);
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(createdProcess.getInputStream()));

                outputWriter.write(rawFileContent);
                outputWriter.close();

                try {
                    createdProcess.waitFor();
                } catch (InterruptedException e) {
                    createdProcess.destroy();
                    ErrorReporter.throwJscsExecutionFailed("jscs process was interrupted, possibly after timeout!", e);
                }

                while (inputReader.ready()) {
                    resultBuilder.append(inputReader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
