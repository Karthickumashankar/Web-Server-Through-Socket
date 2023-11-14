import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebServer{
    public static void main(String[] args) throws IOException {
        int port = 6605;
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("Server connecting to the port: " + port);

            while (true) {
                try (Socket s = ss.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                     OutputStream out = s.getOutputStream()) {

                    // http request
                    String req = reader.readLine();
                    System.out.println("Request: " + req);

                    // get method http
                    if (req != null && req.startsWith("GET")) {
                        String filePath = getFilePath(req);

                        if (filePath != null) {
                            File file = new File(filePath);

                            if (file.exists()) {
                                // File found, send 200 OK response with headers
                                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                                if(filePath.endsWith("html")){
                                    out.write("Content-Type: text/html\r\n".getBytes());
                                }
                                else if(filePath.endsWith("jpg") || filePath.endsWith("webp")){
                                    out.write("Content-Type: image/jpeg\r\n".getBytes());
                                }


                                // Read the file content
                                //String fileContent = readHtmlFile(filePath);
                                byte[] data=readBinaryFile(filePath);
                                // Send Content-Length header
                                out.write(("Content-Length: " + data.length + "\r\n\r\n").getBytes());

                                // Send the file content
                                out.write(data);
                                out.flush();
                            } else {
                                // File not found, send 404 Not Found response
                                out.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                            }
                        } else {
                            // Invalid request, send 400 Bad Request response
                            out.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getFilePath(String request) {
        try {
            String[] parts = request.split(" ");
            if (parts.length >= 2) {
                String path = parts[1];
                return "C:\\Users\\karth\\Downloads\\Webserver\\src\\" + path; // add demo.html as path
                // http://localhost:6605/demo.html
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String readHtmlFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String codeLine;
            while ((codeLine = bf.readLine()) != null) {
                sb.append(codeLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    private static byte[] readBinaryFile(String path) {
        try (InputStream inputStream = new FileInputStream(path)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
