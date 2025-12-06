package com.mathpar.parallel.webCluster.engine;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author r1d1
 *
 *<br><br>
 * Этот класс служит для взаимодействия с серверной программой (экземпляром класса
 * Server, запущенным на управляющем узле кластера).<br>
 * Никаких других способов взаимодейтсвия не предусматривается. Запросы к серверной
 * программе могут поступать из двух точек: web сервера mathpar и от любого из узлов кластера.
 */

 
public class QueryCreator {
    Socket s;
    ObjectOutputStream oos=null;
    ObjectInputStream ois=null;
    
    private String currentUserName;
    private boolean isUserCheckedBySSH;        
    private String currentUserPassword;

    public QueryCreator(String uName, String uPswd) {
        currentUserName=uName;
        currentUserPassword=uPswd;
        isUserCheckedBySSH=false;
    }

    /**
     * Метод устанавливает соединение с серверной программой, запущенной
     * на управляющем узле кластера.
     * @return true, если удалось установить сокетное соединение с серверной программой,
     * false в противном случае.
     */
    
    
    private boolean echo(){
        try {
            oos.writeObject(AlgorithmsConfig.ECHO_CONST);
            Integer tmp=(Integer)ois.readObject();
            if (tmp!=AlgorithmsConfig.ECHO_CONST){
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
    
    
    public int initConnectionFromWebPartToCluster(){
        if (s==null || s.isClosed() || !echo()){
            try {                
                s = new Socket(AlgorithmsConfig.CNF_SERV_HOST, AlgorithmsConfig.CNF_SERV_PORT);                
                OutputStream oStream = s.getOutputStream();
                InputStream iStream = s.getInputStream();
                oos = new ObjectOutputStream(oStream);
                ois = new ObjectInputStream(iStream);
            } catch (Exception e) {
                return AlgorithmsConfig.RES_CONNECT_ERROR;
            };
            if (!isUserCheckedBySSH && AlgorithmsConfig.IS_NEED_SSH_CHEKING) {
                String sshhost = AlgorithmsConfig.CNF_SERV_HOST;                
                int sshPort = 22;
                List<String> results = new ArrayList<String>();
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");

                JSch jsch = new JSch();

                try {
                    Session session = jsch.getSession(currentUserName, sshhost, sshPort);                    
                    session.setPassword(currentUserPassword);
                    session.setConfig(config);
                    session.setTimeout(20000);                    
                    session.connect();
                    if (!session.isConnected()) {
                        return AlgorithmsConfig.RES_SSH_CHECKING_ERROR;
                    }
                    else{
                        isUserCheckedBySSH=true;
                        session.disconnect();
                    }
                } catch (Exception e){
                    return AlgorithmsConfig.RES_SSH_CHECKING_ERROR;
                };
            }
        }
        return AlgorithmsConfig.RES_SUCCESS;
    }
    
    public int initConnectionFromNodeToCluster(){
        if (s==null || s.isClosed() || !echo()){
            try {                
                s = new Socket(AlgorithmsConfig.CNF_SERV_HOST_FOR_NODE, AlgorithmsConfig.CNF_SERV_PORT);                
                OutputStream oStream = s.getOutputStream();
                InputStream iStream = s.getInputStream();
                oos = new ObjectOutputStream(oStream);
                ois = new ObjectInputStream(iStream);
            } catch (Exception e) {
                return AlgorithmsConfig.RES_CONNECT_ERROR;
            };           
        }
        return AlgorithmsConfig.RES_SUCCESS;
    }

    
    /**
     * Этот метод прочитывает заданный текстовый файл, хранящийся на управляющем
     * узле кластера и возвращает его содержимое по сокетному соединению.
     * Содержимое файла записывается в поле QueryResult.data в виде объекта String.
     *
     * @param userID ID пользователя
     * @param taskID ID задачи
     * @param fileName путь к файлу, который необходимо прочесть
     * @return содержимое заданного текстового файла
     */
    public QueryResult getFileContent(Integer userID,Integer taskID,String fileName){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromWebPartToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }        
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_GET_FILE_CONTENT));
            oos.writeObject(userID);
            oos.writeObject(taskID);
            oos.writeObject(fileName);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            res.resultState=(Integer)ois.readObject();
            if (res.resultState==AlgorithmsConfig.RES_SUCCESS){
                res.data=new Object[]{ois.readObject()};
            }
        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
            return res;
        }
        return res;
    }
    /**
     * Этот метод создает список всех имеющихся задач заданного пользователя.
     * Результат - String, записанный в QueryResult.data
     * @param userID ID пользователя
     * @return список задач в виде строки
     */
    public QueryResult getStatesList(Integer userID){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromWebPartToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_GET_STATE_LIST));
            oos.writeObject(userID);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            res.resultState=AlgorithmsConfig.RES_SUCCESS;
            res.data=(Object[])ois.readObject();
        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
            return res;
        }
        return res;
    }

    public QueryResult getFileList(Integer userID){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromWebPartToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_GET_FILE_LIST));
            oos.writeObject(userID);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            res.resultState=AlgorithmsConfig.RES_SUCCESS;
            res.data=(Object[])ois.readObject();
        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
            return res;
        }
        return res;
    }
    /**
     * Метод сохраняет заданный файл в папку пользователя.
     * @param userID ID пользователя
     * @param fileName имя файла, под которым требуется его сохранить
     * @param data содержимое файла
     * @return данные записываются только в QueryResult.resultState - успешное\
     * неудачное сохранение
     */
    public QueryResult uploadFileToCluster(Integer userID, String fileName, Object data){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromWebPartToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_UPLOAD_FILE_TO_CLUSTER));
            oos.writeObject(userID);
            oos.writeObject(fileName);
            oos.writeObject(data);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            res.resultState=(Integer)ois.readObject();
        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
            return res;
        }
        return res;
    }
    
    public QueryResult uploadFileToClusterAndCompile(Integer userID, String fileName, Object data){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromWebPartToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_UPLOAD_FILE_TO_CLUSTER_AND_COMPILE));
            oos.writeObject(userID);
            oos.writeObject(fileName);
            oos.writeObject(data);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            Integer lCode=(Integer)ois.readObject();
            Integer tNumb=(Integer)ois.readObject();
            res.data=new Object[1];
            res.resultState=lCode;
            res.data[0]=tNumb;
        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
            return res;
        }
        return res;
    }

    /**
     * Метод для добавления задач любого типа.
     * @param userID ID пользователя
     * @param data пути до нужных файлов и прочие аргументы
     * @param conf настройки запуска задачи
     * @return QueryResult.resultState - сообщение о запуске. В случае успешного запуска
     * в QueryResult.data[0] будет записан int - номер (ID), присвоенный запущенной задаче.
     */
    public QueryResult addTask(Integer userID, Object []data,TaskConfig conf){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromWebPartToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_ADD_TASK));
            oos.writeObject(userID);
            oos.writeObject(data);
            oos.writeObject(conf);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            Integer lCode=(Integer)ois.readObject();
            Integer tNumb=(Integer)ois.readObject();
            res.data=new Object[1];
            res.resultState=lCode;
            res.data[0]=tNumb;
        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
            return res;
        }
        return res;
    }
    /**
     * Метод, с помощью которого произвольный узел кластера получает данные для счета с управляющего.
     * @param userID ID пользователя
     * @param taskID ID задачи
     * @return QueryResult.resultState - успешность\неудача запроса,
     * QueryResult.data - данные для счета (в случае успешного запроса)
     */
    public QueryResult getTaskDataForUser(Integer userID, Integer taskID){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromNodeToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_GET_DATA_FOR_CALC));
            oos.writeObject(userID);
            oos.writeObject(taskID);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            res.resultState=(Integer)ois.readObject();
            res.data=(Object[])ois.readObject();
        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
            return res;
        }
        return res;
    }

     /**
     * Метод, с помощью которого производится сохранение результата счета на управляющем узле в файл result.ser.
     * Данные с результатом счета приходят от произвольного узла кластера.
     * @param userID ID пользователя
     * @param taskID ID задачи
     * @param data результат счета
     * @return QueryResult.resultState - успешность\неудача запроса,
     */

    public QueryResult saveCalcResultOnRootNode(Integer userID, Integer taskID, Object []data){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromNodeToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_SAVE_CALC_RESULT));
            oos.writeObject(userID);
            oos.writeObject(taskID);
            oos.writeObject(data);
            oos.flush();

        }catch (Exception e){            
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        res.resultState=AlgorithmsConfig.RES_SUCCESS;
        return res;
    }
    
    public QueryResult sendMessageAboutFinish(Integer userID, Integer taskID){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromNodeToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_SEND_MESSAGE_ABOUT_FINISH));
            oos.writeObject(userID);
            oos.writeObject(taskID);            
            oos.flush();

        }catch (Exception e){            
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        res.resultState=AlgorithmsConfig.RES_SUCCESS;
        return res;
    }

     /**
     * Метод, с помощью которого web сервер mathpar получает результат счета для заданной задачи.
     * @param userID ID пользователя
     * @param taskID ID задачи
     * @return QueryResult.resultState - успешность\неудача запроса,
     * QueryResult.data - данные с результатом (в случае успешного запроса)
     */
    public QueryResult recvResultForTaskFromWeb(Integer userID, Integer taskID){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromWebPartToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_SEND_CALC_RESULT_TO_WEB));
            oos.writeObject(userID);
            oos.writeObject(taskID);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            res.resultState=(Integer)ois.readObject();
            if (res.resultState==AlgorithmsConfig.RES_SUCCESS){
                res.data=(Object[])ois.readObject();
            }

        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
        }
        return res;
    }
    /**
     * Проверка состояния задачи
     * @param userID ID пользователя
     * @param taskID ID задачи
     * @return QueryResult.resultState -успешность\неудача запроса
     * QueryResult.data int - константа из AlgorithmsConfig, описывающая состояние задачи
     */
    public QueryResult getStatusForTask(Integer userID, Integer taskID){
        QueryResult res=new QueryResult();
        res.resultState=initConnectionFromWebPartToCluster();
        if (res.resultState!=AlgorithmsConfig.RES_SUCCESS){
            return res;
        }
        try {
            oos.writeObject(new Integer(AlgorithmsConfig.QS_GET_STATE));
            oos.writeObject(userID);
            oos.writeObject(taskID);
            oos.flush();

        }catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_SEND_DATA_ERROR;
            return res;
        }
        try {
            res.resultState=AlgorithmsConfig.RES_SUCCESS;
            res.data=(Object[])ois.readObject();
        }
        catch (Exception e){
            res.resultState=AlgorithmsConfig.RES_RECV_RESULT_ERROR;
        }
        return res;
    }
}
