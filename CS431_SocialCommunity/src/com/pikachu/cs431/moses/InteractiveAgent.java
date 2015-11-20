package com.pikachu.cs431.moses;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.PublicKey;

import moses.member.Agent;
import moses.member.Member;
import moses.member.Receiver;
import moses.security.LGICert;
import moses.security.Secu;
import moses.security.certCreation;
import moses.util.Const;

public class InteractiveAgent implements Agent{
    public void run (String[] args) throws Exception { 
        if(args.length < 4) {
            System.out.println("> java InteractiveAgent controller_name controller_port law_path agent_name");
            System.out.println("> java InteractiveAgent controller_name controller_port law_path agent_name ca_public_key");
            System.out.println("> java InteractiveAgent controller_name controller_port law_path agent_name ca_public_key my_cert my_private_key");
            return;
        }
        
        String controller_name = args[0];
        String controller_port = args[1];
        String law_path = args[2];
        String agent_name = args[3];
        String raw_ca_public_key = null;
        String my_cert = null;
        String my_private_key = null;
        PublicKey ca_public_key = null;
        Member member = null;

        if (args.length == 5) {
            raw_ca_public_key = args[4];
        }

        if (args.length == 7) {
            raw_ca_public_key = args[4];
            my_cert = args[5];
            my_private_key = args[6];
        }

        if (raw_ca_public_key != null) {
            ca_public_key = certCreation.getPublicKey(raw_ca_public_key);
        }
        else {
            ca_public_key = null;
        }

        @SuppressWarnings("resource")
		FileInputStream law_stream  = new FileInputStream(law_path);
        byte[] law_byte = new byte[law_stream.available()];
        law_stream.read(law_byte);
        String law_content = new String(law_byte);


        if (ca_public_key == null) {
            member = new Member(law_content, Const.IMM_LAW, controller_name, Integer.parseInt(controller_port), agent_name);
        }
        else {
            member = new Member(law_content, Const.IMM_LAW, controller_name, Integer.parseInt(controller_port), agent_name, ca_public_key);
        }


        if (my_cert != null) {
            LGICert cert = certCreation.getCert(my_cert);
            PrivateKey private_key = certCreation.getPrivateKey(my_private_key);
            byte[] sign = null;
            sign = Secu.signSelfCertificate(cert, private_key);
            member.addCertificate(cert, sign);
        } 


        System.out.println(member.adopt("mypassword","someargument"));

        /* Ready to receive message */

        new Receiver(this, member).start();

        System.out.println("Type exit to quit");

        @SuppressWarnings("unused")
		String send_error = "send(msg,dest) -- not understood, try again";
        
        while (true) {
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String raw_command  = in.readLine();
                if (raw_command.equals("exit")) {
                    member.close();
                    System.out.println("The Agent is shutting down...");
                    System.exit(0);
                }
                
                String[] parts = raw_command.split(" ", 2);

                String destination = parts[0];
                String message = parts[1];

                member.send_lg(message, destination);

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void processRequest(Member member, String message, String destination) {
        System.out.println("Sent to " + destination + ": " + message);
    }

    public void processReply(Member member, String reply) {
        System.out.println("Received: " + reply);
    }

    public void processReply(Member member, byte[] breply) {
        System.out.println("Received: " + breply);
    }

    public void processReply(Member member, Object oreply) {
        System.out.println("Received: " + oreply);
    }

    public static void main (String[] args) throws Exception { 
        InteractiveAgent agent = new InteractiveAgent();
        agent.run(args);
    }
}