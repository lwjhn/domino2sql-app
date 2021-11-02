package com.lwjhn.domino2sql;

import com.lwjhn.domino.BaseUtils;
import com.lwjhn.domino2sql.config.ArcConfig;
import com.lwjhn.util.CloseableBase;
import lotus.domino.NotesFactory;
import lotus.domino.Session;

import java.io.File;
import java.util.Scanner;

public class DiiopApplication {
    public static void main(String[] args) {
        //java -DDominoHost="192.168.211.53:63148" -DDominoUser="Admin" -DDominoPassword="Fjsft_123" -DDominoPath="./arc.sql.config.sft.json" -DDominoOutput=./arc.sql.config.output.json -jar ./domino2sql-app.jar
        String host, user, password, path, output = null;
        if ((host = System.getProperty("DominoHost")) == null) {
            throw new RuntimeException("domino host is null !");
        }
        if ((user = System.getProperty("DominoUser")) == null) {
            throw new RuntimeException("domino user is null !");
        }
        if ((password = System.getProperty("DominoPassword")) == null) {
            throw new RuntimeException("domino password is null !");
        }
        if ((path = System.getProperty("DominoPath")) == null) {
            path = "./arc.sql.config.json";
        }
        if ((output = System.getProperty("DominoOutput")) == null) {
            output = "./arc.sql.config.output.json";
        }
        System.out.printf("java -DDominoHost=\"%s\" -DDominoUser=\"%s\" -DDominoPassword=\"%s\" -DDominoPath=\"%s\" -DDominoOutput=\"%s\" -jar ./domino2sql-app.jar\n",
                host, user, password, path, output);
        Session session = null;
        ArcConfig config = null;
        Action action = null;
        try {
            System.out.println("ArchXC Agent Start . ");
            System.out.println("config file path : " + new File(path).getCanonicalPath());

            System.out.printf("connect to host(%s) , user(%s), password(%s) \n", host, user, password);
            session = NotesFactory.createSession(host, user, password);
            System.out.println("connected ....");

            config = ArcConfigSerialization.parseArcConfig(new File(path));    //(new File(this.getClass().getResource("./arc.sql.config.json").getFile()));
            action = new Action(config, session, 1000, 1000);
            System.out.println("Authority : " + Domino2SqlHelp.join(session.evaluate("@UserName"), "; ", null));
            action.archive();
            action = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (config != null) {
                    ArcConfigSerialization.toJSONFile(config, output);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            CloseableBase.close(action);
            BaseUtils.recycle(session);
            System.out.println("ArchXC Agent ShutDown . ");
        }
        Scanner scan = new Scanner(System.in);
        System.out.println("press any character to shut down .");
        boolean flag = scan.hasNext();
        scan.close();
    }
}
