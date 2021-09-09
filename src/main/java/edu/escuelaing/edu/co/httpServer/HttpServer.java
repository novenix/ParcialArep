package edu.escuelaing.edu.co.httpServer;

import com.sun.xml.internal.ws.api.ha.StickyFeature;

import java.net.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

//solo pued haber una instancia de http server con singleton para que no haya mas instancias
public class HttpServer {
    private static HttpServer _instance = new HttpServer();
    private static String selectedFunction = "cos";
    private static boolean num1;
    private HttpServer(){

    }
    private static HttpServer getInstance(){
        return _instance;
    }
    public static void main(String... args) throws IOException{
        HttpServer.getInstance().startServer(args);
    }

    public  void startServer(String[] args) throws IOException {
        int port =8080;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+port);
            System.exit(1);
        }
        Socket clientSocket = null;
        boolean running = true;
        while (running){
            try {
                System.out.println("Listo para recibir en puerto ..."+port);
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            processRequest(clientSocket);
        }



        serverSocket.close();
    }
    private static Double valuePi (String num){
        Double pi= 1.0;
        String[] listnum;
        String number = num;
        Double ans=0.0;
        if(num.contains("π")) {
            if(num.length()>1){
                listnum=num.trim().split("π");
                number = listnum[0];
                pi=Math.PI;
                ans=Double.valueOf(number)*pi;
            }
            else{
                ans=Math.PI;
            }

        }
        return ans;
    }
    private static Double calculatesinoscon (String num1,String num2){
        System.out.println(num1+" "+num2);
        Double pi1= valuePi(num1);
        Double pi2= valuePi(num2);
        Double answer=0.0;
        if(selectedFunction.equals("sin")){
            answer=Math.sin(pi1/pi2);
        }
        else if(selectedFunction.equals("cos")){
            answer=Math.cos(pi1/pi2);
        }
        else if(selectedFunction.equals("tan")){
            answer=Math.tan(pi1/pi2);
        }
        return answer;
    }
    private static Double calculatesinoscon (String num){
        Double pi= valuePi(num);
        Double answer=0.0;
        if(selectedFunction.equals("sin")){
            answer=Math.sin(pi);
        }
        else if(selectedFunction.equals("cos")){
            answer=Math.cos(pi);
        }
        else if(selectedFunction.equals("tan")){
            System.out.println(pi);
            System.out.println(Math.tan(pi));
            answer=Math.tan(pi);
        }
        return answer;
    }
    public  void processRequest(Socket clientSocket) throws IOException{
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine="";
        String method="";
        String path = "";
        String version = "";
        List<String> headers = new ArrayList<String>();
        while ((inputLine = in.readLine()) != null) {
            if(method.isEmpty()){
                String[] requestStrings = inputLine.split(" ");
                method = requestStrings[0];
                path = requestStrings[1];

                version = requestStrings[2];
                System.out.println("reques: "+method +" "+ path + " "+ version);
                System.out.println(path);

            } else{

                System.out.println("path"+path);
                System.out.println(inputLine+"asdasdas");
                //URL url = new URL(inputLine);
                //System.out.println(url.getQuery());


                System.out.println("header: "+inputLine);
                //System.out.println("outpusadaskhdbaskdbhkasbdiasbdkjbaskdbaksdbaskdb: "+inputLine);
                headers.add(inputLine);
            }
            System.out.println("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }

        System.out.println(outputLine);
        String responseMessage = createResponse(path);
        out.println(responseMessage);

        out.close();

        in.close();

        clientSocket.close();
    }
    public String createResponse(String path){
        String type = "text/html";
        System.out.println(path+"path");
       /* if(path.contains("/calculator.html") && path.contains("number")&path.contains("op")){

            Double Answer = 0.0;
            outputLine="";
            ;switch (inputLine){
                case "fun:sin":
                    selectedFunction = "sin";
                    break;
                case "fun:cos":
                    selectedFunction = "cos";
                    break;
                case "fun:tan":
                    selectedFunction = "tan";
                    break;
                default:
                    int pi = -1;
                    int div = -1;
                    String[] values = new String[0];
                    if(inputLine.contains("/")){
                        values= inputLine.trim().split("/");
                        Answer = calculatesinoscon(values[0],values[1]);

                    }
                    else{
                        Answer = calculatesinoscon(inputLine.trim());
                    }



                    outputLine = "Respuesta "+inputLine +" :" + Answer;


            }
        }*/
        //para leer archivos
        try {
            path=path.substring(0,path.indexOf("?"));
        }
        catch (Exception e){
            path=path;
        }

        Path file = Paths.get("./www"+path);
        Charset charset = Charset.forName("UTF-8");
        String outmsg ="";
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                outmsg = outmsg + line;
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: "+type+"\r\n"
                + "\r\n"+ outmsg;
    }
}
