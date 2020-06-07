package ChatWork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleServer {
    //记录端口号
    private static final int PORT = 10002;
    //用于保存当前连接的用户
    private static List<Socket> socketList=new ArrayList<Socket>();
    //线程池
    private ExecutorService exec;
    //服务器的socket监视
    private ServerSocket serverSocket;

    public static void main(String[] args) {
         new SimpleServer();
    }

    public SimpleServer(){
        //创建一个serverSocket，用于监听客户端Socket的连接请求
        try {
            serverSocket = new ServerSocket(PORT);//10002为此服务器的端口号
            exec = Executors.newCachedThreadPool();
            System.out.println("服务器启动咯");

            //采用循环不断接收来自客户端的请求
            while (true) {
                //每当接收到客户端的请求，服务器端就对应产生一个Socket
                Socket client = serverSocket.accept();
                System.out.println("服务连接");
                // 把连接的客户端加入到list列表中
                socketList.add(client);
                //启动一个新的线程
                //任务线程,每个连接用都有一个属于自己专用的任务线程
                //这个线程内会处理信息的发送和响应
                exec.execute(new ChatThread(client));
            }
        }catch(IOException e){
            e.printStackTrace();
    }
}

 static class ChatThread implements Runnable {

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private String msg;

    public ChatThread(Socket client)throws IOException {
        this.socket = client;
        br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        msg ="【"+this.socket.getPort()+"】 进入了群聊！  当前有["+socketList.size()+"]人正在分享女装经验";

        sendMessageAll();
    }

    //把接收到的消息发送给各客户端
    public void run() {
        try {
            //接收客户端的消息
            while((msg =br.readLine())!=null){
                //如果客户端发送了再见就退出聊天程序并告诉其他人
                if(msg.trim().equals("再见")) {
                    socketList.remove(socket);
                    //br.close();
                    //pw.close();
                    msg = "【"+socket.getPort()+"】 离开群聊！  当前有["+socketList.size()+"]人正在分享女装经验";
                    sendMessageAll();
                }
                //否则就将客户端的消息打印出来并打给所有人
                else {
                    msg =" 【"+socket.getPort() +"】  说:"+msg;
                    sendMessageAll();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将消息发送给所有人
    private void sendMessageAll()throws IOException{
        System.out.println(msg);
        for(Socket client :socketList){
            pw =new PrintWriter(client.getOutputStream(),true);
            pw.println();
        }
    }
    }
}



