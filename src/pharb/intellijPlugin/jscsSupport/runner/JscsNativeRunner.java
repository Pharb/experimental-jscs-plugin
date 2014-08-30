package pharb.intellijPlugin.jscsSupport.runner;

import pharb.intellijPlugin.jscsSupport.dialog.JscsDialog;
import pharb.intellijPlugin.jscsSupport.error.ErrorReporter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class JscsNativeRunner {

    /**
     * Timeout in milliseconds after which the process is considered to not respond anymore.
     */
    private static final long PROCESS_TIMEOUT_INTERVALL = 10_000;

    /**
     * Only allow a single jscs process to run at a time.
     */
    private static final AtomicBoolean jscsProcessIsRunning = new AtomicBoolean(false);

    private final AtomicBoolean interruptDialogShown = new AtomicBoolean(false);

    private final AtomicBoolean jscsCheckFinished = new AtomicBoolean(false);

    private final String fileName;
    private final String workingDirectory;

    public JscsNativeRunner(String fileName, String workingDirectory) {
        this.fileName = fileName;
        this.workingDirectory = workingDirectory;
    }

    public String runJscs(final String rawFileContent) {

        final StringBuilder resultBuilder = new StringBuilder();

        if (!jscsProcessIsRunning.getAndSet(true)) {
            try {
                Thread jscsRunnerThread = new Thread(new JscsRunnable(rawFileContent, resultBuilder), "jscsRunner");
                startAndWaitForRunnerThread(jscsRunnerThread);
            } catch (InterruptedException e) {
                System.out.println("Monitor runner thread got interrupted: " + e.getMessage());
            } finally {
                jscsProcessIsRunning.set(false);
            }
            System.out.println("jscs result: " + resultBuilder);
        }
        return resultBuilder.toString();
    }

    private void startAndWaitForRunnerThread(final Thread jscsRunnerThread) throws InterruptedException {
        synchronized (jscsCheckFinished) {
            jscsRunnerThread.start();
            while (jscsRunnerThread.isAlive()) {
                jscsCheckFinished.wait(PROCESS_TIMEOUT_INTERVALL);
                System.out.println("No longer wait on jscsCheckFinished!");

                if (jscsCheckFinished.get()) {
                    return;
                } else {
                    System.out.println("PROCESS_TIMEOUT_INTERVALL reached!");

                    askForInterrupt(new Runnable() {
                        @Override
                        public void run() {
                            jscsRunnerThread.interrupt();
                            interruptDialogShown.set(false);
                        }
                    });
                }
            }
        }
    }

    private void askForInterrupt(Runnable interruptRunnable) {
        Runnable cancelRunnable = new Runnable() {
            @Override
            public void run() {
                interruptDialogShown.set(false);
            }
        };

        if (!interruptDialogShown.getAndSet(true)) {
            JscsDialog.showAskForInterruptDialog(fileName, interruptRunnable, cancelRunnable);
        }
    }

    private Process startJscsProcess() throws IOException {
        return new ProcessBuilder("jscs", "-r", "checkstyle", "-v", "-").directory(new File(workingDirectory)).start();
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
                Process createdProcess = startJscsProcess();

                OutputStreamWriter streamWriter = new OutputStreamWriter(createdProcess.getOutputStream(), StandardCharsets.UTF_8);
                BufferedWriter outputWriter = new BufferedWriter(streamWriter);

                InputStreamReader streamReader = new InputStreamReader(createdProcess.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader inputReader = new BufferedReader(streamReader);

                outputWriter.write(rawFileContent);
                outputWriter.close();

                try {
                    createdProcess.waitFor();

                    while (inputReader.ready()) {
                        resultBuilder.append(inputReader.readLine());
                    }

                    jscsCheckFinished.set(true);

                } catch (InterruptedException e) {
                    createdProcess.destroy();
                    System.out.println("Jscs thread got interrupted.");
                }
            } catch (IOException e) {
                ErrorReporter.throwJscsExecutionFailed("Jscs process execution failed!", e);
            } finally {
                synchronized (jscsCheckFinished) {
                    jscsCheckFinished.notify();
                }
            }
        }
    }
}
