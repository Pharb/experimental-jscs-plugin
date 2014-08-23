package pharb.intellijPlugin.jscsSupport.runner;


import java.io.*;

public class JscsNativeRunner {

    public static String runJscs(String rawFileContent, String workingDirectory) {

        String options = "-r checkstyle -v";
        String command = String.format("jscs %s", options);
        String result = "";

        try {
            System.out.println("executing: " + command + " cwd: " + workingDirectory);
            Process createdProcess = Runtime.getRuntime().exec(command, null, new File(workingDirectory));


            BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter((createdProcess.getOutputStream())));
            outputWriter.write(rawFileContent); //TODO make sure it's UTF-8
            outputWriter.close();

            BufferedReader inputReader = new BufferedReader(new InputStreamReader((createdProcess.getInputStream())));
            createdProcess.waitFor();

            while (inputReader.ready()) {
                result += inputReader.readLine();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(result);

        return result;
    }

}
