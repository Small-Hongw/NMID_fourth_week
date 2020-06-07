package ChatWork;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private static final int PORT = 10002;
    private static ExecutorService exec = Executors.newCachedThreadPool();


    public static void main(String[] args) {
        new Client();
    }

    public Client(){
        try{
            //获取本机的IP地址，并连接服务器
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ip =inetAddress.getHostAddress().toString();
            Socket client = new Socket(ip, PORT);
        //进入线程池
            exec.execute(new ClientThread(client));
            System.out.println(" 【"+client.getLocalPort()+"】 欢迎来到女装分享群聊！");

            InputStream is = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String msg;

            while ((msg = br.readLine())!=null) {
            //将消息打印
            System.out.println(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

static class ClientThread implements Runnable {

    private Socket socket;

    public ClientThread(Socket client) {
        this.socket = client;
    }

    public void run() {
        // 发出消息
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            String msg ;
            // 把输入的内容输出到socket
            while (true) {
                //将消息打印出
                msg = br.readLine();
                pw.println(msg);

                //如果发送再见就退出聊天
                if(msg.trim().equals("再见")){
                    pw.close();
                    br.close();
                    exec.shutdown();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
}
