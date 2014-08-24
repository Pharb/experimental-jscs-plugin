package pharb.intellijPlugin.jscsSupport.runner;

import pharb.intellijPlugin.jscsSupport.dialog.JscsDialog;
import pharb.intellijPlugin.jscsSupport.error.ErrorReporter;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JscsNativeRunner {

    /**
     * Milliseconds to wait between checking if process is still running.
     */
    private static final long PROCESS_CHECK_INTERVALL = 100;

    /**
     * Timeout in milliseconds after which the process is considered to not respond anymore.
     */
    private static final long PROCESS_TIMEOUT_INTERVALL = 10_000;

    private final String workingDirectory;

    private boolean interruptDialogShown = false;

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

    private void monitorRunnerThread(final Thread jscsRunnerThread) throws InterruptedException {
        Long runnerTimeoutStart = System.nanoTime();
        jscsRunnerThread.start();

        while (jscsRunnerThread.isAlive()) {
            wait(PROCESS_CHECK_INTERVALL);
            if ((System.nanoTime() - runnerTimeoutStart) / 1_000_000 > PROCESS_TIMEOUT_INTERVALL) {
                System.out.println("timeout, ask for interrupt!");
                runnerTimeoutStart = System.nanoTime();

                askForInterrupt(new Runnable() {
                    @Override
                    public void run() {
                        setInterruptDialogShown(false);
                        jscsRunnerThread.interrupt();
                    }
                });
            }
            System.out.println(jscsRunnerThread.getState() + " " + System.nanoTime());
        }
    }

    private void askForInterrupt(Runnable interruptRunnable) {
        Runnable cancelRunnable = new Runnable() {
            @Override
            public void run() {
                setInterruptDialogShown(false);
            }
        };

        if (!interruptDialogShown) {
            setInterruptDialogShown(true);
            JscsDialog.showAskForInterruptDialog(interruptRunnable, cancelRunnable);
        }
    }

    private void setInterruptDialogShown(boolean interruptDialogShown) {
        this.interruptDialogShown = interruptDialogShown;
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
                    System.out.println("Jscs thread got interrupted.");
                }

                while (inputReader.ready()) {
                    resultBuilder.append(inputReader.readLine());
                }
            } catch (IOException e) {
                ErrorReporter.throwJscsExecutionFailed("Jscs process execution failed!", e);
            }
        }
    }
}
