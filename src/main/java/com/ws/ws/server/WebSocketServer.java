package com.ws.ws.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author lcb 2019/8/23
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer {
    private Session session;
    private static int onlineCount = 0;
    private String sid="";

    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();


    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws IOException {
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        this.sid = sid;
        sendMessage("连接成功");
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException, InterruptedException {
        log.debug(session.getId() + message);
        for (WebSocketServer item : webSocketSet) {
            Thread.sleep(5000);
            item.sendMessage("主动推送成功");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error(session.getId() + "发生错误");
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message,@PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口"+sid+"，推送内容:"+message);
        for (WebSocketServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                if(sid==null) {
                    item.sendMessage(message);
                }else if(item.sid.equals(sid)){
                    item.sendMessage(message);
                }
            } catch (IOException ignored) {
                log.debug("发送失败" + ignored.getMessage());
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
